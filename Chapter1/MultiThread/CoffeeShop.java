import java.lang.*;
import java.util.*;

public class CoffeeShop {
    public static void main(String arg[]){
	Counter counter = new Counter();
	ShopMaster master = new ShopMaster(counter);
	CoffeeDrinker itota  = new CoffeeDrinker(counter,"itota");
	CoffeeDrinker fukuta = new CoffeeDrinker(counter,"fukuta");
	CoffeeDrinker hatto = new CoffeeDrinker(counter,"hatto");
	master.start();
	itota.start();
	fukuta.start();
	hatto.start();
    }
}

class Counter {
    Vector coffees;

    Counter(){
	coffees = new Vector();
    }

    public synchronized void getCoffee(String name)
	throws InterruptedException {
	    // だれか一人を起こす瞬間に，他のだれかがコーヒーを
	    // 持って行ってしまう可能性があるので while
	    while(coffees.size() == 0){
		System.out.println(name +" can NOT drink a COFFEE!");
		wait();
	    }
	    coffees.removeElementAt(0);
	    System.out.println(name +" can drink a COFFEE!");
	    System.out.println(coffees.toString());
	    
	    if(coffees.size() == 4) notify();
    }

    public synchronized void putCoffee()
	throws InterruptedException {

	    coffees.addElement(new String("Coffee"));
	    if(coffees.size() > 4){
		System.out.println("It's AKAJI!");
		wait();
	    }
	    System.out.println("Master made a COFFEE");
	    System.out.println(coffees.toString());
	
	if(coffees.size() == 1) notify();
    }
}

class ShopMaster extends Thread {
    Counter counter;
    ShopMaster(Counter theCounter){
	this.counter = theCounter;
    }
    public void run(){
	while(true){
	    try{
		counter.putCoffee();
		// コーヒーをぼちぼち作る．
		Thread.sleep((int)(3000 * Math.random()));
	    } catch(InterruptedException e){}
	}
    }
}

class CoffeeDrinker extends Thread {
    Counter counter;
    String name;
    CoffeeDrinker(Counter theCounter,String theName){
	this.counter = theCounter;
	this.name    = theName;
    }
    public void run(){
	while(true){
	    try{
		counter.getCoffee(this.name);
		// コーヒーをぼちぼち飲む．
		Thread.sleep((int)(10000 * Math.random()));
	    } catch(InterruptedException e){}
	}
    }
}
