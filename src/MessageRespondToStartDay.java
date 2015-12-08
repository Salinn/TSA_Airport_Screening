import akka.actor.ActorRef;

public class MessageRespondToStartDay {
	
	private final ActorRef r;

	public MessageRespondToStartDay(ActorRef respond) {
		r = respond;
	}
	
	public ActorRef getResponseActor() {
		return r;
	}
}

