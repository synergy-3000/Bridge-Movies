package movie;

import java.util.Deque;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import converter.Deal.Dir;
import movie.Bid.Tricks;
import movie.Bid.Trumps;
import movie.Bid.Type;

public class Bids {
	static EnumMap<Dir, Dir> nexttobid = new EnumMap<Dir, Dir>
		(Map.of(Dir.WEST, Dir.NORTH, Dir.NORTH, Dir.EAST, Dir.EAST, Dir.SOUTH, Dir.SOUTH, Dir.WEST));
	
	private static Map<Tricks, Map<Trumps, Bid>> table =
    	    new EnumMap<Tricks, Map<Trumps, Bid>>(Tricks.class);
	static {
	    for (Tricks tricks : Tricks.values()) {
	        Map<Trumps, Bid> trumpTable = new EnumMap<Trumps, Bid>(Trumps.class);
	        for (Trumps trumps : Trumps.values())
	        	trumpTable.put(trumps, new TrumpBid(tricks, trumps));
	        table.put(tricks, trumpTable);
	    }
	}
	public static Bid pass() {
        return Pass.PASS;
    }
	public static Bid doubleX() {
        return Double.DOUBLE;
    }
	public static Bid redouble() {
        return ReDouble.REDOUBLE;
    }
	public static Bid getBid(Tricks tricks, Trumps trumps) {
		return table.get(tricks).get(trumps);
	}
	/**
	 * 
	 * @param auction
	 * @return the last bid or null if there isn't one. pass, double & redouble are ignored.
	 */
	public static Bid lastBid(Deque<Bid> auction) {
		Bid ret = null, bid;
		Iterator<Bid> iter = auction.iterator();
		while (iter.hasNext() && ret == null) {
			bid = iter.next();
			ret = bid.type().equals(Type.BID) ? bid : null;
		}
		return ret;
	}
	public static boolean isfinished(Deque<Bid> auction) {
		final Type[] type = {Type.PASS, Type.PASS, Type.PASS };
		return compare(auction, type) && auction.size()>3;
	}
	/**
	 * 
	 * @param auction
	 * @param dealer
	 * @return The contract or null if the board was passed in.
	 */
	public static Contract getContract(Deque<Bid> auction, Dir dealer) {
		boolean doubled = false;
		boolean redoubled = false;
		Bid last = lastBid(auction);
		Dir declarer;
		Dir lastDir = null;
		Bid bid = null;
		Dir bidder = dealer;
		Dir E_W = null, N_S = null;
		if (last == null) return null;
		Trumps trumps = last.trumps();
		Iterator<Bid> iter = auction.descendingIterator();
		while (iter.hasNext()) {
			bid = iter.next();
			if (bid.type().equals(Type.BID) && bid.trumps().equals(trumps)) {
				if (E_W == null && (bidder.equals(Dir.WEST) || bidder.equals(Dir.EAST))) {
					E_W = bidder;
				}
				else if (N_S == null && (bidder.equals(Dir.NORTH) || bidder.equals(Dir.SOUTH))) {
					N_S = bidder;
				} 
			}
			if (bid == last) lastDir = bidder;
			if (lastDir != null && bid.type().equals(Type.DOUBLE)) doubled = true;
			if (lastDir != null && bid.type().equals(Type.REDOUBLE)) {
				redoubled = true;
				doubled = false;
			}
			bidder = nexttobid.get(bidder);
		}
		declarer = N_S;
		if (lastDir.equals(Dir.EAST) || lastDir.equals(Dir.WEST)) {
			declarer = E_W;
		}
		//System.out.println("Making contract: ");
		return new Contract(declarer,doubled,redoubled,last);
	}
    static class Pass implements Bid
    {
       static final Bid  PASS = new Pass();

		public Tricks tricks() { return Tricks.ZERO; }
		public Trumps trumps() { return Trumps.NOTRUMPS; }
		public Type type() { return Type.PASS; }
		public boolean isLegal(Deque<Bid> auction) { return true; }
		@Override
		public String toString() {
			return "Pass";
		}
		
    }
    
    static class Double implements Bid
    {
        @Override
		public String toString() {
			return "X";
		}
		static final Bid DOUBLE = new Double();
        
        Type[] type1 = { Type.PASS, Type.PASS, Type.BID }; 
        Type[] type2 = { Type.BID };
		public Tricks tricks() { return Tricks.ZERO; }
		public Trumps trumps() { return Trumps.NOTRUMPS; }
		public Type type() { return Type.DOUBLE; }
		public boolean isLegal(Deque<Bid> auction) { 
			return compare(auction, type2) || compare(auction, type1);
		}
    }
    static class ReDouble implements Bid
    {
        @Override
		public String toString() {
			return "XX";
		}
		static final Bid REDOUBLE = new ReDouble();
        
        Type[] type1 = { Type.PASS, Type.PASS, Type.DOUBLE }; 
        Type[] type2 = { Type.DOUBLE };
		public Tricks tricks() { return Tricks.ZERO; }
		public Trumps trumps() { return Trumps.NOTRUMPS; }
		public Type type() { return Type.REDOUBLE; }
		public boolean isLegal(Deque<Bid> auction) { 
			return compare(auction, type2) || compare(auction, type1);
		}
    }
    static class TrumpBid implements Bid {

		private Tricks tricks;
		private Trumps trumps;
		
		public TrumpBid(Tricks tricks, Trumps trumps) {
			this.tricks = tricks;
			this.trumps = trumps;
		}
		@Override
		public String toString() {
			return tricks().toString() + trumps().toString(); 
		}
		public Tricks tricks() {return tricks;}
		public Trumps trumps() {return trumps;}
		public Type type() {return Type.BID;}
		public boolean isLegal(Deque<Bid> auction) { 
			Bid last = lastBid(auction);
			if (last == null) return true;
			return last.tricks() == tricks ? trumps.compareTo(last.trumps()) > 0 : tricks.compareTo(last.tricks()) > 0;
		}
    	
    }
    /**
     * 
     * @param auction
     * @param type
     * @return true if the last bids were of Type[] type
     */
    public static boolean compare(Deque<Bid> auction, Type[] type) {
    	Iterator<Bid> iter = auction.iterator();
    	int i;
    	for (i=0; i<type.length && iter.hasNext(); i++) {
    		if (!type[i].equals(iter.next().type())) {
    			break;
    		}
    	}
    	return i == type.length;
    }
}
