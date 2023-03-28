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
		// New random seed for each game
		rand = new Random(System.currentTimeMillis());
	}

	/* Selects a random column */
	public int selectColumn(CXBoard B) {
		//Integer[] L = B.getAvailableColumns();
		//return L[rand.nextInt(L.length)];
		
		
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
	}

	public String playerName() {
		return "enzo";
	}
}
	
	

