package simpler_dealer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import converter.Card;
import converter.Card.Suit;

public class Chooser {
	
	List<List<Integer>> shapes = Arrays.asList(Arrays.asList(2,3,3,5)
											  ,Arrays.asList(2,3,4,4)
											  ,Arrays.asList(3,3,3,4)); // Balanced hand shapes
	EnumMap<Suit, Integer> lenMap = new EnumMap<Suit, Integer>(Suit.class);
	
	EnumMap<Suit, Integer> suitlens = new EnumMap<Suit, Integer>(Map.of(Suit.SPADES, 0, Suit.HEARTS, 0, Suit.DIAMONDS, 0, Suit.CLUBS, 0));
	static EnumMap<Suit, Integer> NoMax = new EnumMap<Suit, Integer>(Map.of(Suit.SPADES, 13, Suit.HEARTS, 13, Suit.DIAMONDS, 13, Suit.CLUBS, 13));
	Suit[] suits = {Suit.SPADES, Suit.HEARTS, Suit.DIAMONDS, Suit.CLUBS};
	
	/**
	 * Searches list for the lowest valued card (of the same suit) such that the difference between the 
	 * given card and found card is <= dec. 
	 *  
	 **/
	public Card find(ArrayList<Card>list, Card card, int dec) {
		Card ret = null;
		if (card.hcp() == 0) return null;   // +ve < 0 impossible
		
		ret =	list
				.stream()
				.filter(c->samesuit(c, card) && card.hcp() > c.hcp() && (card.hcp() - c.hcp()) <= dec) 
				.min((c1,c2) -> Integer.compare(c1.hcp(), c2.hcp())).orElse(null);

		return ret;
	}
	
	public boolean samesuit(Card c1, Card c2) {
		return samesuit(c1, c2.suit());
	}
	public boolean samesuit(Card c, Suit suit) {
		return c.suit().equals(suit);
	}
	public int hcp(ArrayList<Card>list) {
		return list.stream().mapToInt(c->c.hcp()).sum();
	}
	
	public int suitlength(ArrayList<Card> list, Suit suit) {
		return (int)list.stream().filter(c -> samesuit(c,suit)).count();
	}
	/**
	 * Swaps cards between the two lists so as to make the total HCP of the lhcp = hcp while
	 * preserving the suit lengths of the lists. Note: It might not be possible to find a solution.
	 * For example: lhcp = AKQJ1098765432 and hcp > 10. Cannot increase High Card Points without
	 * altering the suit length.
	 * 
	 */
	public boolean setHCP(ArrayList<Card> lhcp, ArrayList<Card> list, int hcp) {
		ArrayList<Card> copy = new ArrayList<Card>(list);
		copy.removeAll(lhcp);
		
		boolean ok = true;
		int diff; 
		
		while(hcp(lhcp) != hcp && ok) {
			
			diff = hcp(lhcp) - hcp;
			if (diff > 0) {  	// want to decrease the points in lhcp and increase in copy
				ok = increase_hcp(copy, lhcp, diff);
			}
			else {  // want to increase the points in lhcp and decrease in copy
				ok = increase_hcp(lhcp, copy, -diff);
			}
		}
		return ok;
	}
	/* If inc < 5 tries to find an exact swap. If not defaults to any card that increases the hcp by
	 * the largest amount < inc. If inc >= 5, swaps with any card that increases the hcp.
	 */
	public boolean increase_hcp(ArrayList<Card> toincrease, ArrayList<Card> todecrease, int inc) {
		boolean ret = false;
		int LOW_HCP = 1, HIGH_HCP = 0;
		
		Stream<Card[]> pairs = todecrease.stream().map(c-> new Card[] {c, find(toincrease, c, inc) })
				//.peek(e -> System.out.println("Mapped: " + Arrays.toString(e)))
				.filter(c->c[LOW_HCP] != null);
				
		if (inc > 4) {
			Card[] pair = pairs.findAny().orElse(null);
			if (pair != null) {
				System.out.println("inc > 4 pair = " + Arrays.toString(pair));
				swap(toincrease, pair[LOW_HCP], todecrease, pair[HIGH_HCP]);
				ret = true;
			}
		}
		else {
			Card[] pair = pairs.min( (c1,c2) -> Integer.compare(c1[HIGH_HCP].hcp()-c1[LOW_HCP].hcp()
												, c2[HIGH_HCP].hcp()-c2[LOW_HCP].hcp()) ).orElse(null);
			
			System.out.println("elem = " + Arrays.toString(pair));
			if (pair != null) {
				swap(toincrease, pair[LOW_HCP], todecrease, pair[HIGH_HCP]);
				ret = true;
			}
		}
		return ret;
	}
	/**
	 * swap two elements of two lists.
	 */
	public void swap(ArrayList<Card> list1, Card frlist1, ArrayList<Card> list2, Card frlist2) {
		replace(list1, frlist1, frlist2);
		replace(list2, frlist2, frlist1);
	}
	public void replace(ArrayList<Card> list, Card oldVal, Card newVal) {
		list.remove(oldVal);
		list.add(newVal);
	}
	/**
	 * Chooses cards from list with the given suit lengths, suitlens
	 */
	public ArrayList<Card> pick(ArrayList<Card> list, EnumMap<Suit, Integer> suitlens) {
		ArrayList<Card> picked = new ArrayList<Card>();
		int total = suitlens.values().stream().mapToInt(x->x).sum();
		EnumMap<Suit, Integer> left = new EnumMap<Suit, Integer>(suitlens);
		
		for (Card c : list) {
			if (left.get(c.suit()) > 0) {
				picked.add(c);
				left.merge(c.suit(), 1, (x,y)->x-y);
				if (picked.size() == total) break;
			}
		}
		return picked;
	}
	/** 
	 * Returns a random balanced shaped hand. 
	 * 
	 */
	public ArrayList<Card> balanced(ArrayList<Card> list) {
		
		/* Pick a random balanced shape */
		Collections.shuffle(shapes);
		Collections.shuffle(shapes.get(0));
		int i=0;
		for (Suit suit : Suit.values() ) {
			lenMap.put(suit, shapes.get(0).get(i++));
		}
		return pick(list, lenMap);
	}
	/**
	 *  Picks nCards from list with suit lengths between the specified min, max values.
	 * 
	 */
	public ArrayList<Card> pick(ArrayList<Card> list, int nCards, EnumMap<Suit, Integer> min, EnumMap<Suit, Integer> max) {
		int totalmin = min.values().stream().reduce(Integer::sum).orElse(null);
		int picked = 0;
		
		int r; Suit s;
		suitlens.replaceAll((x,y)->0);
		while (picked != nCards) {
			r = (int)(Math.random()/0.25);
			System.out.println("Random num: " + r);
			s = suits[r];
			if (suitlens.get(s) < min.get(s)) {
				suitlens.merge(s, 1, Integer::sum);
				picked += 1;
				totalmin -= 1;
			}
			else if (totalmin < (nCards - picked)) {
				suitlens.merge(s, 1, Integer::sum);
				picked += 1;
			}
		}
		return pick(list,suitlens);
	}
}
