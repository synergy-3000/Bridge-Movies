package junit_tests;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import converter.Deal.Dir;
import movie.Bid;
import movie.Bid.Tricks;
import movie.Bid.Trumps;
import movie.Bid.Type;
import movie.Bids;
import movie.Contract;

class BidsTest {
	Bid pass = Bids.pass();
	Bid doubleX = Bids.doubleX();
	Bid redouble = Bids.redouble();
	Bid[] spades,hearts,diamonds,clubs,nt; 
	
	void setup() {
		spades = fill(Trumps.SPADES);
		hearts = fill(Trumps.HEARTS);
		diamonds = fill(Trumps.DIAMONDS);
		clubs = fill(Trumps.CLUBS);
		nt = fill(Trumps.NOTRUMPS);
	}
	Bid[] fill(Trumps trumps) {
		Tricks[] tricks = Tricks.values();
		Bid[] ret = new Bid[tricks.length];
		int i=0;
		for (Tricks t : tricks)  ret[i++] = Bids.getBid(t, trumps);
		return ret;
	}
	@Test
	void testToStringBid() {
		System.out.println("Tricks.ONE " + Tricks.ONE);
		ArrayList<Bid> allbids = new ArrayList<Bid>();
		for (Tricks tricks : Tricks.values()) {
			for (Trumps trumps : Trumps.values()) {
				allbids.add(Bids.getBid(tricks, trumps));
			}
		}
		allbids.add(Bids.pass());
		allbids.add(Bids.doubleX());
		allbids.add(Bids.redouble());
		System.out.println("All bids");
		for (Bid bid : allbids) {
			System.out.println(bid);
		}
	}

	@Test
	void testPass() {
		System.out.println("Test pass:");
		ArrayDeque<Bid> auction = new ArrayDeque<Bid>(List.of(pass,pass,pass));
		assertTrue(pass.isLegal(auction));
		//assertTrue(false);
		//assertTrue(pass.isLegal(auction));
	}

	@Test
	void testDoubleX() {
		setup();
		ArrayDeque<Bid> auction = new ArrayDeque<Bid>(List.of(spades[1],pass,pass));
		assertTrue(doubleX.isLegal(auction));
		auction.push(hearts[2]); auction.push(pass);
		System.out.println("Auction: " + auction);
		assertFalse(doubleX.isLegal(auction));
		auction.push(doubleX);
		assertFalse(doubleX.isLegal(auction));
		auction.clear();
		auction.push(pass);auction.push(pass);auction.push(pass);
		assertFalse(doubleX.isLegal(auction));
		auction.push(spades[2]);
		assertTrue(doubleX.isLegal(auction));
	}

	@Test
	void testRedouble() {
		setup();
		ArrayDeque<Bid> auction = new ArrayDeque<Bid>(List.of(pass,pass,pass));
		assertFalse(redouble.isLegal(auction));
		auction.push(nt[1]);
		assertFalse(redouble.isLegal(auction));
		auction.push(doubleX);
		assertTrue(redouble.isLegal(auction));
		auction.push(pass);
		assertFalse(redouble.isLegal(auction));
	}

	@Test
	void testLastBid() {
		setup();
		ArrayDeque<Bid> auction = new ArrayDeque<Bid>(List.of(pass,pass,pass));
		Bid last = Bids.lastBid(auction);
		assertNull(last);
		auction.push(nt[2]);
		last = Bids.lastBid(auction);
		assertEquals(nt[2], last);
		auction.push(pass);
		assertEquals(nt[2], last);
		auction.push(doubleX);
		assertEquals(nt[2], last);
		auction.push(redouble);
		assertEquals(nt[2], last);
		auction.push(pass);
		assertEquals(nt[2], last);
		System.out.println(auction);
	}
	@Test
	void testGetBid() {
		setup();
		ArrayDeque<Bid> auction = new ArrayDeque<Bid>(List.of(pass,pass,pass,nt[1]));
		assertFalse(hearts[1].isLegal(auction));
		assertTrue(spades[2].isLegal(auction));
		auction.clear();
		auction.addAll(List.of(pass,pass,hearts[1]));
		assertFalse(hearts[1].isLegal(auction));
		assertFalse(clubs[1].isLegal(auction));
		assertTrue(spades[1].isLegal(auction));
		assertTrue(nt[1].isLegal(auction));
		assertTrue(spades[2].isLegal(auction));
	}
	@Test
	void testIsfinished() {
		setup();
		assertFalse(Bids.isfinished(new ArrayDeque<Bid>(List.of(pass,pass,pass))));
		assertFalse(Bids.isfinished(new ArrayDeque<Bid>(List.of(spades[1],pass,pass))));
		assertFalse(Bids.isfinished(new ArrayDeque<Bid>(List.of(spades[1],nt[1],spades[2]))));
		assertTrue(Bids.isfinished(new ArrayDeque<Bid>(List.of(pass,pass,pass,pass))));
		assertTrue(Bids.isfinished(new ArrayDeque<Bid>(List.of(pass,pass,pass,spades[1]))));
	}

	@Test
	void testGetContract() {
		// passed in
		setup();
		ArrayDeque<Bid> auction = new ArrayDeque<Bid>(List.of(pass,pass,pass,pass));
		assertNull(Bids.getContract(auction, Dir.WEST));
		
		// 2 hearts by W
		auction.pop();
		auction.offerLast(hearts[2]);
		System.out.println("Get contract for : " + auction);
		Contract c = Bids.getContract(auction, Dir.WEST);
		System.out.println(hearts[2] + " by W: " + c);
		
		// 3 hearts by N
		auction.clear();
		auction.addAll(List.of(pass,pass,hearts[3],hearts[2]));
		System.out.println("Contract for : " + auction);
		c = Bids.getContract(auction, Dir.WEST);
		System.out.println(c);
		
		// 4 hearts by W
		auction.clear();
		Dir dlr = Dir.WEST;
		auction.addAll(List.of(pass,hearts[4],hearts[3],hearts[2]));
		System.out.println("Contract for : " + auction + " Dlr: " + dlr);
		c = Bids.getContract(auction, dlr);
		System.out.println(c);
		
		// 4 spades by S
		auction.clear();
		dlr = Dir.WEST;
		auction.addAll(List.of(spades[4],hearts[4],hearts[3],hearts[2]));
		System.out.println("Contract for : " + auction + " Dlr: " + dlr);
		c = Bids.getContract(auction, dlr);
		System.out.println(c);
		
		// 4 hearts X by W
		auction.clear();
		dlr = Dir.WEST;
		auction.addAll(List.of(doubleX,hearts[4],hearts[3],hearts[2]));
		System.out.println("Contract for : " + auction + " Dlr: " + dlr);
		c = Bids.getContract(auction, dlr);
		System.out.println(c);
		
		// 4 spades XX by N
		auction.clear();
		dlr = Dir.WEST;
		auction.addAll(List.of(redouble,doubleX,spades[4],pass,spades[1],pass));
		System.out.println("Contract for : " + auction + " Dlr: " + dlr);
		c = Bids.getContract(auction, dlr);
		System.out.println(c);
		
		// 5 hearts X by E
		auction.clear();
		dlr = Dir.WEST;
		auction.addAll(List.of(doubleX,hearts[5],spades[4],hearts[2],spades[1],pass));
		System.out.println("Contract for : " + auction + " Dlr: " + dlr);
		c = Bids.getContract(auction, dlr);
		System.out.println(c);
		
		// 5 hearts by S
		auction.clear();
		dlr = Dir.NORTH;
		auction.addAll(List.of(hearts[5],spades[4],hearts[2],hearts[1],pass));
		System.out.println("Contract for : " + auction + " Dlr: " + dlr);
		c = Bids.getContract(auction, dlr);
		System.out.println(c);
	}

	@Test
	void testCompare() {
		setup();
		Type[] tocompare = { Bid.Type.PASS, Bid.Type.PASS, Bid.Type.PASS };
		ArrayDeque<Bid> auction = new ArrayDeque<Bid>(List.of(pass,pass,pass));
		assertTrue(Bids.compare(auction, tocompare));
	}

}
