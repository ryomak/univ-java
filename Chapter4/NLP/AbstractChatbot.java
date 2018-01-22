
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.muc.*;
import org.xml.sax.SAXException;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.*;

/**
 * 通信や受信メッセージの処理など，チャットボットで汎用的に使う機能を実装した抽象クラス．
 */
public abstract class AbstractChatbot implements MessageListener, Runnable {

    /**
     * 形態素解析APIのURL
     */
    protected String maServURL;

    /**
     * 係り受け解析APIのURL
     */
    protected String daServURL;

    /**
     * XMPPサーバのURL
     * (ChatbotクラスのXMPP_SERVERで指定)
     */
    public String xmppServer;
    
    /**
     *  Yahoo Web API のアプリケーションID 
     * (ChatbotクラスのYAHOO_APP_IDで指定)
     */
    protected String yahooAppId;

    /**
     *  チャットボットの名前 
     * (ChatbotクラスのBOT_NAMEで指定)
     */
    protected String botName;

    /**
     * チャットボットのパスワード 
     * (ChatbotクラスのBOT_PASSで指定)
     */
    protected String botPass;
    
    /**
     * チャット履歴
     */
    protected List<Message> history;
    
    /**
     * あるチャットで最後に発言した時刻 (ミリ秒) を記録するMap
     */
    protected Map<Chat, Long> lastUttTimeMap;
    
    /**
     * 話題を振るまでに沈黙を耐える時間 (秒)
     */
    protected int silenceLimit = 60;
    
    /**
     * Proxyのホスト名
     */
    public String proxyHost;

    /**
     * Proxyのポート番号
     */
    public String proxyPort;

    protected SAXParser saxParser;
    protected SimpleDateFormat sdf;
    
    protected XMPPConnection conn;
    protected ChatManager chatMan;
    protected Thread botThread;
    
    protected Map<String,Chat> chatMap;
    protected Map<String,MultiUserChat> chatRoomMap;
    
   
    /**
     * コンストラクタ 
     */
    public AbstractChatbot(String yahooAppId, 
                           String botName, 
                           String botPass, 
                           String xmppServer,
                           String proxyHost,
                           String proxyPort) {
        this.yahooAppId = yahooAppId;
        this.botName = botName;
        this.botPass = botPass;
        this.xmppServer = xmppServer;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        maServURL = "http://jlp.yahooapis.jp/MAService/V1/parse?appid=" 
            + yahooAppId + "&response=surface,reading,pos,baseform,feature&sentence=";
        daServURL = "http://jlp.yahooapis.jp/DAService/V1/parse?appid="
            + yahooAppId + "&sentence=";
        if (proxyHost != null && ! proxyHost.isEmpty()) {
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", proxyPort);
        }
        conn = new XMPPConnection(new ConnectionConfiguration(xmppServer, 5222));
        chatMap = new HashMap<String, Chat>();
        chatRoomMap = new HashMap<String, MultiUserChat>();
        initSAXParser();
        sdf = new SimpleDateFormat("HH:mm:ss ");
        history = new ArrayList<Message>();
        lastUttTimeMap = new HashMap<Chat, Long>();
    }
    
    
    /**
     * 受信したメッセージを処理する
     * @param chat 
     * @param message 受信したメッセージ
     */
    public void processMessage(Chat chat, Message message) {
        history.add(message);
        String senderName = message.getFrom();
        System.out.println(sdf.format(new Date()) + 
            senderName+"からのメッセージ: "+message.getBody());
        recordUtteranceTime(chat);
        String resText = createResponse(senderName, message.getBody());
        try {
            // 0.5秒待つ
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // do nothing
        }
        // 応答メッセージを送る
        sendMessage(chat, resText);

    }
    
    
    /**
     * senderから来たメッセージに対する返答文を生成し，返す
     * @param sender メッセージの送信者 (アカウント名@XMPPサーバ名)
     * @param text メッセージ
     * @return 返答する文
     */
    public abstract String createResponse(String sender, String text);

    /**
     * target に向けて発信するメッセージの文を生成し，返す
     * @param target 宛先　(アカウント名@XMPPサーバ名)
     * @return 送信する文
     */
    public abstract String createMessage(String target);
    
    
    /**
     * SAXParser を初期化
     */
    private void initSAXParser() {
        try {
            saxParser = SAXParserFactory.newInstance().newSAXParser();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * ログイン
     */
    public void login() {
        try {
            conn.connect();
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            conn.login(botName, botPass);
            chatMan = conn.getChatManager();
            System.out.println(xmppServer + "に"+botName+"としてログインしました ");
        } catch (XMPPException e) {
            System.err.println("[AbstractChatbot#login] " +
                "XMPPサーバへのログインに失敗しました");
            e.printStackTrace();
        }
    }
    
    public Chat createChatWith(String targetName) {
        Chat chat = chatMan.createChat(targetName, this);
        chatMap.put(targetName, chat);
        return chat;
    }
    
    public Chat getChatWith(String targetName) {
        Chat chat = chatMap.get(targetName);
        if (chat != null) {
            return chat;
        } else {
            chat = createChatWith(targetName);
            return chat;
        }
    }
    
    /**
     * createMessageが生成した文をメッセージとして送る
     * @param targetName メッセージの送信先
     */
    public void sendMessage(String targetName) {
        sendMessage(targetName, createMessage(targetName));
    }
    
    /**
     * メッセージを送る
     * @param targetName メッセージの送信先
     * @param text メッセージ
     */
    public void sendMessage(String targetName, String text) {
        Chat chat = getChatWith(targetName);
        Message message = new Message(targetName);
        message.setBody(text);
        try {
            chat.sendMessage(message);
            recordUtteranceTime(chat);
            System.out.println(sdf.format(new Date()) +
                targetName + "へメッセージを送信: " + text);
        } catch (XMPPException e) {
            System.err.println("[AbstractChatbot#sendMessage] " +
                targetName + "へのメッセージ送信に失敗しました: " + text);
        }
    }

    /**
     * メッセージを送る
     * @param chat 送信するチャット
     * @param text メッセージ
     */
    public void sendMessage(Chat chat, String text) {
        String targetName = chat.getParticipant();
        Message message = new Message(targetName);
        message.setBody(text);
        try {
            chat.sendMessage(message);
            recordUtteranceTime(chat);
            System.out.println(sdf.format(new Date()) +
                targetName + "へメッセージを送信: " + text);
        } catch (XMPPException e) {
            System.err.println("[AbstractChatbot#sendMessage] " +
                targetName + "へのメッセージ送信に失敗しました: " + text);
        }
    }
    

    
    /**
     * Yahoo形態素解析の結果をMorphemeオブジェクトのリストにして返す
     * @param text 解析したい日本語の文
     * @return 形態素解析の結果
     */
    public List<Morpheme> analyzeMorpheme(String text) {
        try {
            String encoded = URLEncoder.encode(text, "UTF-8");
            URL url = new URL(maServURL + encoded);
            URLConnection conn = url.openConnection();
            MorphSAXHandler maHandler = new MorphSAXHandler();
            saxParser.parse(conn.getInputStream(), maHandler);
            return maHandler.getMorphemes();
        } catch (UnsupportedEncodingException e) {
            // nothing to do
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // nothing to do
            e.printStackTrace();
        } catch (SAXException e) {
            System.err.println("[AbstractChatBot#analyzeMorpheme] " +
            "Yahoo 形態素解析 Web API が返した解析結果XMLの解釈に失敗しました");
        } catch (IOException e) {
            System.err.println("[AbstractChatBot#analyzeMorpheme] " +
            "Yahoo 形態素解析 Web API への接続に失敗しました");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Yahoo形態素解析の結果のXMLをそのまま返す
     * @param text
     * @return 形態素解析結果のXMLそのまま
     */
    public String getYahooMorphemeAnalysisXML(String text) {
        StringBuffer sb = new StringBuffer();
        try {
            String encoded = URLEncoder.encode(text, "UTF-8");
            URL url = new URL(maServURL + encoded);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
   
    /**
     * Yahoo係り受け解析の結果のXMLをそのまま返す
     * @param text
     * @return 係り受け解析結果のXMLそのまま
     */
    public String getYahooDependencyAnalysisXML(String text) {
        StringBuffer sb = new StringBuffer();
        try {
            String encoded = URLEncoder.encode(text, "UTF-8");
            URL url = new URL(daServURL + encoded);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Yahoo係り受け解析の結果をChunkDependencyTreeオブジェクトにして返す
     * @param text 解析したい日本語の文
     * @return 係り受け解析の結果
     */
    public Sentence analyzeDependency(String text) {
        try {
            String encoded = URLEncoder.encode(text, "UTF-8");
            URL url = new URL(daServURL + encoded);
            URLConnection conn = url.openConnection();
            DepSAXHandler depHandler = new DepSAXHandler();
            saxParser.parse(conn.getInputStream(), depHandler);
            return depHandler.getDependencyTree();
        } catch (UnsupportedEncodingException e) {
            // nothing to do
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // nothing to do
            e.printStackTrace();
        } catch (SAXException e) {
            System.err.println("[AbstractChatBot#analyzeDependency] " +
            "Yahoo 係り受け解析 Web API が返した解析結果XMLの解釈に失敗しました");
        } catch (IOException e) {
            System.err.println("[AbstractChatBot#analyzeDependency] " +
            "Yahoo 係り受け解析 Web API への接続に失敗しました");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 発話時刻を記録
     * @param name 発言者
     */
    protected void recordUtteranceTime(Chat chat) {
        lastUttTimeMap.put(chat, System.currentTimeMillis());
    }
    
    /**
     * 話題を振るまでに沈黙を耐える時間を設定
     * @param sec 秒数
     */
    public void setSilenceLimit(int sec) {
        silenceLimit = sec;
    }
    
    /**
     * Webからコンテンツを取得し，文字列を返す．
     * @param url 
     * @return HTML等のコンテンツの文字列
     */
    public String getWebContent(String url) {
        StringBuffer sb = new StringBuffer();
        try {
            URLConnection conn = new URL(url).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Webからコンテンツを取得し，文字列を返す．コンテンツの文字コードを指定する．
     * @param url URL
     * @param enc 文字コード
     * @return HTML等のコンテンツの文字列
     */
    public String getWebContent(String url, String enc) {
        StringBuffer sb = new StringBuffer();
        try {
            URLConnection conn = new URL(url).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), enc));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    

    
    /**
     * 
     */
    public void start() {
        if (botThread == null) { 
            botThread = new Thread(this);
            botThread.start();
        }
    }
    
    /**
     * 
     */
    public void run() {
        Thread current = Thread.currentThread();
        while (botThread == current){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e){
                // do nothing
            } finally {
                Iterator<Chat> i = chatMap.values().iterator();
                while (i.hasNext()) {
                    Chat chat = i.next();
                    if (isWhenToSay(chat)) {
                        sendMessage(chat.getParticipant());
                    }
                }
            }
        }
    }
    
    /**
     * このメソッドが呼び出された瞬間がメッセージを送信すべきタイミングならtrue, 
     * そうでなければfalseを返す
     * @param chat
     * @return メッセージを送信すべきタイミングが否か
     */
    protected boolean isWhenToSay(Chat chat) {
        Long lastUttTime = lastUttTimeMap.get(chat);
        if (lastUttTime == null) {
            recordUtteranceTime(chat);
            lastUttTime = lastUttTimeMap.get(chat);
        }
        // 最後の発言からの経過時間をチェック
        if (System.currentTimeMillis() - lastUttTime > silenceLimit * 1000) {
            return true;
        } else {
            return false;
        }
        
    }
    
}
