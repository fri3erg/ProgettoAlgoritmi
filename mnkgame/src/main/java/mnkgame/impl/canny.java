package mnkgame.impl;

import java.util.Random;


import mnkgame.MNKBoard;
import mnkgame.MNKCell;
import mnkgame.MNKGameState;
import mnkgame.MNKPlayer;

public class struct{
	short num;
	MNKCell cell;
}

public class NewException extends Exception { 
    public NewException(String errorMessage) {
        super(errorMessage);
    }
}
public class canny implements MNKPlayer {

	private Random rand;
	private MNKBoard B;
	private MNKGameState myWin;
	private MNKGameState yourWin;
	private MNKGameState draw;
	private int TIMEOUT;

	public canny() {
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
	public short isDraw(MNKCell[] fc,MNKCell[] mc,MNKCell selected, Boolean firstplayer) {
		if(B.markCell(selected.i,selected.j) == draw) {
			return (short) 50;  
		}
		return 0;
	}
	public short evalGeneral(MNKCell[] fc,MNKCell[] mc,MNKCell selected, Boolean firstplayer) {
		short max=0;
		//......
		return max;
	}
	public MNKCell forced(MNKCell[] fc,MNKCell[] mc,MNKCell selected, Boolean firstplayer) {
		//......
		return ;
	}
	public MNKCell enemyForced(MNKCell[] fc,MNKCell[] mc,MNKCell selected, Boolean firstplayer) {
		//......
		return ;
	}
	public Boolean forcing(MNKCell[] fc,MNKCell[] mc,MNKCell selected, Boolean firstplayer) {
		//......
		return ;
	}
	
	public short eval(MNKCell[] fc,MNKCell[] mc,MNKCell selected, Boolean firstplayer,Boolean isforcing) {
		short max;
		if(B.markCell(selected.i,selected.j) == myWin) {
			return (short) (100-(2*(short)(firstplayer?1:0))-2*((short)(isforcing?1:0)));  
		}
		if(forcing(fc,mc,selected,firstplayer)) {
			MNKCell a=forced(fc,mc,selected,firstplayer);
			MNKCell b=enemyForced(fc,mc,selected,firstplayer);
			//friendly
				B.markCell(a.i,a.j);
				//enemy
				B.markCell(b.i,b.j);
				short[]k =new short[(((K*2)-1)^2)];
				short i=0;
				for(MNKCell d : fc) {  
					k[i]=eval(fc,mc,d,firstplayer,true);
					i++;
				}	
				B.unmarkCell();
				B.unmarkCell();
				
				max=max(k[i]);
				if(max>=(98-(2*(short)(firstplayer?1:0)))||max==50) {
					return max;
				}
			
				}
		max=max(evalGeneral(fc,mc,selected,firstplayer),isDraw(fc,mc,selected,firstplayer));
		return max;
		

	}
	
	
	
	
	
	public short deepEval(MNKCell[] fc,MNKCell[] mc,MNKCell selected, Boolean firstPlayer,Boolean isforcing) {
		boolean keepGoing = true;
		B.markCell(selected.i,selected.j);
		for(MNKCell d : fc) {
			if(!keepGoing) {
				return 0;
			}
			keepGoing=false;
			B.markCell(d.i,d.j);
			for(MNKCell c : fc) {
				short evaluated=eval(fc,mc,c,true,isforcing);
				if(evaluated==98||evaluated==100) {
					keepGoing=true;
					break;
				}
			}
			B.unmarkCell();
		}
		B.unmarkCell();
		return (short) (96-(2*(short)(firstPlayer?1:0)));
		
		
	}
		
		
		
		
		
	@Override
	public MNKCell selectCell(MNKCell[] fc, MNKCell[] mc) {
		struct[] eval=new struct[fc.length];
		long start = System.currentTimeMillis();
		//??????????????
		if(mc.length > 0) {
			MNKCell c = mc[mc.length-1]; // Recover the last move from MC
			B.markCell(c.i,c.j);         // Save the last move in the local MNKBoard
		}
		// If there is just one possible move, return immediately
		if(fc.length == 1)
			return fc[0];
		
		// Check whether there is single move win 
		short j=0;
		for(MNKCell d : fc) {  
			 if((System.currentTimeMillis()-start)/1000.0 > TIMEOUT*(99.0/100.0)) {
				 break;
			 }
			eval[j].cell=d;
			eval[j].num =max(eval(fc,mc,d,true,false),eval(fc,mc,d,false,false));
			j++;
			}
		j=0;
		short tmp;
		for(MNKCell d : fc) {  
			 if((System.currentTimeMillis()-start)/1000.0 > TIMEOUT*(99.0/100.0)) {
				 break;
			 }
			 if(eval[j].cell!=d) {throw new NewException("DeepEval mismatch" );}
			eval[j].num=max(deepEval(fc,mc,d,true,false),deepEval(fc,mc,d,false,false),eval[j].num);
			j++;
			}
		
		
		return max(eval.num).cell;
		
			
				
			
			//????????????
				B.unmarkCell();
			
		
	/*	// Check whether there is a single move loss:
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
		return c;*/
	}
	

	@Override
	public String playerName() {
		return "canny";
	}

}
