package converter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import converter.Card.Rank;
import converter.Card.Suit;

public class Deal {
	public enum Dir { NORTH, SOUTH, EAST, WEST } // Seat direction

    public enum Vul { N_S, E_W, BOTH, NIL }   // Vulnerability
	
    private Hand north;      // Cards are unsorted
	private Hand south;
	private Hand east;
	private Hand west;
	private Vul vul;
	private Dir dealer;
	private StringBuffer[] bidsbyhand; // bidsbyhand[0] = all bids made by West, bidsbyhand[1] = all bids made by North, then East and finally South
	
	private static EnumMap<Rank, String> rankToString = new EnumMap<Rank, String>(Rank.class);
	private static String[] names = { "2","3","4","5","6","7","8","9","10","J","Q","K","A" };
	private static EnumMap<Suit, String> suitsymbols = new EnumMap<Suit, String>(Map.of(Suit.SPADES, "\u2660", Suit.HEARTS, "\u2665"
																				, Suit.DIAMONDS, "\u2666", Suit.CLUBS, "\u2663"));
	static {
		Rank vals[] = Rank.values();
		for (int i =0; i < vals.length; i++) {
			rankToString.put(vals[i], names[i]);
		}
	}
	public Deal(Hand north, Hand south, Hand east, Hand west, Vul vul, Dir dealer, StringBuffer[] bidsbyhand) {
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.vul = vul;
		this.dealer = dealer;
		this.bidsbyhand = bidsbyhand;
	}
	public Hand getNorth() {
		return north;
	}
	public Hand getSouth() {
		return south;
	}
	public Hand getEast() {
		return east;
	}
	public Hand getWest() {
		return west;
	}
	public Vul getVul() {
		return vul;
	}
	public Dir getDealer() {
		return dealer;
	}
	@Override
	public String toString() {
		String padl = " ".repeat(25);
		// Line 1-4
		int l = 3;
		String[] lines = new String[12];
		for (Suit suit : Suit.values()) {
			lines[l] = padl + formatforfulldeal(suit, north.getSuit(suit)) + padl; 
			lines[l+4] = formatforfulldeal(suit, west.getSuit(suit)) + padl + formatforfulldeal(suit, east.getSuit(suit));
			lines[l+8] = padl + formatforfulldeal(suit, south.getSuit(suit)) + padl; 
			l--;
		}
		return String.join("\n", lines);
	}
	public static String toString(Card c) {
		return rankToString.get(c.rank());
	}
	public static String formatSuit(Card[] cards) {
		return formatSuit(Stream.of(cards));
	}
	public static String formatSuit(List<Card> suit) {
		return formatSuit(suit.stream());
	}
	public static String getSymbol(Suit s) {
		return suitsymbols.get(s);
	}
	public static String formatSuit(Stream<Card> cardstream) {
		String ret = cardstream.map(card -> rankToString.get(card.rank())).collect(Collectors.joining(" "));
		 if (ret.isBlank()) ret = "-";
		 return  ret; // String.format("%-25s", ret);
	}
	private String formatforfulldeal(Suit s, Card[] cards) {
		return String.format("%-25s", getSymbol(s) + " " + formatSuit(cards));
	}
	public StringBuffer[] getBids() {
		return bidsbyhand;
	}
}
