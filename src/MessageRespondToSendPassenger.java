import akka.actor.ActorRef;

public class MessageRespondToSendPassenger {
	
	private final Passenger p;
	private final ActorRef r;

	public MessageRespondToSendPassenger(Passenger passenger, ActorRef response) {
		p = passenger;
		r = response;
	}

	public Passenger getPassenger() {
		return p;
	}

	public ActorRef getResponseActor() {
		return r;
	}
}
