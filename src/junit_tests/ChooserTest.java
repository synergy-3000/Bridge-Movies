package junit_tests;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import converter.Card;
import converter.Card.Suit;
import converter.Deal;
import simpler_dealer.Chooser;

class ChooserTest {
	EnumMap<Suit, String> suits = new EnumMap<Suit, String> (Suit.class);
	
	void print(ArrayList<Card> cards, Chooser chooser) {
		System.out.println("Deal: ");
		System.out.println(suitString(Suit.SPADES, cards));
		System.out.println(suitString(Suit.HEARTS, cards));
		System.out.println(suitString(Suit.DIAMONDS, cards));
		System.out.println(suitString(Suit.CLUBS, cards));
		System.out.println("HCP: " + chooser.hcp(cards));
		
	}
	String suitString(Suit suit, ArrayList<Card> all) {
		return Deal.getSymbol(suit) + " " + Deal.formatSuit(
				all.stream()
				.filter(c->c.suit().equals(suit))
				.sorted((c1,c2) -> c1.compareTo(c2)));
	}
	/* Get a shuffled deck of 52 cards. nCards in deal 
	 * Returns a Chooser
	 */
	Chooser init(ArrayList<Card> deal, ArrayList<Card> deck, int nCards) {
		Chooser chooser = new Chooser();
		deck.addAll(Card.newDeck());
        Collections.shuffle(deck);
        List<Card> view = deck.subList(deck.size()-nCards, deck.size());
        deal.addAll(view);
        //view.clear();
        //ArrayList<Card> list = new ArrayList<Card>(deck);
        return chooser;
	}
	@Test
	void testPickArrayListIntEnumMapEnumMap() {
		ArrayList<Card> deal = new ArrayList<Card>();
		ArrayList<Card> deck = new ArrayList<Card>();
		Chooser chooser = init(deal, deck, 13);
		int spades = 4, hearts = 4, diamonds = 0, clubs = 0;
		EnumMap<Suit, Integer> min = new EnumMap<Suit, Integer>
			(Map.of(Suit.SPADES, spades, Suit.HEARTS, hearts, Suit.DIAMONDS, diamonds, Suit.CLUBS, clubs));
		EnumMap<Suit, Integer> max = new EnumMap<Suit, Integer>
			(Map.of(Suit.SPADES, 13, Suit.HEARTS, 13, Suit.DIAMONDS, 13, Suit.CLUBS, 13));
		System.out.println("Minimum Lengths: " + min.entrySet());
		System.out.println("Maximum Lengths: " + max.entrySet());
		ArrayList<Card> picked = chooser.pick(deck, 13, min, max);
		print(picked, chooser);
		System.out.println("Always 5 spades and 5 hearts: ");
		min.putAll(Map.of(Suit.SPADES, 5, Suit.HEARTS, 5, Suit.DIAMONDS, 0, Suit.CLUBS, 0));
		max.putAll(Map.of(Suit.SPADES, 5, Suit.HEARTS, 5, Suit.DIAMONDS, 13, Suit.CLUBS, 13));
		picked = chooser.pick(deck, 13, min, max);
		print(picked, chooser);
	}
	@Test
	void testBalancedArrayListCard() {
		ArrayList<Card> deal = new ArrayList<Card>();
		ArrayList<Card> deck = new ArrayList<Card>();
		Chooser chooser = init(deal, deck, 13);
		int n = 1;
		ArrayList<Card> balanced = chooser.balanced(deck);
		do {
			System.out.println("Balanced Hand: " + n++);
			print(balanced, chooser);
			balanced = chooser.balanced(deck);
			System.out.println("Balanced Hand: " + n++);
			print(balanced, chooser);
			balanced = chooser.balanced(deck);
			System.out.println("Balanced Hand: " + n++);
			print(balanced, chooser);
		} 
		while (n < 10);
		int hcp = 26;
		System.out.println("Choosing 26 cards with HCP = " + hcp);
		ArrayList<Card> cards = new ArrayList<Card>(deck.subList(deck.size()-26, deck.size()));
		chooser.setHCP(cards, deck, hcp);
		print(cards, chooser); 
		List<Card> view = cards.subList(13, 26);
		ArrayList<Card> hand1 = new ArrayList<Card>(view);
		view.clear();
		System.out.println("Hand1: ");
		print(hand1, chooser);
		System.out.println("Hand2: ");
		print(cards,chooser);
		
		
	}
	void randomness() {
		double count[] = {0, 0, 0, 0};
		for(int i=0; i<100000; i++) {
			count[(int)(Math.random()/0.25)] += 1;
		}
		Arrays.setAll(count, x->count[x]/1000.00);
		System.out.println("Count: " + Arrays.toString(count));
	}
	@Test
	void testSetHcpArrayListArrayListInt () {
		ArrayList<Card> deal = new ArrayList<Card>();
		ArrayList<Card> deck = new ArrayList<Card>();
		Chooser chooser = init(deal, deck, 13);
		int hcp = 4;
		System.out.println("setHCP = " + hcp);
		System.out.println("Deal: ");
		print(deal, chooser);
		System.out.println("HCP = " + chooser.hcp(deal));
		chooser.setHCP(deal, deck, hcp);
		System.out.println("Deal: ");
		print(deal, chooser);
		System.out.println("HCP = " + chooser.hcp(deal));
		
		System.out.println("Test Fail of setHcp");
		EnumMap<Suit, Integer> min = new EnumMap<Suit, Integer>
			(Map.of(Suit.SPADES, 13, Suit.HEARTS, 0, Suit.DIAMONDS, 0, Suit.CLUBS, 0));
		EnumMap<Suit, Integer> max = new EnumMap<Suit, Integer>
			(Map.of(Suit.SPADES, 13, Suit.HEARTS, 13, Suit.DIAMONDS, 13, Suit.CLUBS, 13));
		ArrayList<Card> hand = chooser.pick(deck, min);
		print(hand, chooser);
		System.out.println("Hand HCP: " + chooser.hcp(hand));
		hcp = 15;
		if (!chooser.setHCP(hand, deck, hcp)) {     // Impossible
			System.out.println("Failed to set HCP = " + hcp + " for deal: ");
			print(hand, chooser);
		}
		
	}
	@Test
	void testIncrease_hcpArrayListArrayListInt () {
		ArrayList<Card> deal = new ArrayList<Card>();
		ArrayList<Card> deck = new ArrayList<Card>();
		Chooser chooser = init(deal, deck, 13);
		deck.removeAll(deal);
		System.out.println("increase_hcp:");
		System.out.println("Deal: ");
		int hcp = chooser.hcp(deal);
		System.out.println("HCP: " + hcp);
		
		print(deal, chooser);
		System.out.println("Deck: ");
		print(deck, chooser);
		
		
		
		/*
		 * Max HCP
		 */
		int[] maxhcp = {0,4,7,9,10,10,10,10,10,10,10,10,10};
		int max = 0;
		for (Suit suit : Suit.values()) {
			max += maxhcp[chooser.suitlength(deal, suit)];
		}
		int inc = max - chooser.hcp(deal);
		System.out.println("increase by " + inc);
		while( chooser.hcp(deal) < hcp + inc) {
			chooser.increase_hcp(deal, deck, hcp + inc - chooser.hcp(deal));
		}
		System.out.println("Deal: ");
		System.out.println("HCP: " + chooser.hcp(deal));
		print(deal, chooser);
		System.out.println("Deck: ");
		print(deck, chooser);
		
	}
	@Test
	void testFindArrayListOfCardCard() {
		Chooser chooser = new Chooser();
		List<Card> deck  = Card.newDeck();
        Collections.shuffle(deck);
        List<Card> view = deck.subList(deck.size()-13, deck.size());
        ArrayList<Card> tofind = new ArrayList<Card>(view);
        view.clear();
        ArrayList<Card> list = new ArrayList<Card>(deck);
        System.out.println("Hand: ");
        print(tofind, chooser);
        System.out.println();
        System.out.println("Search List: ");
        print(list, chooser);
        System.out.println("Card <Card");
        Card result;
        for (Card card : tofind) {
        	result = chooser.find(list, card, 10);
        	System.out.printf("inc = %d %5s %s\n", 10, card, result);
        	result = chooser.find(list, card, 3);
        	System.out.printf("inc = %d %5s %s\n", 3, card, result);
        	result = chooser.find(list, card, 1);
        	System.out.printf("inc = %d %5s %s\n", 1, card, result);
        }
        System.out.println();
		//Arrays.asList(Card.valueOf(Rank.JACK, Suit.HEARTS),)
	}

	@Test
	void testFindArrayListOfCardCardInt() {
		Chooser chooser = new Chooser();
		List<Card> deck  = Card.newDeck();
        Collections.shuffle(deck);
        List<Card> view = deck.subList(deck.size()-10, deck.size());
        ArrayList<Card> tofind = new ArrayList<Card>(view);
        view.clear();
        ArrayList<Card> list = new ArrayList<Card>(deck);
        
        System.out.println("Card <Card");
        Card result;
        int inc = 3;
        System.out.println("inc: " + inc);
        for (Card card : tofind) {
        	result = chooser.find(list, card, inc);
        	System.out.printf("%5s %s\n",card, result);
        }
        System.out.println();
	}

	@Test
	void testFindArrayListOfCardCardPredicateOfCard() {
		fail("Not yet implemented");
	}

	@Test
	void testSamesuitCardCard() {
		fail("Not yet implemented");
	}

	@Test
	void testSamesuitCardSuit() {
		fail("Not yet implemented");
	}

	@Test
	void testHcp() {
		
		Chooser chooser = new Chooser();
		List<Card> deck  = Card.newDeck();
        Collections.shuffle(deck);
        ArrayList<Card> list = new ArrayList<Card>(deck);
        System.out.println("Total deck HCP: " + chooser.hcp(list));
	}

	@Test
	void testSuitlength() {
		
		Chooser chooser = new Chooser();
		List<Card> deck  = Card.newDeck();
        Collections.shuffle(deck);
        ArrayList<Card> list = new ArrayList<Card>(deck);
        for (Suit suit : Suit.values()) {
        	System.out.println(suit + " length = " + chooser.suitlength(list, suit));
        }
        System.out.println("Total deck HCP: " + chooser.hcp(list));
        //print(list);
	}

	@Test
	void testPick() {
		Chooser chooser = new Chooser();
		List<Card> deck  = Card.newDeck();
        Collections.shuffle(deck);
        ArrayList<Card> list = new ArrayList<Card>(deck);
        
        EnumMap<Suit, Integer> map = new EnumMap<Suit, Integer>(Map.of(Suit.SPADES,5,Suit.HEARTS,3,Suit.DIAMONDS,3,Suit.CLUBS,2));
        ArrayList<Card> chosen = chooser.pick(list, map);
        System.out.println("Suit lengths = 5, 3, 3, 2");
        print(chosen, chooser);
		/*
		 * for (Suit suit : Suit.values()) { System.out.println(suit + " : " +
		 * Arrays.toString(chosen.stream().filter(c->chooser.samesuit(c,suit)).toArray()
		 * )); }
		 */
	}

}
