package converter;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import converter.Card.Suit;
import converter.Deal.Dir;
import converter.Deal.Vul;

public class BridgeDealsWriter {
	
	private static BridgeDealsWriter instance;
	private static String header = "Dealer,Vulnerable, BidWest, BidsNorth, BidsEast, BidsSouth,NSpades,NHearts," +
									"NDiamonds,NClubs, ESpades,EHearts,EDiamonds,EClubs, SSpades,SHearts,SDiamonds," +
									"SClubs, WSpades,WHearts,WDiamonds,WClubs";
	private static EnumMap<Dir, String> dirntostring = new EnumMap<Dir, String>(Map.of(Dir.NORTH, "N", Dir.SOUTH, "S", Dir.EAST, "E", Dir.WEST, "W"));
	private static EnumMap<Vul, String> vultostring = new EnumMap<Vul, String>(Map.of(Vul.BOTH , "All", Vul.NIL, "Nil", Vul.E_W, "E-W", Vul.N_S, "N-S"));
	
	// Singleton Class
	public static BridgeDealsWriter getInstance() {
		if (instance == null) {
			instance = new BridgeDealsWriter();
		}
		return instance;
	}
	private BridgeDealsWriter() {
//		// Create mapping of string to Rank
//		map = new HashMap<String,Rank>();
//		Rank vals[] = Rank.values();
//		for (int i =0; i < vals.length; i++) {
//			map.put(names[i], vals[i]);
//		}2♦
	}
	public void writeFile(PrintWriter pw, Deal[] deals) {
		pw.println(header);
		StringBuffer record = new StringBuffer(200);
		for (Deal deal  : deals) {
			record.delete(0,record.length());
			pw.println( String.join(",", format(deal.getDealer()),
							 format(deal.getVul()),
							 format(deal.getBids()),
							 format(deal.getNorth()),
							 format(deal.getEast()),
							 format(deal.getSouth()),
							 format(deal.getWest()))
					  );
		}
		// Debug
		//"\u2662".getBytes(Charset.forName("UTF-8"));
		//pw.println("\u2662");
		System.out.println("\u2665");
		System.out.println("\u00C6\u00D8\u00C5");
		System.out.println(Arrays.toString(new int[] {1,2,3,4,5}));
	}
	private String format(Dir dealer) {
		return dirntostring.get(dealer);
	}
	private String format(Vul vul) {
		return vultostring.get(vul);
	}
	private String format(StringBuffer[] bids) {
		return String.join(",", bids).replaceAll("P", "pass");
	}
	private String format(Hand hand) {
		return String.join(",", Deal.formatSuit(hand.getSuit(Suit.SPADES)), 
								Deal.formatSuit(hand.getSuit(Suit.HEARTS)), 
								Deal.formatSuit(hand.getSuit(Suit.DIAMONDS)),
								Deal.formatSuit(hand.getSuit(Suit.CLUBS)) );
	}
}
