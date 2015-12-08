public class MessageScannedPassenger {
	private final Passenger p;
	private final boolean passed;

	public MessageScannedPassenger(Passenger passenger, boolean scanPassed) {
		p = passenger;
		passed = scanPassed;
	}

	public Passenger getPassenger() {
		return p;
	}

	public boolean isPassed() {
		return passed;
	}

	public boolean toJail() {
		return !passed;
	}
}
