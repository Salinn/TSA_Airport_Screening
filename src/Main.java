import akka.actor.*;

import java.util.ArrayList;

public class Main {
	private final static int TOTALSTATIONS = 4;
	private static ActorRef DocChecker;
	private final static int CYCLES = 100;

	public static void main(String[] args) {
		final ActorRef jail = Actors.actorOf(new UntypedActorFactory() {
			public UntypedActor create() {
				return new Jail(TOTALSTATIONS);
			}
		}).start();
		final ArrayList<ActorRef> lineQueues = new ArrayList<ActorRef>();
		for (int i = 0; i < TOTALSTATIONS; i++) {
			final int n = i;
			final ActorRef sec = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() { return new Security(n, jail);
				}
			}).start();
			final ActorRef bodyScan = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() { return new BodyScan(sec, n);
				}
			}).start();
			final ActorRef bagScan = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() { return new BagScan(sec, n);
				}
			}).start();
			final ActorRef lineQueue = Actors.actorOf(new UntypedActorFactory() {
				public UntypedActor create() { return new Queue(bodyScan, bagScan, n);
				}
			}).start();
			lineQueues.add(lineQueue);
		}
		DocChecker = Actors.actorOf(new UntypedActorFactory() {
			public UntypedActor create() {
				return new DocumentChecker(lineQueues);
			}
		}).start();
		startDay();
		cyclePassengers();
	}

	private static void startDay() {
		DocChecker.tell(new MessageStartDay());
		System.out.println("Main sending start day message to doc check");
	}

	private static void cyclePassengers() {
		for(int i = 0 ; i < CYCLES; i++) {
			final MessageSendPassenger passM = new MessageSendPassenger(new Passenger());
			DocChecker.tell(passM);
			System.out.println("System Main sending passenger " +
					(passM).getPassenger().getID() + " to Document Check.");
		}

		DocChecker.tell(new MessageEndDay());
		System.out.println("System Main sending end-of-day to Document Check.");
	}
}
