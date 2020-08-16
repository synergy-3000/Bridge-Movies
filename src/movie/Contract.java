package movie;

import converter.Deal.Dir;
import movie.Bid.Tricks;
import movie.Bid.Trumps;

public class Contract {
	
	private Dir declarer;
	private boolean redoubled;
	private boolean doubled;
	private Bid bid;
	
	public Contract(Dir declarer, boolean doubled, boolean redoubled, Bid bid) {
		this.declarer=declarer;
		this.doubled=doubled;
		this.redoubled=redoubled;
		this.bid = bid;
	}
	public boolean isDoubled() {
		return doubled;
	}
	public boolean isRedoubled() {
		return redoubled;
	}
	public Dir declarer() {
		return declarer;
	}
	public Tricks tricks() {
		return bid.tricks();
	}
	public Trumps trumps() {
		return bid.trumps();
	}
	
	public String toString() {
		String sdbl = doubled ? "X" : (redoubled ? "XX" : "");
		return String.format("%s by %s", bid.toString() + sdbl, declarer.toString());
	}
}
