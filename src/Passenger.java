public class Passenger {

	private final int id;
	protected static int nextid = 0;
	
	public int getID() {
		return id;
	}

	public Passenger() {
		this.id = nextid;
		nextid++;
	}
}
