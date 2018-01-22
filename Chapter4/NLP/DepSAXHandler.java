
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;

/**
 * Yahoo形態素解析Web APIが返す解析結果XMLをChunkのリストにするクラス
 */
public class DepSAXHandler extends DefaultHandler {

    Morpheme curMorpheme;
    List<Chunk> chunks;
    Sentence sentence;
    Chunk curChunk;
    StringBuffer sb;
    boolean endOfChunks;

    public DepSAXHandler() {
        chunks = new ArrayList<Chunk>();
        endOfChunks = false;
    }

    public List<Chunk> getChunks() {
        return chunks;
    }

    public Sentence getDependencyTree() {
        return sentence;
    }
    
    public void endElement(String namespaceURI,
            String localName,
            String qName) {
        if (qName.equals("Result")) {
            endOfChunks = true;
            sentence = new Sentence(chunks);
        } else if (qName.equals("Chunk")) {
            chunks.add(curChunk);
        } else if (qName.equals("Id")) {
            curChunk.setId(Integer.parseInt(sb.toString()));
        } else if (qName.equals("Dependency")) {
            curChunk.setDependency(Integer.parseInt(sb.toString()));
        } else if (qName.equals("Morphem")) {
            curChunk.addMorpheme(curMorpheme);
        } else if (qName.equals("Surface")) {
            curMorpheme.setSurface(sb.toString());
        } else if (qName.equals("Reading")) {
            curMorpheme.setReading(sb.toString());
        } else if (qName.equals("POS")) {
            curMorpheme.setPos(sb.toString());
        } else if (qName.equals("Baseform")) {
            curMorpheme.setBaseform(sb.toString());
        } else if (qName.equals("Feature")) {
            curMorpheme.setFeature(sb.toString());
        }
    }

    public void characters(char[] ch, int start, int length) {
        if(sb != null) {
            sb.append(ch, start, length);
        }
    }

    public void startElement(String namespaceURI,
            String localName,
            String qName,
            Attributes atts) {
        if (endOfChunks) {
            return;
        }
        if (qName.equals("Chunk")) {
            curChunk = new Chunk();
        } else if (qName.equals("Morphem")) {
            curMorpheme = new Morpheme();
        } else if (qName.equals("Id") ||
                qName.equals("Dependency") ||
                qName.equals("Surface") ||
                qName.equals("Reading") ||
                qName.equals("POS") ||
                qName.equals("Baseform") ||
                qName.equals("Feature")) {
            sb = new StringBuffer(); 
        }
    }
}
