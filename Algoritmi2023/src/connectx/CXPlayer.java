package connectx;

public interface CXPlayer { 
	

import java.util.Arrays;
import java.util.Random;

static int max;
static int timeout;
static Integer[] eval;
static CXGameState mywin;
static CXGameState hiswin;


	public void initPlayer(int M, int N, int K,  boolean first, int timeout_in_secs) {
		Random rand;
		rand= new Random(System.currentTimeMillis());
		timeout=timeout_in_secs;
		max=0;
		eval= new Integer[N];
		for(int i=0;i<M;i++) {
			eval[i]=0;
		}
	}

	public int selectColumn (CXBoard b) {
		
		//Integer[] L = B.getAvailableColumns();
		if(b.numOfFreeCells()!=0) {					//if not full
			if(b.currentPlayer()==0) { 				//if my turn
				Integer [] column=b.getAvailableColumns();
		for(int i=0;i<column.length;i++) {			//for each column
			if(b.markColumn(column[i])==mywin) {		//if i win						//not sure
				return column[i];
		}		
		}
		for(int repeat=0;repeat==0;repeat++) {		//if valid
		max=max(eval);
		b.markColumn(max);							//mark the best
		column=b.getAvailableColumns();
		for(int i=0;i<column.length;i++) {			//for each column
			if(b.markColumn(column[i])==hiswin) {		//if he wins										//not sure
				if(column[i]==max) {				//wins and same column
					eval[column[i]]=-1;				//dont put that column
					repeat--;						//try another column
					break;
				}
				else {
				return column[i];					//if i have to block
				}
				}
			
				
		}
		}

		column=b.getAvailableColumns();
		for(int i=0;i<column.length;i++) {			//for each column
		if(checkForced(b,column[i], 0)==102) {		//check my forced
			return column[i];
		}
		}
		for(int repeat=0;repeat==0;repeat++) {		//if valid
			max=max(eval);
			b.markColumn(max);						//mark the best
		for(int i=0;i<column.length;i++) {			//for each column
			if(checkForced(b,column[i], 1)==101) {	//if they can force 
			eval[max]=-1;	
			repeat--;								//try another column
			break;
		}
		}
		}
		if(max<=100) {
			deepAnalysis();
		}
		}
		}
	}
	
	void deepAnalysis() {			//to do
		
	}
	
	int max(Integer [] eval) {
		int maxfound=Arrays.stream(eval).max().getAsInt();;			//max che ho
		if(maxfound<0) {
			//porcodio
		}
		return maxfound;
		
	}
	
	void generalCheck(CXBoard b) {
		Integer[] column=b.getAvailableColumns();
		for(int i=0;i<column.length;i++) {		//temporarly random
		eval[column[i]]= rand.nextInt(100);
		}
		
	}
	
	int checkForced(CXBoard b,int column, int player) {
			b.markColumn(column);					//mark me
			int forced =forcedColumn(b,player);			//is forced?
			if(forced==-2) {						//i have 2 ways to win
				if(player==0) {
				return 102;	
				}
				else{
					return 101;
				}
			}
			if(forced!=-1) {				//i am forcing
				if(player==0) {
				player++;
				}
				else {
					player=0;
				}
				return checkForced(b,forced,player);	//recursive opposite 
			}
			else {								//not forced
				return 0;
			}
		}
		
		//for each
		//mark me
		//check if they need to put it there
		//mark them
		//repeat
		//if 2 way/i win return 
		//if gets to nothing sad return
	
		int forcedColumn(CXBoard b,int player){
			int index=-1;
			int found=0;
			CXGameState win=mywin;
			if(player==1) {
				win=hiswin;
			}
			Integer[] column=b.getAvailableColumns();
		for(int i=0;i<column.length;i++) {
			if (b.markColumn(column[i])==win) {
				found++;
				index=column[i];
			}
		}
		if(found==0)return -1;
		if(found>1)return -2;
		return index;
		}
		
		//mark each us
		//check win if win(""104"" and break)
		//check forced (forced tree if win 102(check accidental opponent win))
		//unmark
		//general?
		//mark random or 102
		//mark each them
		//check win (if win&& not same column ""103"" and break of found ,else 0 of our mark)
		//check forced (0 of our mark if !=102)
		//if change <=101 go back (check the previously marked)
		//general?
		//if<=100
		//deep analysis
		//general?
		//notes: BFS sarebbe meglio ma non implementabile quindi prob. DFS per deep
		//deep: check recursive (if follows best moves?)
		//all in time
		//max every time(time safety) or at the end (time efficient)
		//general cares of?? closeness??center??
		//hard coded for beginning???
	

		
		public String playerName() {
			return "enzo";
		}
	
		
}