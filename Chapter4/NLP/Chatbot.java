import java.util.*;
import java.util.regex.*;




/**
 * チャットボットのクラス
 */
public class Chatbot extends AbstractChatbot {
    
    // Yahoo Web API のアプリケーションID 
    // (自分で取得したアプリケーションIDに変更する)
    private static String YAHOO_APP_ID = "Y.6xN_K......";
    
    // チャットサーバのURL 
    // (XMPPサーバが動いているホスト名に変更する)
    public static String XMPP_SERVER = "localhost";
    
    // チャットボットのログイン名
    // (好きな名前に変更する)
    private static String BOT_NAME = "your_bot_name";
    
    // チャットボットのログインパスワード
    // (設定したパスワードに変更する)
    private static String BOT_PASS = "apple";
    
    // チャットボットと話すユーザのログイン名
    // (好きな名前に変更する)
    private static String USER_NAME = "your_name";

    // プロキシサーバのURL　(必要なら設定する)
    public static String PROXY_HOST = "";

    // プロキシサーバのポート番号　(必要なら設定する)
    public static String PROXY_PORT = "";
    
    // 
    /**
     * 最初に呼び出されるmainメソッド
     */
    public static void main(String[] args) throws Exception {
        // Chatbotクラスのオブジェクトを作り，スレッドを開始
        Chatbot chatbot = new Chatbot();
        chatbot.start();
        
        // 沈黙を耐える秒数をセット
        chatbot.setSilenceLimit(30);
        
        // チャットサーバへログイン
        chatbot.login();
        
        //chatbot.createChatWith(USER_NAME + "@" + XMPP_SERVER); 
        // 起動時にcreateMessageで生成したメッセージを送る
        chatbot.sendMessage(USER_NAME + "@" + XMPP_SERVER);  
    }
    
    /**
     * senderから来たメッセージに対する返答文を生成し，返す
     * @param sender メッセージの送信者 (アカウント名@チャットサーバ名)
     * @param text メッセージ
     * @return 返答する文
     */
    public String createResponse(String sender, String text) {
        // 形態素解析
        List<Morpheme> morphs = analyzeMorpheme(text);
        System.out.println("形態素解析の結果: "+ morphs);
        
        // 係り受け解析
        Sentence sentence = analyzeDependency(text);
        System.out.println("係り受け解析の結果:\n"+ sentence);
 
        // 最も単純な実装例: オウム返し
        String senderName = sender.substring(0, sender.indexOf("@"));
        return senderName + "さんが「" + text + "」と言った";
    }

    /**
     * target に向けて発信するメッセージの文を生成し，返す
     * @param target 宛先　(アカウント名@チャットサーバ名)
     * @return 送信する文
     */ 
    public String createMessage(String target) {
        // 最も単純な実装例: 呼びかけるだけ
        String targetName = target.substring(0, target.indexOf("@"));
        return targetName + "さん、こんにちは";
    }	

    /**
     * コンストラクタ
     */
    public Chatbot() {
        super(YAHOO_APP_ID, BOT_NAME, BOT_PASS, XMPP_SERVER, PROXY_HOST, PROXY_PORT);
    }


}
