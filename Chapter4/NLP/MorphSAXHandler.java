
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;

/**
 * Yahoo形態素解析Web APIが返す解析結果XMLをMorphemeのリストにするクラス
 */
public class MorphSAXHandler extends DefaultHandler {

    List<Morpheme> morphs;
    Morpheme curMorph;
    StringBuffer sb;
    int totalCount;
    boolean endOfMorphs;

    public MorphSAXHandler() {
        morphs = new ArrayList<Morpheme>();
        endOfMorphs = false;
    }

    public List<Morpheme> getMorphemes() {
        return morphs;
    }

    public void endElement(String namespaceURI,
            String localName,
            String qName) {
        if (qName.equals("ma_result")) {
            endOfMorphs = true;
        } else if (qName.equals("word")) {
            morphs.add(curMorph);
        } else if (qName.equals("total_count")) {
            totalCount = Integer.parseInt(sb.toString());
        } else if (qName.equals("surface")) {
            curMorph.setSurface(sb.toString());
        } else if (qName.equals("reading")) {
            curMorph.setReading(sb.toString());
        } else if (qName.equals("pos")) {
            curMorph.setPos(sb.toString());
        } else if (qName.equals("baseform")) {
            curMorph.setBaseform(sb.toString());
        } else if (qName.equals("feature")) {
            curMorph.setFeature(sb.toString());
        }
    }

    public void characters(char[] ch, int start, int length) {
        sb.append(ch, start, length);
    }

    public void startElement(String namespaceURI,
            String localName,
            String qName,
            Attributes atts) {
        if (endOfMorphs) {
            return;
        }
        if (qName.equals("word")) {
            curMorph = new Morpheme();
        } else if (qName.equals("surface") ||
                qName.equals("reading") ||
                qName.equals("pos") ||
                qName.equals("baseform") ||
                qName.equals("feature") ||
                qName.equals("total_count")) {
            sb = new StringBuffer(); 
        }
    }
}
