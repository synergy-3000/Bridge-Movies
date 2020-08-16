package converter;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import converter.Card.Suit;

public class Hand {
	private EnumMap<Suit, Card[]> suits;
	
	public Hand(Card[] cards) {
		suits = new EnumMap<Suit, Card[]>(Suit.class);
		for(Suit suit : Suit.values()) {
			suits.put(suit, Arrays.stream(cards).filter(c -> c.suit().equals(suit)).toArray(Card[]::new));
		}
	}
	public Hand(Card[] spades, Card[] hearts, Card[] diamonds,Card[] clubs) {
		suits = new EnumMap<Suit,Card[]>(Map.of(Suit.SPADES, spades, Suit.HEARTS, hearts, Suit.DIAMONDS, diamonds, Suit.CLUBS, clubs));
	}
	public Hand(EnumMap<Suit, Card[]> map) {
		suits = new EnumMap<Suit, Card[]>(map);
	}
	public Card[] getSuit(Suit suit) {
		return suits.get(suit);
	}
}
