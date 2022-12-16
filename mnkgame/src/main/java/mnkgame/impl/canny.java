package mnkgame.impl;

import java.util.Random;


import mnkgame.MNKBoard;
import mnkgame.MNKCell;
import mnkgame.MNKGameState;
import mnkgame.MNKPlayer;

public class struct{
	int num;
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
	public boolean isDraw(MNKCell[] fc,MNKCell[] mc,MNKCell selected, boolean firstplayer) {
		if(B.markCell(selected.i,selected.j) == draw) {
			return true;  
		}
		return false;
	}
	public int generalEval(MNKCell[] fc,MNKCell[] mc,MNKCell selected) {
		
		//......
		return ;
	}

	public MNKCell enemyForced(MNKCell[] fc,MNKCell[] mc,MNKCell selected) {
		for(MNKCell d:fc) {
			if(d.i==selected.i+1||d.i==selected.i-1||d.j==selected.j+1||d.j==selected.j-1) {
				if(B.markCell(d.i,d.j)==yourWin) {
					return d;
				}
			}
		}
		return selected;
	}
	public MNKCell forcing(MNKCell[] fc,MNKCell[] mc,MNKCell selected, boolean firstplayer) {
		//......
		return ;
	}
	
	public int eval(MNKCell[] fc,MNKCell[] mc,MNKCell selected, boolean firstplayer,boolean isforcing) {
		int max=0,draw=0;
		if(B.markCell(selected.i,selected.j) == myWin) {
			return (int) (100-((int)(!firstplayer?1:0))-(2*(int)(isforcing?1:0)));  
		}
		if(isDraw(fc,mc,selected,firstplayer)) {
			draw=50;
		}
		MNKCell a=forcing(fc,mc,selected,firstplayer);
		if(a!=selected) {
			//friendly
			B.markCell(a.i,a.j);
			MNKCell b=enemyForced(fc,mc,selected);
				//enemy
				B.markCell(b.i,b.j);
				int k[fc.length];
				int i=0;
				for(MNKCell d : fc) {  
					k[i]=eval(fc,mc,d,firstplayer,true);
					i++;
					
				}	
				B.unmarkCell();
				B.unmarkCell();
				
				for(int d=0;d<k.length;d++) {
					if(k[d]>max) {
				max=k[d];
					}
				}

				}
		return Math.max(max,draw);
		
		

	}
	
	
	
	
	
	public int deepEval(MNKCell[] fc,MNKCell[] mc,MNKCell selected, Boolean firstPlayer,Boolean isforcing) {
		boolean keepGoing = true;
		B.markCell(selected.i,selected.j);
		for(MNKCell d : fc) {
			if(!keepGoing) {
				return 0;
			}
			keepGoing=false;
			B.markCell(d.i,d.j);
			for(MNKCell c : fc) {
				int evaluated=eval(fc,mc,c,firstPlayer,isforcing);
				if(evaluated==100-((int)(!firstPlayer?1:0))||evaluated==98-((int)(!firstPlayer?1:0))) {
					keepGoing=true;
					break;
				}
			}
			B.unmarkCell();
		}
		B.unmarkCell();
		return (int) (96-((int)(!firstPlayer?1:0)));
		
		
	}
		
		
		
		
		
	@Override
	public MNKCell selectCell(MNKCell[] fc, MNKCell[] mc) {
		
		
		
		long start = System.currentTimeMillis();
		//??????????????
		if(mc.length > 0) {
			MNKCell c = mc[mc.length-1]; // Recover the last move from MC
			B.markCell(c.i,c.j);         // Save the last move in the local MNKBoard
		}
		// If there is just one possible move, return immediately
		if(fc.length == 1)
			return fc[0];
		
		
		struct[] eval=new struct[fc.length];
		int i;
		for(MNKCell d:fc) {
			eval[i].cell=d;
			eval[i].num=0;
			i++;
		}
		int j=0;
		struct max;
		max.cell=fc[0];
		max.num=0;
		for(MNKCell d : fc) {  
			 if((System.currentTimeMillis()-start)/1000.0 > TIMEOUT*(99.0/100.0)) {
				 break;
			 }
			 if(eval[j].cell!=d) {throw new NewException("myEval mismatch" );}
			 
			 
			eval[j].num =eval(fc,mc,d,true,false);
			if(eval[j].num>max.num) {
				max=eval[j];
			}
			j++;
			}
		j=0;
		B.markCell(max.cell.i, max.cell.j);
		
		
		struct max2;
		max2.cell=fc[0];
		max2.num=0;
		for(MNKCell d : fc) {  
			 if((System.currentTimeMillis()-start)/1000.0 > TIMEOUT*(99.0/100.0)) {
				 break;
			 }
			 if(eval[j].cell!=d) {throw new NewException("hisEval mismatch" );}
			 
			 
			eval[j].num =Math.max(eval[j].num, eval(fc,mc,d,false,false));
			
			if(eval[j].num>max2.num) {
				max2=eval[j];
			}
			j++;
			}
		
		if(max2.num>max.num) {
			B.unmarkCell();
			B.markCell(max2.cell.i, max2.cell.j);
			int evaluated=eval(fc,mc,max.cell,false,false);
			if(evaluated>max2.num) {
				for(int a=0;a<fc.length;a++) {
					if(eval[a].cell==max.cell) {
					eval[a].num=evaluated;	
					}
				}
				
			}
		}
		
		B.unmarkCell();
		
		int maximum=Math.max(max.num, max2.num);
		if(maximum<=97) {
			struct max3;
			max3.cell=fc[0];
			max3.num=0;
			short tmp;
		for(MNKCell d : fc) {  
			 if((System.currentTimeMillis()-start)/1000.0 > TIMEOUT*(99.0/100.0)) {
				 break;
			 }
			 if(eval[j].cell!=d) {throw new NewException("myDeepEval mismatch" );}
			 
			eval[j].num=Math.max(deepEval(fc,mc,d,true,false),eval[j].num);
			
			if(eval[j].num>max3.num) {
				max3=eval[j];
			}
			
			j++;
			}
		B.markCell(max3.cell.i, max3.cell.j);
		
		struct max4;
		max4.cell=fc[0];
		max4.num=0;
		for(MNKCell d : fc) {  
			 if((System.currentTimeMillis()-start)/1000.0 > TIMEOUT*(99.0/100.0)) {
				 break;
			 }
			 if(eval[j].cell!=d) {throw new NewException("hisDeepEval mismatch" );}
			 
			eval[j].num=Math.max(deepEval(fc,mc,d,false,false),eval[j].num);
			
			if(eval[j].num>max4.num) {
				max4=eval[j];
			}
			
			j++;
			}
		
		B.unmarkCell();
		
		maximum=Math.max(max3.num, max4.num);
		
		if(maximum<95) {
			for(MNKCell d:fc) {
				if(eval[j].cell!=d) {throw new NewException("generalEval mismatch" );}
				 
				eval[j].num=Math.max(generalEval(fc,mc,d,false),eval[j].num);
			}
		}
		
		}
		
		
		struct maxi;
		maxi.cell=fc[0];
		maxi.num=0;
		for(struct d:eval) {
			if(d.num>maxi.num) {
				maxi=d;
			}
		}
		
		
		//non so se ci va
		B.unmarkCell();
		
		return maxi.cell;
		
			
				
			
			//????????????
			
			
		
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
