import java.util.*;

class Node {
    String name;

    // 自分から出ていくリンク
    Vector departFromMeLinks;
    // 自分に入ってくるリンク
    Vector arriveAtMeLinks;

    Node(String theName){
	name = theName;
	departFromMeLinks = new Vector();
	arriveAtMeLinks   = new Vector();
    }

    public Vector getISATails(){
	Vector isaTails = new Vector();
	for(int i = 0 ; i < arriveAtMeLinks.size() ; i++){
	    Link theLink = (Link)arriveAtMeLinks.elementAt(i);
	    if("is-a".equals(theLink.getLabel())){
		isaTails.addElement(theLink.getTail());
	    }
	}
	return isaTails;
    }

    public void addDepartFromMeLinks(Link theLink){
	departFromMeLinks.addElement(theLink);
    }

    public Vector getDepartFromMeLinks(){
	return departFromMeLinks;
    }

    public void addArriveAtMeLinks(Link theLink){
	arriveAtMeLinks.addElement(theLink);
    }
    
    public Vector getArriveAtMeLinks(){
	return arriveAtMeLinks;
    }

    public String getName(){
	return name;
    }

    public String toString(){
	return name;
    }
}
