
import java.util.*;

/**
 * •¶‘S‘Ì‚Ì•¶ß‚ÌƒŠƒXƒgD•¶ßŠÔ‚ÌŒW‚èó‚¯\‘¢‚ğ•Û‚·‚éƒNƒ‰ƒXD
 */
public class Sentence extends ArrayList<Chunk> {

    Chunk head;
    
    public Sentence(List<Chunk> chunks) {
        super(chunks);
        Iterator<Chunk> i = chunks.iterator();
        while (i.hasNext()) {
            Chunk chunk = i.next();  
            int dependency = chunk.getDependency();
            if (dependency == -1) {
                head = chunk;
            } else {
                Chunk depChunk = chunks.get(dependency);
                depChunk.addDependentChunk(chunk);
                chunk.setDependencyChunk(depChunk);
            }
        }
    }
    
    /**
     * å«‚Ì•¶ß‚ğ•Ô‚·
     * @return å«‚Ì•¶ß
     */
    public Chunk getHeadChunk() {
        return head;
    }
    
    /**
     * “®ìåŠi‚Ì•¶ß‚ğ•Ô‚·
     * @return “®ìåŠi‚Ì•¶ß
     */
    public Chunk getAgentCaseChunk() {
        // å«‚ÉŒW‚é•¶ß‚Ì’†‚©‚çƒKŠi‚Ì•¶ß‚ğ’T‚·
        Chunk cand = findChunkByHead(head.getDependents(), "•Œ", "‚ª");
        if (cand != null) return cand;
        // å«‚ÉŒW‚é•¶ß‚Ì’†‚©‚çƒnŠi‚Ì•¶ß‚ğ’T‚·
        cand = findChunkByHead(head.getDependents(), "•Œ", "‚Í");
        if (cand != null) return cand;
        // ‘S‚Ä‚Ì•¶ß‚Ì’†‚©‚çƒKŠi‚Ì•¶ß‚ğ’T‚·
        cand = findChunkByHead(this, "•Œ", "‚ª");
        if (cand != null) return cand;
        // ‘S‚Ä‚Ì•¶ß‚Ì’†‚©‚çƒnŠi‚Ì•¶ß‚ğ’T‚·
        cand = findChunkByHead(this, "•Œ", "‚Í");
        return cand;
    }
    
    /**
     * w’è‚µ‚½•iŒEŒ´Œ^‚ÌŒ`‘Ô‘f‚ğå«‚É‚Â•¶ß‚ğ•¶ßƒŠƒXƒg‚©‚ç’T‚µ‚Ä•Ô‚·
     * @param chunks •¶ßƒŠƒXƒg
     * @param pos ’T‚µ‚Ä‚Ä‚é•¶ß‚Ìå«Œ`‘Ô‘f‚Ì•iŒ
     * @param baseform ’T‚µ‚Ä‚¢‚é•¶ß‚Ìå«Œ`‘Ô‘f‚ÌŒ´Œ^
     * @return Œ©‚Â‚©‚Á‚½•¶ß
     */
    private Chunk findChunkByHead(List<Chunk> chunks, String pos, String baseform) {
        for (Iterator<Chunk> i = chunks.iterator(); i.hasNext(); ) {
            Chunk chunk = i.next();
            Morpheme head = chunk.getHeadMorpheme();
            if (pos.equals(head.getPos()) && baseform.equals(head.getBaseform())) {
                return chunk;
            }
        }
        return null;
    }
    
    /**
     * ‚±‚Ì•¶‚ÉŠÜ‚Ü‚ê‚é•¶ßŠÔ‚ÌŒW‚èó‚¯\‘¢‚ğ•\‚·XML•—‚Ì•¶š—ñ‚ğ•Ô‚·
     * @return XML•—‚Ì•¶š—ñ
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<Sentence head=\""+ head.getId() +"\">\n");
        for (Iterator<Chunk> i = iterator(); i.hasNext(); ) {
            sb.append(i.next().toString());
            sb.append("\n");
        }
        sb.append("</Sentence>");
        return sb.toString();
    }
    
}
