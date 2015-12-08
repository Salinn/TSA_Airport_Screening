import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.LinkedList;

public class Queue extends UntypedActor{
	private final ActorRef bodyScan;
	private final ActorRef bagScan;	
	private final int ID;
	
	private java.util.Queue<Passenger> passengers = new LinkedList<Passenger>();
	private boolean scanReady = false;
	private boolean endDay = false;
	
	public Queue(ActorRef bodySc, ActorRef bagSc, int id) {
		bodyScan = bodySc;
		bagScan = bagSc;
		ID = id;
	}
	
	@Override
	public void onReceive(Object m) throws Exception {
		
		if(m instanceof MessageStartDay) {
			
			System.out.println("Security Queue " + ID + " received start day message.");
			
			// Send start day to body and bag scan
			bodyScan.tell(new MessageRespondToStartDay(this.getContext()));
			bagScan.tell(m);
			
			System.out.println("Security Queue " + ID + " sending start day message to body scan and bag scan " + ID + ".");
			
		} else if (m instanceof MessageEndDay) {
			
			System.out.println("Security Queue " + ID + " received end day message.");
			
			// Send end day to bag scan. Body scan gets it later
			bagScan.tell(m);
			endDay = true;
			
			System.out.println("Security Queue " + ID + " sending end day message to bag scan " + ID + ".");
			
			// Queue is empty, end of day, so start shutting down
			if (passengers.isEmpty() && scanReady) {
				
				MessageEndDay msg = new MessageEndDay();
				bodyScan.tell(msg);
				System.out.println("Security Queue " + ID + " sending end day message to body scan " + ID + ".");
				
				context().stop();
			}
			
		} else if (m instanceof MessageSendPassenger) {
			
			System.out.println("Security Queue " + ID + " recieved passenger " + ((MessageSendPassenger) m).getPassenger().getID() + ".");
			
			// Send passenger on to bag scan
			bagScan.tell(m);
			System.out.println("Security Queue " + ID + " sending passenger " + ((MessageSendPassenger) m).getPassenger().getID() + " to bag scan " + ID + ".");
			
			// Queue the passenger for body scan
			passengers.add(((MessageSendPassenger) m).getPassenger());
			if (!passengers.isEmpty() && scanReady) {
				
				// If ready, send them on
				sendPassengerToBodyScan();
				System.out.println("Security Queue " + ID + " sending passenger " + ((MessageSendPassenger) m).getPassenger().getID() + " to body scan " + ID + ".");
			}
			
		} else if (m instanceof MessageReady) {
			System.out.println("Security Queue " + ID + " recieved ready signal from body scan " + ID + ".");
			
			// Send next passenger if there is one
			if (!passengers.isEmpty()) {
				System.out.println("Security Queue " + ID + " sending passenger " + passengers.peek().getID() + " to body scan " + ID + ".");
				sendPassengerToBodyScan();
			} else {
				scanReady = true;
			}
			
			// Queue is empty, end of day, so start shutting down
			if (endDay && passengers.isEmpty() && scanReady) {
				
				MessageEndDay msg = new MessageEndDay();
				bodyScan.tell(msg);
				System.out.println("Security Queue " + ID + " sending end day message to body scan " + ID + ".");
				
				context().stop();
			}
			
		}
		
	}

	private void sendPassengerToBodyScan() {
		Passenger p = passengers.poll();
		MessageRespondToSendPassenger msg = new MessageRespondToSendPassenger(p, this.getContext());
		bodyScan.tell(msg);
		scanReady = false;
	}

}
