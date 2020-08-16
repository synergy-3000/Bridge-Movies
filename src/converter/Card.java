package converter;

import java.util.*;

public class Card implements Comparable<Card> {
	
    public enum Rank { DEUCE, THREE, FOUR, FIVE, SIX,
        SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }

    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

    private final Rank rank;
    private final Suit suit;
    private Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank rank() { return rank; }
    public Suit suit() { return suit; }
    public String toString() { return Deal.getSymbol(suit()) + " " + Deal.toString(this); }
    
    private static Map<Rank, Integer> hcp = new EnumMap<Rank, Integer>
    	(Map.of(Rank.ACE, 4, Rank.KING, 3, Rank.QUEEN, 2, Rank.JACK, 1 ));

    
    private static final List<Card> protoDeck = new ArrayList<Card>();
    
    private static Map<Suit, Map<Rank, Card>> table =
    	    new EnumMap<Suit, Map<Rank, Card>>(Suit.class);
	static {
	    for (Suit suit : Suit.values()) {
	        Map<Rank, Card> suitTable = new EnumMap<Rank, Card>(Rank.class);
	        for (Rank rank : Rank.values())
	            suitTable.put(rank, new Card(rank, suit));
	        table.put(suit, suitTable);
	    }
	}
	public int hcp() {
		return hcp.getOrDefault(rank(), 0);
	}
	// Highest cards first
	public int compareTo(Card c) {
	    int suitCompare = -suit.compareTo(c.suit);
	    return suitCompare != 0 ? suitCompare : -rank.compareTo(c.rank);
	}
	 
	public static Card valueOf(Rank rank, Suit suit) {
	    return table.get(suit).get(rank);
	}
	// Initialize prototype deck
    static {
        for (Suit suit : Suit.values())
            for (Rank rank : Rank.values())
                protoDeck.add(Card.valueOf(rank, suit));
    }
    public static ArrayList<Card> newDeck() {
        return new ArrayList<Card>(protoDeck); // Return copy of prototype deck
    }
}
