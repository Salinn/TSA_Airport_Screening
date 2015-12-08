import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.Random;

public class BodyScan extends UntypedActor{

	private final ActorRef security;
	private final int id;
	private Random random;
	private final int failPercentage = 20;
	private final int failMax = 100;
	
	public BodyScan(ActorRef sec, int id) {
		this.security = sec;
		this.id = id;
		r = new Random();
	}
	
	@Override
	public void onReceive(Object m) throws Exception {
		if(m instanceof MessageRespondToStartDay){
			System.out.println("BodyScan " + id + " received start day message.");
			((MessageRespondToStartDay) m).getResponseActor().tell(new MessageReady());
		} else if(m instanceof MessageEndDay){
			System.out.println("BodyScan " + id + " received end day message.");
			System.out.println("BodyScan " + id + " sending end day message to Security.");
			
			security.tell(m);
			context().stop();
		} else if(m instanceof MessageRespondToSendPassenger){
			System.out.println("BodyScan " + id + " receives passenger " + ((MessageRespondToSendPassenger) m).getPassenger().getID());
			if (r.nextInt(failMax)>=failPercentage) {
				System.out.println("BodyScan " + id + " fails the inspection of " + ((MessageRespondToSendPassenger) m).getPassenger().getID());
				MessageScannedPassenger msp = new MessageScannedPassenger(((MessageRespondToSendPassenger) m).getPassenger(), false);
				System.out.println("BodyScan " + id + " sending " + ((MessageRespondToSendPassenger) m).getPassenger().getID() + " to Security");
				security.tell(msp);
			} else {
				System.out.println("BodyScan " + id + " passes the inspection of " + ((MessageRespondToSendPassenger) m).getPassenger().getID());
				MessageScannedPassenger msp = new MessageScannedPassenger(((MessageRespondToSendPassenger) m).getPassenger(), true);
				System.out.println("BodyScan " + id + " sending " + ((MessageRespondToSendPassenger) m).getPassenger().getID() + " to Security");
				security.tell(msp);
			}
			((MessageRespondToSendPassenger) m).getResponseActor().tell(new MessageReady());
		}
	}

}
