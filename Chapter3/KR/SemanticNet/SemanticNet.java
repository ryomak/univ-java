import java.util.*;

/***
 * 意味ネットワーク (Semantic Net)
 *
 */
public class SemanticNet {
    Vector links;
    Vector nodes;
    Hashtable nodesNameTable;
    
    SemanticNet(){
	links = new Vector();
	nodes = new Vector();
	nodesNameTable = new Hashtable();
    }

    public void query(Vector theQueries){
	System.out.println("*** Query ***");
	for(int i = 0 ; i < theQueries.size() ; i++){
	    System.out.println(((Link)theQueries.elementAt(i)).toString());
	}
	System.out.println((doQuery(theQueries)).toString());
    }

    public Vector doQuery(Vector theQueries){
	Vector bindingsList = new Vector();
	for(int i = 0 ; i < theQueries.size() ; i++){
	    Link theQuery = (Link)theQueries.elementAt(i);
	    Vector bindings = queryLink(theQuery);
	    if(bindings.size() != 0){
		bindingsList.addElement(bindings);
	    } else {
		//失敗したとき
		return (new Vector());
	    }
	}
	return join(bindingsList);
    }

    public Vector queryLink(Link theQuery){
	Vector bindings = new Vector();
	for(int i = 0 ; i < links.size() ; i++){
	    Link theLink = (Link)links.elementAt(i);
	    Hashtable binding = new Hashtable();
	    String theQueryString = theQuery.getFullName();
	    String theLinkString  = theLink.getFullName();
	    if((new Matcher()).
	       matching(theQueryString,theLinkString,binding)){
		bindings.addElement(binding);
	    }
	}
	return bindings;
    }

    public Vector join(Vector theBindingsList){
	int size = theBindingsList.size();
	switch(size){
	    case 0:
		// 失敗している時？
		break;
	    case 1:
		return (Vector)theBindingsList.elementAt(0);
	    case 2:
		Vector bindings1 = (Vector)theBindingsList.elementAt(0);
		Vector bindings2 = (Vector)theBindingsList.elementAt(1);
		return joinBindings(bindings1,bindings2);
	    default:
		bindings1 = (Vector)theBindingsList.elementAt(0);
		theBindingsList.removeElement(bindings1);
		bindings2 = join(theBindingsList);
		return joinBindings(bindings1,bindings2);
	}
	// ダミー
	return (Vector)null;
    }

    public Vector joinBindings(Vector theBindings1,Vector theBindings2){
	Vector resultBindings = new Vector();
	for(int i = 0 ; i < theBindings1.size() ; i++){
	    Hashtable theBinding1 = (Hashtable)theBindings1.elementAt(i);
	    for(int j = 0 ; j < theBindings2.size() ; j++){
		Hashtable theBinding2 = (Hashtable)theBindings2.elementAt(j);
		Hashtable resultBinding =
		    joinBinding(theBinding1,theBinding2);
		if(resultBinding.size()!=0){
		    resultBindings.addElement(resultBinding);
		}
	    }
	}
	return resultBindings;
    }

    public Hashtable joinBinding(Hashtable theBinding1,Hashtable theBinding2){
	Hashtable resultBinding = new Hashtable();
	//System.out.println(theBinding1.toString() + "<->" + theBinding2.toString());
	// theBinding1 の key & value をすべてコピー
	for(Enumeration e = theBinding1.keys() ; e.hasMoreElements();){
	    String key = (String)e.nextElement();
	    String value = (String)theBinding1.get(key);
	    resultBinding.put(key,value);
	}
	// theBinding2 の key & value を入れて行く，競合があったら失敗
	for(Enumeration e = theBinding2.keys() ; e.hasMoreElements();){
	    String key = (String)e.nextElement();
	    String value2 = (String)theBinding2.get(key);
	    if(resultBinding.containsKey(key)){
		String value1 = (String)resultBinding.get(key);
		//System.out.println("=>"+value1 + "<->" + value2);
		if(!value2.equals(value1)){
		    resultBinding.clear();
		    break;
		}
	    }
	    resultBinding.put(key,value2);
	}
	return resultBinding;
    }

    /***
     * 例: Ito  =is-a=>  NIT-student:
     *     tail : Ito,
     *     head : NIT-student,
     *     label: is-a.
     */
    public void addLink(Link theLink){
	Node tail = theLink.getTail();
	Node head = theLink.getHead();
	links.addElement(theLink);

	// 性質の継承
 	if("is-a".equals(theLink.getLabel())){
 	    // head のすべてのリンクを is-a をたどってすべてのノードに継承．
 	    Vector tmp = new Vector();
 	    tmp.addElement(tail);
 	    recursiveInheritance(head.getDepartFromMeLinks(),tmp);
 	}
	// theLink を is-a をたどってすべてのノードに継承させる
	Vector tmp = new Vector();
	tmp.addElement(theLink);
	recursiveInheritance(tmp,tail.getISATails());

	
	// 関係を head と tail に登録．
	head.addArriveAtMeLinks(theLink);
	tail.addDepartFromMeLinks(theLink);
    }

    /***
     * theInheritLinks : 継承すべきリンク
     * theInheritNodes : 継承すべきリンクを継承するノード
     */
    public void recursiveInheritance(Vector theInheritLinks,
				     Vector theInheritNodes){
	for(int i = 0 ; i < theInheritNodes.size() ; i++){
	    Node theNode = (Node)theInheritNodes.elementAt(i);
	    // theNode 自体にリンクを継承．
	    for(int j = 0 ; j < theInheritLinks.size() ; j++){
		// theNode を tail にしたリンクを生成
		Link theLink = (Link)theInheritLinks.elementAt(j);
		Link newLink = new Link(theLink.getLabel(),
					theNode.getName(),
					(theLink.getHead()).getName(),
					 this);
		newLink.setInheritance(true);
		links.addElement(newLink);
		theNode.addDepartFromMeLinks(newLink);
	    }
	    // theNode から is-a でたどれるノードにリンクを継承
	    Vector isaTails = theNode.getISATails();
	    if(isaTails.size() != 0){
		recursiveInheritance(theInheritLinks,isaTails);
	    }
	}
    }


    public Vector getNodes(){
	return nodes;
    }
    
    public Hashtable getNodesNameTable(){
	return nodesNameTable;
    }

    public void printLinks(){
	System.out.println("*** Links ***");
	for(int i = 0 ; i < links.size() ; i++){
	    System.out.println(((Link)links.elementAt(i)).toString());
	}
    }

    public void printNodes(){
	System.out.println("*** Nodes ***");
	for(int i = 0 ; i < nodes.size() ; i++){
	    System.out.println(((Node)nodes.elementAt(i)).toString());
	}
    }
}


class Matcher {
    StringTokenizer st1;
    StringTokenizer st2;
    Hashtable vars;
    
    Matcher(){
	vars = new Hashtable();
    }

    public boolean matching(String string1,String string2,Hashtable bindings){
	this.vars = bindings;
	if(matching(string1,string2)){
	    return true;
	} else {
	    return false;
	}
    }
    
    public boolean matching(String string1,String string2){
	//System.out.println(string1);
	//System.out.println(string2);
	
	// 同じなら成功
	if(string1.equals(string2)) return true;
	
	// 各々トークンに分ける
	st1 = new StringTokenizer(string1);
	st2 = new StringTokenizer(string2);

	// 数が異なったら失敗
	if(st1.countTokens() != st2.countTokens()) return false;
		
	// 定数同士
	for(int i = 0 ; i < st1.countTokens();){
	    if(!tokenMatching(st1.nextToken(),st2.nextToken())){
		// トークンが一つでもマッチングに失敗したら失敗
		return false;
	    }
	}
	
	// 最後まで O.K. なら成功
	return true;
    }

    boolean tokenMatching(String token1,String token2){
	//System.out.println(token1+"<->"+token2);
	if(token1.equals(token2)) return true;
	if( var(token1) && !var(token2)) return varMatching(token1,token2);
	if(!var(token1) &&  var(token2)) return varMatching(token2,token1);
	return false;
    }

    boolean varMatching(String vartoken,String token){
	if(vars.containsKey(vartoken)){
	    if(token.equals(vars.get(vartoken))){
		return true;
	    } else {
		return false;
	    }
	} else {
	    vars.put(vartoken,token);
	}
	return true;
    }

    boolean var(String str1){
	// 先頭が ? なら変数
	return str1.startsWith("?");
    }

}
