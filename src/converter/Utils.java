package converter;

import converter.Deal.Vul;
import converter.Deal.Dir;

public class Utils {
	
	static Dir[] dealer = { Dir.NORTH, Dir.EAST, Dir.SOUTH, Dir.WEST };
	static Vul[] vulnerability = { Vul.NIL,Vul.N_S,Vul.E_W,Vul.BOTH,
								   Vul.N_S,Vul.E_W,Vul.BOTH,Vul.NIL,
								   Vul.E_W,Vul.BOTH,Vul.NIL,Vul.N_S,
								   Vul.BOTH,Vul.NIL,Vul.N_S,Vul.E_W };

	public static Vul getVulnerability(int board) {
		return vulnerability[(board-1) % 16];
	}
	public static Dir getDealer(int board) {
		return dealer[(board-1) % 4];
	}
	/**
	 * Returns the sum of max(a[i] - b[i], 0). Always returns an int >= 0.
	 * So diff_positive(a, b) == 0 if a[i] <= b[i] for all i
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int diff_positive(int[] a, int[] b) {
		int ret = 0;
		for (int i=0; i<a.length; i++) ret += Math.max(a[i]- b[i], 0);
		return ret;
	}
}
