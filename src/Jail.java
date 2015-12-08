import akka.actor.Actors;
import akka.actor.UntypedActor;

import java.util.ArrayList;

public class Jail extends UntypedActor{
	private int totalSecurityPoints;
	private ArrayList<Passenger> jailedPassengers;

	public Jail(int totalSecurityPoints) {
		this.totalSecurityPoints = totalSecurityPoints;
		this.jailedPassengers = new ArrayList<Passenger>();
	}

	@Override
	public void onReceive(Object m) throws Exception {
		if(m instanceof MessageSendPassenger) {
			MessageSendPassenger mp = (MessageSendPassenger) m;
			
			System.out.println("Passenger " + mp.getPassenger().getID()
					+ " has arrived at the Jail.");
			
			jailedPassengers.add(mp.getPassenger());
		} else if(m instanceof MessageEndDay) {
			System.out.println("Jail received end of day message.");
			
			totalSecurityPoints--;
			
			if(totalSecurityPoints == 0) {
				for(Passenger p : jailedPassengers) {
					System.out.println("Passenger " + p.getID()
							+ " was sent to detention center.");
				}
				System.out.println("The Jail is shutting down.");

				Actors.registry().shutdownAll();
			}
		}
	}

}
