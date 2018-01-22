
/**
 * 形態素クラス
 */
public class Morpheme {

    /**
     * 表層的文字列
     */
    String surface;
    
    /**
     * 読み
     */
    String reading;
    
    /**
     * 品詞
     */
    String pos;
    
    /**
     * 原型
     */
    String baseform;
    
    /**
     * 特徴
     */
    String feature;

    /**
     * コンストラクタ
     */
    public Morpheme() {
        super();
    }

    /**
     * コンストラクタ
     */
    public Morpheme(String surface, String reading, String pos, String baseform) {
        this.surface = surface;
        this.reading = reading;
        this.pos = pos;
        this.baseform = baseform;
        feature = "";
    }
    
    /**
     * コピーコンストラクタ
     */
    public Morpheme(Morpheme morph) {
        this.surface = morph.surface;
        this.reading = morph.reading;
        this.pos = morph.pos;
        this.baseform = morph.baseform;
        this.feature = morph.feature;
    }


    
    /**
     * 動詞ならtrue, そうでなければfalseを返す
     * @return 動詞か否か
     */
    public boolean isVerb() {
        return pos.equals("動詞");
    }


    public void setSurface(String surface) {
        this.surface = surface;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setBaseform(String baseform) {
        this.baseform = baseform;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    /**
     * この形態素の表層的文字列を返す
     * @return 表層的文字列
     */
    public String getSurface() {
        return surface;
    }

    /**
     * この形態素の読みを返す
     * @return 読み
     */
    public String getReading() {
        return reading;
    }

    /**
     * この形態素の品詞を返す
     * @return 品詞
     */
    public String getPos() {
        return pos;
    }

    /**
     * この形態素の原型を返す
     * @return 原型
     */
    public String getBaseform() {
        return baseform;
    }

    /**
     * この形態素の特徴を合わせた文字列を返す
     * @return 特徴を合わせた文字列
     */
    public String getFeature() {
        return feature;
    }

    /**
     * この形態素の情報を全て合わせてXML的に表現した文字列を返す
     * @return 形態素情報を全て合わせてXML的に表現した文字列
     */
    public String toString() {
        return "<Morpheme surface=\""+surface+"\" reading=\""+reading+"\" pos=\""+pos+"\" baseform=\""+baseform+"\" feature=\""+feature+"\" />";
    }
}
