import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashMap;

public class Security extends UntypedActor{
	private final HashMap<Passenger, Boolean> scanResults;
	private final int id;
	private final ActorRef jail;
	private int scannerOffCount;

	public Security(int id, ActorRef jail) {
		this.id = id;
		this.jail = jail;
		this.scanResults = new HashMap<Passenger, Boolean>();
		scannerOffCount = 0;
	}

	@Override
	public void onReceive(Object m) throws Exception {
		if(m instanceof MessageScannedPassenger){
			MessageScannedPassenger msp = (MessageScannedPassenger) m;
			
			System.out.println("Security Station " + id
					+ " receives scan complete message with passenger "
					+ msp.getPassenger().getID() + ".");
			
			Boolean result = scanResults.get(msp.getPassenger());
			//Need to check if we have already received
			//the passenger's results from the other scan.
			if(result == null) {
				scanResults.put(msp.getPassenger(), msp.isPassed());
			} else {
				if(!result || !msp.isPassed()) {
					MessageSendPassenger mp =
						new MessageSendPassenger(msp.getPassenger());
					System.out.println("Security Station " + id
							+ " send passenger " + msp.getPassenger().getID()
							+ " to the Jail.");
					jail.tell(mp);
				} else {
					System.out.println("Passenger " + msp.getPassenger().getID() +
							" at Security Station " + id + " has left the security area.");
				}
			}
		} else if(m instanceof MessageEndDay) {
			System.out.println("Security Station " + id
					+ " receives end of day message " + (scannerOffCount + 1) + ".");
			scannerOffCount++;
			if(scannerOffCount == 2) {
				System.out.println("Security Station " + id +
						" is shutting down.");
				jail.tell(m);
				context().stop();
			}
		}
		
	}

}
