package mnkgame.impl;

import java.util.Random;

import mnkgame.MNKBoard;
import mnkgame.MNKCell;
import mnkgame.MNKGameState;
import mnkgame.MNKPlayer;

public class PCPlayer implements MNKPlayer {

	private Random rand;
	private MNKBoard B;
	private MNKGameState myWin;
	private MNKGameState yourWin;
	private int TIMEOUT;

	public PCPlayer() {
		super();
	}

	@Override
	public void initPlayer(int m, int n, int k, boolean first, int timeoutInSecs) {
		rand    = new Random(System.currentTimeMillis()); 
		B       = new MNKBoard(m,n,k);
		myWin   = first ? MNKGameState.WINP1 : MNKGameState.WINP2; 
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
		TIMEOUT = timeoutInSecs;	

	}

	@Override
	public MNKCell selectCell(MNKCell[] fc, MNKCell[] mc) {
		long start = System.currentTimeMillis();
		if(mc.length > 0) {
			MNKCell c = mc[mc.length-1]; // Recover the last move from MC
			B.markCell(c.i,c.j);         // Save the last move in the local MNKBoard
		}
		// If there is just one possible move, return immediately
		if(fc.length == 1)
			return fc[0];
		
		// Check whether there is single move win 
		for(MNKCell d : fc) {
			// If time is running out, select a random cell
			if((System.currentTimeMillis()-start)/1000.0 > TIMEOUT*(99.0/100.0)) {
				MNKCell c = fc[rand.nextInt(fc.length)];
				B.markCell(c.i,c.j);
				return c;
			} else if(B.markCell(d.i,d.j) == myWin) {
				return d;  
			} else {
				B.unmarkCell();
			}
		}
		
		// Check whether there is a single move loss:
		// 1. mark a random position
		// 2. check whether the adversary can win
		// 3. if he can win, select his winning position 
		int pos   = rand.nextInt(fc.length); 
		MNKCell c = fc[pos]; // random move
		B.markCell(c.i,c.j); // mark the random position	
		for(int k = 0; k < fc.length; k++) {
			// If time is running out, return the randomly selected  cell
      if((System.currentTimeMillis()-start)/1000.0 > TIMEOUT*(99.0/100.0)) {
				return c;
			} else if(k != pos) {     
				MNKCell d = fc[k];
				if(B.markCell(d.i,d.j) == yourWin) {
					B.unmarkCell();        // undo adversary move
					B.unmarkCell();	       // undo my move	 
					B.markCell(d.i,d.j);   // select his winning position
					return d;							 // return his winning position
				} else {
					B.unmarkCell();	       // undo adversary move to try a new one
				}	
			}	
		}
		// No win or loss, return the randomly selected move
		return c;
	}

	@Override
	public String playerName() {
		return "PCPlayer";
	}

}
