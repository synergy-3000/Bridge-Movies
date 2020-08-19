package converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import converter.Card.Rank;
import converter.Card.Suit;
import converter.Deal.Dir;
import converter.Deal.Vul;


/**
 * This class reads a text file that has been created from the "HandViewer" link in BBO (Bridge Base Online)
 * by doing a "Select All" and then a "Copy" in Safari browser.
 *  
 * @author Michael Bish
 *
 */
public class BBOReader {

	private static BBOReader instance;
	
	private static  Dir[] biddingOrder = { Dir.WEST, Dir.NORTH, Dir.EAST, Dir.SOUTH };
	// String to Card rank map
	//private Map<String,Rank> map = Map.ofEntries(entry("A",Rank.ACE), );
	
	private Map<String, Rank> map;  // Card names -> enum (Rank)
	
	private String[] names = { "2","3","4","5","6","7","8","9","T","J","Q","K","A" };
	
	public static void main(String args[]) {
		// Debug
		//System.out.println("s" + String.format("%-15s", "hello") + "e");
		
		
		
		
	   BBOReader reader = BBOReader.getInstance();
	   
	   String path = "C:\\Bridge\\Christine Everingham\\PlayHandsGenerator"; 
	   String fname = "Lesson11Hands.txt"; 
	   String outname = "testworddatasource.txt";
	   BufferedReader br;
	   
	   //File file = new File(path, fname); 
	   
	   try { 
		   br = new BufferedReader(new FileReader(path + "\\" + fname)); 
		   Deal[] deals = reader.readFile(br);
		   br.close(); 
		   
		   int count = 1;
		   for (Deal deal : deals) {
			   System.out.println("Board " + count++);
			   System.out.println(deal + "\n\n");
		   }
		   //System.out.println("test unicode" + "\u2300");
		   PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path + "\\" + outname)));
		   BridgeDealsWriter.getInstance().writeFile(out , deals);
		   //out.write(biddingLine0);
		   out.close();
		   
	   } 
	   catch(FileNotFoundException e) {
		   System.out.println("FileNotFoundException: " + e); 
	   } 
	   catch (IOException e) {
		   System.out.println("IOException: " + e); 
	   }
		  
		
		
		//Card[] spades = BBOReader.getInstance().readSuit(Suit.SPADES, "A J Q 10 5 2");
		//System.out.println(Arrays.toString(spades));
    }

	/*
	 * public static ArrayList<Card> deal(List<Card> deck, int n) { int deckSize =
	 * deck.size(); List<Card> handView = deck.subList(deckSize-n, deckSize);
	 * ArrayList<Card> hand = new ArrayList<Card>(handView); handView.clear();
	 * return hand; }
	 */
	// Singleton Class
	public static BBOReader getInstance() {
		if (instance == null) {
			instance = new BBOReader();
			
		}
		return instance;
	}
	private BBOReader() {
		// Create mapping of string to Rank
		map = new HashMap<String,Rank>();
		Rank vals[] = Rank.values();
		for (int i =0; i < vals.length; i++) {
			map.put(names[i], vals[i]);
		}
		// Testing: Not a valid card name
		System.out.println("map.get(\"M\"): " + map.get("M") );
	}
	private Card[] readSuit(Suit suit, String str) {
		// Debug
		System.out.println("Reading " + suit + " " + str);
		str = str.replaceAll("10", "T");
		//Stream<Card> stream = str.chars().filter(x-> x != ' ).mapToObj(c -> Card.valueOf( map.get(String.valueOf((char)c)), suit));
		Stream<Card> stream = str.chars().mapToObj(c -> map.get(String.valueOf((char)c))).filter(r->r != null).map(r->Card.valueOf(r,suit));
		//System.out.printf("readSuit(%s,%s)\nS",suit.toString(),str);
		
		Card[] ret = stream.toArray(Card[]::new);
		System.out.println("readSuit(), card[] length: " + ret.length);
		return ret;
	}
	private Hand readHand(BufferedReader br) {
		Card[] spades,hearts,diamonds,clubs;
		try {
			//br.readLine();  // The reader is moved to the first line after the club symbol
			clubs = readSuit(Suit.CLUBS,br.readLine());
			// If the hand is void in a suit then the text file will be missing the line of card names
			if(clubs.length > 0) br.readLine();  // Diamond symbol
			
			diamonds = readSuit(Suit.DIAMONDS,br.readLine());			
			if(diamonds.length > 0) br.readLine(); // Heart symbol
			
			hearts = readSuit(Suit.HEARTS,br.readLine());
			if(hearts.length > 0) br.readLine(); // Spade symbol
			
			spades = readSuit(Suit.SPADES,br.readLine());
			return new Hand(spades,hearts,diamonds,clubs);
		}
		catch(IOException e) {
			System.err.println("IO Exception: " + e);
			return null;
		}
	}
	private void moveToNextHand(BufferedReader br, Hand lastHand) throws IOException {
		
		/* If the hand previously was void in spades then only skip the next 2 lines */
		if (lastHand.getSuit(Suit.SPADES).length > 0) br.readLine();
		br.readLine(); // Player name
		br.readLine(); // Club symbol
		
	}
	private Deal readDeal(BufferedReader br, int board) {
		Hand north,south,east,west;
		Vul vul = Utils.getVulnerability(board);
		Dir dealer = Utils.getDealer(board);
		
		try {
			// South
			south = readHand(br);
			moveToNextHand(br, south);
			
			// West
			west = readHand(br);
			moveToNextHand(br, west);
			
			// North
			north = readHand(br);
			moveToNextHand(br, north);
			
			// East
			east = readHand(br);
			moveToNextHand(br, east);

			return new Deal(north, south, east, west, vul, dealer, readBidding(br, dealer));
		}
		catch (IOException e) {
			System.err.println("IO Exception: " + e);
			return null;
		}
	}
	/* 
	 * Moves BufferedReader to the next deal.
	 * Returns false if end of file reached 
	 */
	private boolean moveToNextDeal(BufferedReader br) {
		String ret = "";
		try {
			while (ret != null && ret.isBlank()) {
				ret = br.readLine();
			}
		}
		catch (IOException e) {
			System.err.println("IO Exception: " + e);
			return false;	
		}
		return ret != null;
	}
	private StringBuffer[] readBidding(BufferedReader br, Dir dealer) {
		// TODO Auto-generated method stub
		StringBuffer[] ret = null;
		try {
			String line; int count = 0; String bidding = "";
			boolean finished = false;
			while (!finished) {
				line = br.readLine();
				System.out.println("Bidding " + count++ + " " + line);
				//if (count == 0) biddingLine0 = new String(line);
				//count++;
				finished = line.contains(":" ); 
				if (!finished) bidding = bidding.concat(" " + line);
			}
			// Replace whitespace with ' '
			//bidding.chars().map(c -> Character.isWhitespace(c) ? ' ' : (char)c).toArray(char[]::new);
			// Debug
			ret = getBidStrings(bidding, dealer);
			System.out.println("Bids " + Arrays.toString(ret));
			
		}
		catch (IOException e) {
			System.err.println("IO Exception: " + e);	
		}
		return ret;
	}
	private StringBuffer[] getBidStrings(String bidding, Dir dlr) {
		String[] allbids = bidding.split("\t");
		// Debug 
		System.out.println("bidding:" + bidding);
		System.out.println("allbids: " + Arrays.toString(allbids));
		StringBuffer[] handbids = new StringBuffer[4];
		int j = -1; String init = "--";
		for (int i=0; i<4; i++) {
			if (dlr.equals(biddingOrder[i])) {
				init = "";
				j = i;
			}
			handbids[i] = new StringBuffer(init);
		}
		for (String bid : allbids) {
			handbids[j].append(String.format("%4s",bid));
			j = (j+1) % 4;
		}
		// Debug
		//System.out.println( "Bids " + Arrays.toString(handbids));
		return handbids;
	}
	public Deal[] readFile(BufferedReader br) {
		
		int board = 1; // Board number 
		List<Deal> deals = new ArrayList<Deal>();
		while (moveToNextDeal(br)) {
			deals.add(readDeal(br, board));
			board++;
		}
		return deals.toArray(new Deal[0]);
	}
}
