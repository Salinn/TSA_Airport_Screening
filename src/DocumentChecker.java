import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.Random;

public class DocumentChecker extends UntypedActor{

	private int current = 0;
	private ArrayList<ActorRef> queues = new ArrayList<ActorRef>();
	private Random r = new Random();
	
	public DocumentChecker(ArrayList<ActorRef> secQueues) {
		queues = secQueues;
	}
	
	@Override
	public void onReceive(Object m) throws Exception {
		
		if (m instanceof MessageStartDay) {
			
			for (int i = 0; i < queues.size(); i++) {
				queues.get(i).tell(m);	// Send start day message
			}
			
		} else if (m instanceof MessageEndDay) {
			
			for (int i = 0; i < queues.size(); i++) {
				queues.get(i).tell(m);	// Send end day message
			}
			
			context().stop();
			
		} else if (m instanceof MessageSendPassenger) {
			// Recieved passenger
			System.out.println("Document Check recieved passenger " + ((MessageSendPassenger) m).getPassenger().getID() + ".");
			
			if (r.nextDouble() > 0.2) {
				// Passenger can go on, so send them to next queue
				MessageSendPassenger msg = new MessageSendPassenger(((MessageSendPassenger) m).getPassenger());
				queues.get(current).tell(msg);
				System.out.println("Document Check sending passenger " + ((MessageSendPassenger) m).getPassenger().getID() + " to Security Queue " + current + ".");
				current = ((current + 1)  % queues.size());
				
			} else {
				// Passenger denied passage 
				System.out.println("Document Check sending passenger " + ((MessageSendPassenger) m).getPassenger().getID() + " away.");
			}
		}
		
	}

}
