package movie;

import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

public interface Bid {
	
	public enum Tricks { ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN;
		
		public String toString() {
			int i=0; String ret = "";
			for (Tricks trick : Tricks.values()) {
				if(this.equals(trick)) {
					ret = Integer.toString(i);
					break;
				}
				i++;
			}
			return ret;
		}
	};

    public enum Type { PASS, DOUBLE, REDOUBLE, BID };
    
    public enum Trumps { CLUBS, DIAMONDS, HEARTS, SPADES, NOTRUMPS;
    	
    	static EnumMap<Trumps, String> strmap = new EnumMap<Trumps, String>
    		(Map.of(Trumps.CLUBS, "\u2663", Trumps.DIAMONDS, "\u2666"
    				, Trumps.HEARTS, "\u2665", Trumps.SPADES, "\u2660", Trumps.NOTRUMPS, "NT"));

    	public String toString() {
    		return strmap.get(this);
    	}
    
    };
	Tricks tricks();

	Trumps trumps();

	Type type();

	boolean isLegal(Deque<Bid> auction);
}