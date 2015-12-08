import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.Random;

public class BagScan extends UntypedActor{
	private final ActorRef security;
	private final int id;
	private Random rand;
	private final int failPercentage = 20;
	private final int failMax = 100;
	
	public BagScan(ActorRef sec, int id) {
		this.security = sec;
		this.id = id;
		rand = new Random();
	}
	
	@Override
	public void onReceive(Object m) throws Exception {
		if(m instanceof MessageStartDay){
			System.out.println("BagScan " + id + " received start day message.");
		} else if(m instanceof MessageEndDay){
			System.out.println("BagScan " + id + " received end day message.");
			System.out.println("BagScan " + id + " sending end day message to Security.");
			
			security.tell(m);
			context().stop();
			
		} else if(m instanceof MessageSendPassenger){
			System.out.println("BagScan " + id + " receives passenger " + ((MessageSendPassenger) m).getPassenger().getID());
			if(rand.nextInt(failMax)>=failPercentage){
				System.out.println("BagScan " + id + " fails the inspection of " + ((MessageSendPassenger) m).getPassenger().getID());
				MessageScannedPassenger msp = new MessageScannedPassenger(((MessageSendPassenger) m).getPassenger(), false);
				System.out.println("BodyScan " + id + " sending " + ((MessageSendPassenger) m).getPassenger().getID() + " to Security");
				security.tell(msp);
			}else{
				System.out.println("BagScan " + id + " passes the inspection of " + ((MessageSendPassenger) m).getPassenger().getID());
				MessageScannedPassenger msp = new MessageScannedPassenger(((MessageSendPassenger) m).getPassenger(), true);
				System.out.println("BodyScan " + id + " sending " + ((MessageSendPassenger) m).getPassenger().getID() + " to Security");
				security.tell(msp);
			}
		}
	}

}
