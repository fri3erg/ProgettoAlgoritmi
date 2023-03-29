/*
 *  Copyright (C) 2022 Lamberto Colazzo
 *  
 *  This file is part of the ConnectX software developed for the
 *  Intern ship of the course "Information technology", University of Bologna
 *  A.Y. 2021-2022.
 *
 *  ConnectX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This  is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details; see <https://www.gnu.org/licenses/>.
 */

package connectx.L0;

import connectx.CXPlayer;
import connectx.CXBoard;
import java.util.Random;

/**
 * Totally random software player.
 */
public class L0 implements CXPlayer {
	private Random rand;

	/* Default empty constructor */
	public L0() {
	}

	public void initPlayer(int M, int N, int K,  boolean first, int timeout_in_secs) {
		int max=0;
		int[] eval= new int[M];
		for(int i=0;i<M;i++) {
			eval[i]=0;
		}
		// New random seed for each game
		rand = new Random(System.currentTimeMillis());
	}

	/* Selects a random column */
	public int selectColumn(CXBoard B) {
		
		//Integer[] L = B.getAvailableColumns();
		//return L[rand.nextInt(L.length)];
		if(numOfFreeCells()!=0) {					//if not full
			if(currentPlayer()==0) { 				//if my turn
				int [] column=getAvailableColumns();
		for(int i=0;i<column.length;i++) {			//for each column
			if(markColumn(column[i])==WIN1) {		//if i win						//not sure
				return column[i];
		}		
		}
		for(int repeat=0;repeat==0;repeat++) {		//if valid
		repeat=0;
		max=max(eval);
		markColumn(max);							//mark the best
		int [] column=getAvailableColumns();
		for(int i=0;i<column.length;i++) {			//for each column
			if(markColumn(column[i])==WIN2) {		//if he wins										//not sure
				if(column[i]==max) {				//wins and same column
					eval[column[i]]=-1;				//dont put that column
					repeat--;						//try another column
				}
				else {
				return column[i];					//if i have to block
				}
				}
			
				
		}
		}

		int [] column=getAvailableColumns();
		for(int i=0;i<column.length;i++) {			//for each column
		if(checkForced(column[i], 0)==102) {		//check my forced
			return column[i];
		}
		}
		for(int repeat=0;repeat==0;repeat++) {		//if valid
			max=max(eval);
			repeat=0;
			markColumn(max);						//mark the best
		for(int i=0;i<column.length;i++) {			//for each column
			if(checkForced(column[i], 1)==102) {	//if they can force 
			eval[max]=-1;	
			repeat--;								//try another column
		}
		}
		}
		if(max<=100) {
			deepAnalysis();
		}
		}
		}
	}
	
	void deepAnalysis() {
		
	}
	
	int max(int[] eval) {
		int maxfound=Math.max(eval);
		if(maxfound<0) {
			//porcodio
		}
		
	}
	
	void generalCheck() {
		int [] column=getAvailableColumns();
		for(int i=0;i<column.length;i++) {
		eval[column[i]]= rand.nextInt(100);
		}
		
	}
	
	int checkForced(int column, int player) {
			markColumn(column);
			int forced =forcedColumn();
			if(forced=-2) {
				return 102;
			}
			if(!bool(forced+1)) {
				checkForced(forced,int(!bool(player)));
			}
			else {
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
		
		
		
		
		
		
		
		
		return 0;
		}
		for(int i=0;i<column.length||i<2;i++) {
		markColumn(i);
		for(int j=0;j<getAvailableColumns().length;j++) {
		if(markColumn(j)==WINmy) {}
		return j;
		}
		unmarkColumn();
		if(i==1) {
			markColumn(0);
			if(markColumn(1)==WINmy) {}
			return 1;
			}
		return -1;
		}
		}
	}
		
		int forcedColumn(){
		for(int i=0;i<getAvailableColumns().length;i++) {
			if (markColumn(i)==WIN?opp) {
				return i;
			}
		}
		return -1;
		}
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
	
	

