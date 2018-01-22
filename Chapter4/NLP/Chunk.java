
import java.util.*;

/**
 * •¶ß‚ğ•\‚·ƒNƒ‰ƒX
 */
public class Chunk extends ArrayList<Morpheme> {

    /**
     * •¶ß”Ô†
     */
    int id;
    
    /**
     * ŒW‚èæ‚Ì•¶ß
     */
    Chunk dependencyChunk;
    
    /**
     * ŒW‚èæ‚Ì•¶ß”Ô†
     */
    int dependency;
    
    /**
     * ‚±‚Ì•¶ß‚ÉŒW‚é•¶ß‚ÌƒŠƒXƒg
     */
    List<Chunk> dependents;

    
    public Chunk() {
        super();
        dependents = new LinkedList<Chunk>();
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setDependency(int dependency) {
        this.dependency = dependency;
    }

    public void setMorphemes(List<Morpheme> morphs) {
        for (Iterator<Morpheme> i = iterator(); i.hasNext(); ) {
            remove(i.next());
        }
        addAll(morphs);
    }
    
    public void addMorpheme(Morpheme morph) {
        add(morph);
    }
    
    public void addDependentChunk(Chunk chunk) {
        dependents.add(chunk);
    }

    public void setDependencyChunk(Chunk chunk) {
        dependencyChunk = chunk;
    }

    /**
     * ‚±‚Ì•¶ß‚Ìå«‚ÌŒ`‘Ô‘f‚ğ“ü‚ê‘Ö‚¦‚é
     * @param morph ƒZƒbƒg‚·‚éŒ`‘Ô‘f
     */
    public void replaceHeadMorpheme(Morpheme morph) {
        set(size()-1, morph);
    }
    
    /**
     * ‚±‚Ì•¶ß‚Ì•¶ß”Ô†‚ğ•Ô‚·
     * @return •¶ß”Ô†
     */
    public int getId() {
        return id;
    }
    
    /**
     * ‚±‚Ì•¶ß‚ªŒW‚é•¶ß‚Ìid‚ğ•Ô‚·
     * @return ‚±‚Ì•¶ß‚ªŒW‚é•¶ß‚Ìid
     */
    public int getDependency() {
        return dependency;
    }

    /**
     * ‚±‚Ì•¶ß‚ªŒW‚é•¶ß‚ğ•Ô‚·
     * @return ‚±‚Ì•¶ß‚ªŒW‚é•¶ß
     */
    public Chunk getDependencyChunk() {
        return dependencyChunk;
    }
    
    /**
     * ‚±‚Ì•¶ß‚ÉŒW‚é•¶ß‚ÌƒŠƒXƒg‚ğ•Ô‚·
     * @return ‚±‚Ì•¶ß‚ÉŒW‚é•¶ß‚ÌƒŠƒXƒg
     */
    public List<Chunk> getDependents() {
        return dependents;
    }

    /**
     * å«‚Å‚ ‚éÅŒã‚ÌŒ`‘Ô‘f‚ğ•Ô‚·
     * @return å«‚Å‚ ‚éÅŒã‚ÌŒ`‘Ô‘f
     */
    public Morpheme getHeadMorpheme() {
        return get(size()-1);
    }
     
    /**
     * ‚±‚Ì•¶ß‚Ì•\‘w•¶š—ñ‚ğ•Ô‚·
     * @return ‚±‚Ì•¶ß‚Ì•\‘w•¶š—ñ
     */
    public String getSurface() {
        StringBuffer sb = new StringBuffer();
        for (Iterator<Morpheme> i = iterator(); i.hasNext(); ) {
            sb.append(i.next().getSurface());
        }
        return sb.toString();
    }
    
    /**
     * ‚±‚Ì•¶ß‚ÉŠÜ‚Ü‚ê‚éŒ`‘Ô‘f‚ÌƒŠƒXƒg‚ğ•\‚·XML•—‚Ì•¶š—ñ‚ğ•Ô‚·
     * @return XML•—‚Ì•¶š—ñ
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<Chunk id=\"" + id);
        sb.append("\" dependency=\"" + dependency);
        if (! dependents.isEmpty()) {
            sb.append("\" dependents=\"");
            for (Iterator<Chunk> i = dependents.iterator(); i.hasNext(); ) {
                sb.append(i.next().getId());
                if (i.hasNext()) {
                    sb.append(",");
                }
            }
        }
        sb.append("\">\n");
        for (Iterator<Morpheme> i = iterator(); i.hasNext(); ) {
            sb.append(" ");
            sb.append(i.next().toString());
            sb.append("\n");
        }
        sb.append("</Chunk>");
        return sb.toString();
    }
}
