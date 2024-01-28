package connectEnzo;

import connectx.*;

import java.util.concurrent.TimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Enzo implements CXPlayer {
	final int CENTER = 20;
	final int NEAR_CENTER = 5;
	final int EMPTY_1 = 5;
	final int EMPTY_0 = 5;
	final int HEIGHT_1 = 10;
	final int MARKED = 7;

	private int M;
	private int N;
	private int X;
	private boolean isFirst;
	private int timeout;
	private long start;

	private List<Integer> eval; // array with evaluation
	private CXGameState myWin;
	private CXGameState hisWin;
	private Integer max; // not used but may be, current max found

	@Override
	public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
		this.M = M;
		this.N = N;
		this.X = X;
		this.isFirst = first;
		this.timeout = timeout_in_secs;
	    this.start = System.currentTimeMillis();
		if (first) {
			this.myWin = CXGameState.WINP1;
			this.hisWin = CXGameState.WINP2;
		} else {
			this.myWin = CXGameState.WINP2;
			this.hisWin = CXGameState.WINP1;
		}

	}
	
	  private void checkTime() throws TimeoutException {
		  // Check if the time limit has been reached
		  if ((System.currentTimeMillis() - start) >= timeout * 995) throw new TimeoutException();
	  }

	/**
	 * main function chooses the column to mark
	 *
	 * @param board , board state
	 * @return column chose
	 */
	  
	@Override
	public int selectColumn(CXBoard board) {
		/*
		int j=196;
		long startstd = System.nanoTime();
		long endstd = System.nanoTime();
		System.out.println("test: "+ (endstd-startstd));
		long startcpy = System.nanoTime();
		CXBoard copy=board.copy(); 
		for(int i=0;i<N-1;i++) {
			for(int k=0;k<M-1;k++) {
		copy.markColumn(i);
			}
		}
		long endcpy = System.nanoTime();
		System.out.println("test copy: "+ (endcpy-startcpy));

		long startunm = System.nanoTime();
		for(int i=0;i<j;i++) {
		board.markColumn(0);
		board.unmarkColumn();
		}
		long endunm = System.nanoTime();

		System.out.println("test unmark: "+ (endunm-startunm));

		*/

		//non fargliene mettere nemmeno 3 di fila
		
		long start = System.nanoTime();		//measure time,to remove

		this.max = 0;
		this.eval = new ArrayList<>(Collections.nCopies(N, 0));
		Integer[] column = board.getAvailableColumns();
		
	    if (board.numOfMarkedCells() <= 1) {
	        return N/2; 
	    }

		Integer temp;
		temp = this.checkMyWin(board, column);
		if (temp != -1) {
			return temp;
		}
		temp = this.checkHisWin(board, column);
		if (temp != -1) {
			return temp;
		}

		temp = this.checkMyForced(board, column);
		if (temp != -1) {
			return temp;
		}
		temp = this.checkHisForced(board, column);
		if (temp != -1) {
			return temp;
		}

		// general analysis
		this.generalAnalysis(board, column);

		// deep
		/*
		temp = this.deepAnalysis(board.copy());
		if (temp != -1) {
			return temp;
		}
*/
		
		
		this.max = Collections.max(this.eval);
		Integer maxColumn = this.eval.indexOf(this.max);
		if (this.max < 0) {
			maxColumn = column[0];
		}

		 long end = System.nanoTime();
		 System.out.println("last move time in ns: "+ (end-start));
		return maxColumn;
	}

	/**
	 * checks if i have a direct win
	 *
	 * @param board,  board to mark, need to be copied for safety
	 * @param column, available columns to check
	 * @return column that results in win, -1 if not found
	 */
	Integer checkMyWin(CXBoard board, Integer[] column) {
		for (Integer col : column) {
			if (board.markColumn(col).equals(this.myWin)) {
				return col;
			}
			board.unmarkColumn();
		}
		return -1;
	}

	/**
	 * checks if he has a direct win
	 *
	 * @param board, board to mark, need to be copied for safety
	 * @param cols,  available columns to check, called different cause i need to
	 *               copy
	 * @return column that results in win, -1 if not found
	 */
	Integer checkHisWin(CXBoard board, Integer[] cols) {
		Integer[] column = cols;
		board.markColumn(column[0]); // mark my first
		column = board.getAvailableColumns();
		for (Integer col : column) {
			if (board.markColumn(col).equals(this.hisWin)) {
				return col;
			}
			board.unmarkColumn();
		}
		board.unmarkColumn();
		if (column.length > 1) { // check if i accidentally blocked
			board.markColumn(column[1]);
			if (board.markColumn(column[0]).equals(this.hisWin)) {
				return column[0];
			}
			board.unmarkColumn();
			board.unmarkColumn();
		}
		// takes the opportunity to avoid places where copycat would lead to loss
		for (Integer col : cols) {
			board.markColumn(col);
			if ((!board.fullColumn(col)) && board.markColumn(col).equals(this.hisWin)) {
				this.eval.set(col, -1);
			}
			board.unmarkColumn();
			board.unmarkColumn();
		}
		return -1;
	}

	/**
	 * checks what column needs to be blocked for forcing
	 *
	 * @param board,        board to mark, need to be copied for safety
	 * @param column,       available columns to check
	 * @param winCondition, opposite of who we are watching in forced, cause we are
	 *                      watching blocking
	 * @return column that results in win, -1 if not found
	 */
	Integer columnForced(CXBoard board, Integer[] column, CXGameState winCondition) {
		for (Integer col : column) {
			if (!board.markColumn(col).equals(CXGameState.OPEN)) { // i am not winning straight up
				board.unmarkColumn();
				continue;
			}
			Integer[] newColumn = board.getAvailableColumns();
			for (Integer col2 : newColumn) {
				if (col2.equals(col)) { // cannot expect opponent to help us
					continue;
				}
				if (board.markColumn(col2).equals(winCondition)) {
					board.unmarkColumn();
					return col2;
				}
				board.unmarkColumn();
			}
			board.unmarkColumn();
		}
		return -1;
	}

	/**
	 * checks if i can force to a win
	 *
	 * @param board,  board to mark, need to be copied for safety
	 * @param column, available columns to check
	 * @return column that results in win, -1 if not found
	 */
	Integer checkMyForced(CXBoard board, Integer[] column) {
		for (Integer col : column) { // for each column that does not lead to loss
			// TODO check above
			if (this.eval.get(col).equals(-1)) {
				continue;
			}
			if (this.checkForced(board.copy(), col, this.myWin) != -1) {
				return col;
			}
		}
		return -1;
	}

	/**
	 * checks if he can force to a win
	 *
	 * @param board,  board to mark, need to be copied for safety
	 * @param column, available columns to check
	 * @return column that results in win, -1 if not found
	 */
	Integer checkHisForced(CXBoard board, Integer[] column) {
		for (Integer col : column) { // for each column that does not lead to loss
			if (this.eval.get(col).equals(-1)) {
				continue;
			}
			board.markColumn(col);
			Integer[] newColumn = board.getAvailableColumns();

			for (Integer newCol : newColumn) {
				if (this.checkForced(board.copy(), newCol, this.hisWin) != -1) { // if they can force
					this.eval.set(col, -1);
					break;
				}
			}
			board.unmarkColumn();
		}
		return -1;
	}

	/**
	 * gives an evaluation 0-100 to each column
	 *
	 * @param board,  board to mark, need to be copied for safety
	 * @param column, available columns to check
	 */
		
	//problema: le colonne tutte piene non vengono più considerate.
	//Se la colonna non c'è allora la consideriamo come non free
	
	void generalAnalysis(CXBoard board, Integer[] column) {
		int tempVal = 0;
		int h = 0;
		int counter = 0;
		int prec_counter = 0;
		int max_row = 0;

		for (Integer col : column) { // for each free column
			if (this.eval.get(col).equals(-1)) {
				continue;
			}
			
			//altezza di questa colonna			
			for (int i = 0; i < M; i++) {
				if(board.cellState(i, col) == CXCellState.FREE) {
					h = M - i - 1;
				}
			}
			
			//centrale
			if(col == N/2 && h != M-1) {
				tempVal+=CENTER;
			}
			//vicina al centro
			else if(Math.abs(col - N/2) == 1 && h != M-1) {
				tempVal+=NEAR_CENTER;
			}			

			//colonna vuota e distanza da altre costruzioni di esattamente 1 colonna vuota
			//caso 1
			if(col+2 <= column.length-1 && col-2 >= 0 ) { 

				if(board.cellState(M-1, col) == CXCellState.FREE && ((board.cellState(M-1, col+1) == CXCellState.FREE &&  board.cellState(M-1, col+2) != CXCellState.FREE) || (board.cellState(M-1, col-1) == CXCellState.FREE && board.cellState(M-1, col-2) != CXCellState.FREE))){
					tempVal+=EMPTY_1;
				}
			}
			//caso 2
			else if(col+2 <= column.length-1) {

				if(board.cellState(M-1, col) == CXCellState.FREE && (board.cellState(M-1, col+1) == CXCellState.FREE &&  board.cellState(M-1, col+2) != CXCellState.FREE)){
					tempVal+=EMPTY_1;
				}
			}
			//caso 3
			else if(col-2 >= 0) {
				if(board.cellState(M-1, col) == CXCellState.FREE && (board.cellState(M-1, col-1) == CXCellState.FREE && board.cellState(M-1, col-2) != CXCellState.FREE)){
					tempVal+=EMPTY_1;
				}
			}
			
			//colonna vuota e attaccata ad altre
			//caso 1
			if(col+1 <= column.length-1 && col-1 >= 0 ) { 
				if(board.cellState(M-1, col) == CXCellState.FREE && (board.cellState(M-1, col+1) != CXCellState.FREE || board.cellState(M-1, col-1) != CXCellState.FREE)){
					tempVal+=EMPTY_0;
				}
			}
			//caso 2
			else if(col+1 <= column.length-1) {
				if(board.cellState(M-1, col) == CXCellState.FREE && board.cellState(M-1, col+1) != CXCellState.FREE){
					tempVal+=EMPTY_0;
				}
			}
			//caso 3
			else if(col-1 >= 0) {
				if(board.cellState(M-1, col) == CXCellState.FREE && board.cellState(M-1, col-1) != CXCellState.FREE){
					tempVal+=EMPTY_0;
				}
			}
			
			//riga >1
			if(board.cellState(M-1, col) != CXCellState.FREE && h != M-1) {
				tempVal+=HEIGHT_1;
			}
			
			//la riga della colonna è quella dove abbiamo più pedine
			//altezze migliori
			
			for (int i = 0; i < M; i++) { //righe / altezza
				for(int j = 0; j < N; j++) { //colonne
					if(board.cellState(i, j) == CXCellState.P1){
						counter++;
					}
				}
				prec_counter = counter;

				if(counter > prec_counter) {
					max_row = i;
				}
			}
			
			if(h == max_row) {
				tempVal+=MARKED;
			}
			
			System.out.println("valore colonna " + col + ": " + tempVal);
			this.eval.set(col, tempVal);
			
			tempVal = 0;
			h=0;
			counter = 0;
			prec_counter = 0;
			max_row = 0;
			
		}
		
	}

	/**
	 * checks if there is more complicated wins through minmax and alpha-beta
	 * pruning while time is available
	 *
	 * @param board,  board to mark, need to be copied for safety
	 * @param column, available columns to check
	 * @return column that results in win, -1 if not found
	 */
	
	Integer deepAnalysis(CXBoard board) {
		
		System.out.println("deep");

		int bestMove = board.getAvailableColumns()[0], tmp = bestMove, tmpEval, eval;
	    
	    try {
	      // Iterate over depths to perform iterative deepening
	      for (int d = 1; d <= board.numOfFreeCells(); d++) {
	        tmpEval = -1;
	        eval = -1;

	        // Iterate over available columns to evaluate possible moves
	        for (Integer m : board.getAvailableColumns()) {
	          
	          board.markColumn(m); // Try making the move
	          eval = alphabeta(board, false, -1, 1, d); // Evaluate the resulting board position using alpha-beta pruning
	          board.unmarkColumn(); // Undo the move

	          // Update the best move if the current move has a higher evaluation
	          if (eval > tmpEval) {
	            tmp = m;
	            tmpEval = eval;
	          }
	          
	        }
	        // Update the best move at the current depth
	        bestMove = tmp;
	      }
	      
	    } catch (TimeoutException e) {}
	    return bestMove;
	}

	  

	  //MinMax with alphabeta pruning
	  private int alphabeta(CXBoard board, boolean maximize, int alpha, int beta, int depth) throws TimeoutException {
	    int eval;
	    checkTime();

	    if (depth == 1 || isLeaf(board.gameState())) {
	      // If at the specified depth or a leaf node, evaluate the board position
	      eval = evaluate(board);
	    } else if (maximize) {
	      // maximize
	      eval = -1;
	      Integer[] ac = board.getAvailableColumns();
	      for (Integer c : ac) {
	    	board.markColumn(c);
	        // Recursively evaluate the resulting position for the minimizing player
	        eval = Integer.max(eval, alphabeta(board, false, alpha, beta, depth - 1));
	        board.unmarkColumn();
	        alpha = Integer.max(eval, alpha); // Update alpha
	        if (beta <= alpha) break; // Perform alpha-beta pruning if necessary
	      }
	    } else {
	      // minimize
	      eval = 1;
	      Integer[] ac = board.getAvailableColumns();
	      for (Integer c : ac) {
	    	board.markColumn(c);
	        // Recursively evaluate the resulting position for the maximizing player
	        eval = Integer.min(eval, alphabeta(board, true, alpha, beta, depth - 1));
	        board.unmarkColumn();
	        beta = Integer.min(eval, beta); // Update beta
	        if (beta <= alpha) break; // Perform alpha-beta pruning if necessary
	      }
	    }
	    return eval; 
	  }

	  private boolean isLeaf(CXGameState s) {
	    // Check if the given game state is a leaf node (terminal state)
	    return s == CXGameState.WINP1 || s == CXGameState.WINP2 || s == CXGameState.DRAW;
	  }

	  private int evaluate(CXBoard board) {
	    // Evaluate the board position based on the game state
	    if (board.gameState() == CXGameState.WINP1) {
	      return 1;
	    } else if (board.gameState() == CXGameState.WINP2) {
	      return -1;
	    } else {
	      return 0;
	    }
	  }

	/**
	 * checks if there is forcing that results in winCondition
	 *
	 * @param board,       board to mark, need to be copied for safety
	 * @param column,      single column to mark
	 * @param winCondition , results we are looking for
	 * @return column that results in win, -1 if not found
	 */
	Integer checkForced(CXBoard board, Integer column, CXGameState winCondition) {
		if (board.markColumn(column).equals(winCondition)) { // check if force found win
			board.unmarkColumn();
			return column;
		}
		Integer forced = this.columnForced(board, board.getAvailableColumns(), winCondition); // is forced?
		if (!forced.equals(-1)) {
			if (board.markColumn(forced) != CXGameState.OPEN) {
				board.unmarkColumn();
				return -1;
			}
			Integer[] columns = board.getAvailableColumns();
			Integer force;
			for (Integer col : columns) {
				force = this.checkForced(board, col, winCondition);// recursive
				if (!force.equals(-1)) {
					return force;
				}
			}
			board.unmarkColumn();
		}
		return -1;
	}

	// check instant wins ("104"instant win "103" instant loss)
	// check forced (forced win 102, forced loss 101(check accidental opponent win))
	// general?
	// deep analysis
	// deep especially for copycat
	// notes: BFS sarebbe meglio ma non implementabile quindi prob. DFS per deep
	// deep: check recursive (if follows best moves?)
	// all in time
	// max every time(time safety) or at the end (time efficient)
	// general cares of?? closeness??center??
	// hard coded for beginning???

	@Override
	public String playerName() {
		return "enzo";
	}
}
