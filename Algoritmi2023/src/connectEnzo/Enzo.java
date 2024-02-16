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
	final int EMPTY_1 = 10;
	final int EMPTY_0 = 5;
	final int HEIGHT_1 = 15;
	final int MARKED = 7;

	private int M;
	private int N;
	private int X;
	private boolean isFirst;
	private int timeout;
	private long start;
    private static int MAX = 100, MIN = 0;

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
	
	   private void checktime() throws TimeoutException {
	        if ((System.currentTimeMillis() - start) / 1000.0 >= timeout * (99.0 / 100.0))
	            throw new TimeoutException();
	    }
	   
	    private CXCellState opponentState() {
	        if (isFirst) {
	            return CXCellState.P2;
	        } else {
	            return CXCellState.P1;
	        }
	    }

	    // Antonio's state
	    private CXCellState myState() {
	        if (isFirst) {
	            return CXCellState.P1;
	        } else {
	            return CXCellState.P2;
	        }
	    }
	    
	    public Integer[] sortFromMiddle(Integer[] L) {
	        Integer[] V = new Integer[L.length];
	        int cont = 0;
	        for (int i = 0; (i <= L.length / 2); i++) {
	            V[cont] = L[L.length / 2 + i];
	            cont++;
	            if (i != 0) {
	                V[cont] = L[L.length / 2 - i];
	                cont++;
	            }

	            if ((i + 1 == L.length / 2) && ((L.length % 2) == 0)) {
	                V[cont] = L[0];
	                break;
	            }
	        }
	        return V;
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
		Integer maxColumn = 0;
		long start = System.nanoTime();		//measure time,to remove

		this.max = 0;
		this.eval = new ArrayList<>(Collections.nCopies(N, 0));
		Integer[] column = board.getAvailableColumns();
		
	    if (board.numOfMarkedCells() <= 1) {
	        return N/2; 
	    }
	    
	    try {

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

		if(column.length > N/2) {
		// general analysis
		this.generalAnalysis(board, column);
		}
		else {
			//DEEPSEARCH
	        int depth;
	        if ((board.M * board.N) < 100) {
	            depth = 7;
	        } else if ((board.M * board.N) < 350) {
	            depth = 3;
	        } else {
	            depth = 1;
	        }
			
	        int outcome = Integer.MIN_VALUE, maxOutcome = outcome;
	        Integer[] S = sortFromMiddle(column);

	        
			for (int colIt : S) {
	            checktime();
	            CXGameState stateAB = board.markColumn(colIt); // mark move to value
	            outcome = AlphaBetaPruning(board, false, Integer.MIN_VALUE, Integer.MAX_VALUE,
	                    depth, stateAB);
	            board.unmarkColumn();
	            if (outcome > maxOutcome) { // comparison between the ABP result and current maximization value
	                maxOutcome = outcome;
	                maxColumn = colIt;
	            }
	        }
		}
	
		
		this.max = Collections.max(this.eval);
		maxColumn = this.eval.indexOf(this.max);
		if (this.max < 0) {
			maxColumn = column[0];
		}
		

		 long end = System.nanoTime();
		 System.out.println("last move time in ns: "+ (end-start));
		return maxColumn;
	    } catch (TimeoutException e) {
	    	 System.err.println("Timeout!!! Last computed column selected");
	         return maxColumn;
	    }
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
				if(board.cellState(i, col) != CXCellState.FREE) {
					h = M - i;
					break;
				}
			}
			
			System.out.println("altezza " + h);
			
			if(N%2 != 0) {
				//centrale
				if(col == N/2 && h != M-1) {
					System.out.println("colonna " + col);
					tempVal+=CENTER;
				}
				//vicina al centro
				else if(Math.abs(col - N/2) == 1 && h != M-1) {
					tempVal+=NEAR_CENTER;
				}	
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
	
	public int AlphaBetaPruning(CXBoard B, boolean playerAntonio, int alpha, int beta, int depth, CXGameState stateAB) {
        int eval = 0;
        try {
            checktime();
            if (!stateAB.equals(CXGameState.OPEN) || (depth == 0)) {
                return evaluate(stateAB, B);
            } else if (playerAntonio) { // MAX player
                eval = Integer.MIN_VALUE;
                Integer[] cols = B.getAvailableColumns();
                for (int c : cols) {
                    CXGameState state = B.markColumn(c);
                    eval = Math.max(eval, AlphaBetaPruning(B, !playerAntonio, alpha, beta, depth - 1, state));
                    alpha = Math.max(eval, alpha);
                    B.unmarkColumn();
                    if (beta <= alpha) { // β cutoff
                        break;
                    }
                }
                return eval;
            } else {// MIN player
                eval = Integer.MAX_VALUE;
                Integer[] cols = B.getAvailableColumns();
                for (int c : cols) {
                    CXGameState state = B.markColumn(c);
                    eval = Math.min(eval, AlphaBetaPruning(B, !playerAntonio, alpha, beta, depth - 1, state));
                    beta = Math.min(eval, beta);
                    B.unmarkColumn();
                    if (beta <= alpha) { // α cutoff
                        break;
                    }
                }
                return eval;
            }
        } catch (TimeoutException e) {
            // System.err.println("Timeout!!! Last computed column selected");
            return eval;
        }
    }

    public int evaluate(CXGameState state, CXBoard B) {
        if (state == myWin) { // Antonio wins
            return MAX;
        } else if (state == hisWin) { // Antonio loses
            return MIN;
        } else if (state == CXGameState.DRAW) { // Draw
            return 0;
        } else { // max depth reached
            int maxd = evalMaxDepth(B);
            return maxd;
        }
    }
    
    // total evaluation ABPruning base case
    public int evalMaxDepth(CXBoard B) {
        CXCell move = B.getLastMove();
        return evalVertical(B, move) + evalHorizontal(B, move) + evalDiagonal(B, move) + evalAntiDiagonal(B, move);
    }
    

    public int evalVertical(CXBoard B, CXCell move) {
        int cont;
        for (cont = 1; (move.i - cont) >= 0; cont++) {
            if (B.cellState((move.i - cont), move.j) == opponentState()) {
                cont++;
                break;
            }
        }
        // control if it's possible to win upwards
        if ((B.M - move.i) + cont - 1 < B.X) {
            return 0;
        }

        return cont = cont - 1;
    }

    public int evalHorizontal(CXBoard B, CXCell move) {
        int l, r, tot; // counters
        boolean l_bound = true, r_bound = true; // variables used to control the presence of opponent's disks at the
                                                // boundaries of Antonio's consecutives disks

        // count to the left
        for (l = 1; (move.j - l) >= 0; l++) {
            if (B.cellState(move.i, (move.j - l)) != myState()) {
                l_bound = (B.cellState(move.i, (move.j - l)) == opponentState());
                l++;
                break;
            }
        }
        l = l - 1;

        // count to the right
        for (r = 1; (move.j + r) < B.N; r++) {
            if (B.cellState(move.i, (move.j + r)) != myState()) {
                r_bound = (B.cellState(move.i, (move.j + r)) == opponentState());
                r++;
                break;
            }
        }
        r = r - 1;

        // if Antonio is blocked on the right and on the left, the move has no value
        if (r_bound && l_bound) {
            tot = 0;
        } else {
            tot = r + l + 1;
        }
        return tot;
    }

    public int evalDiagonal(CXBoard B, CXCell move) {
        // diagonal control
        int l_down, r_up, tot; // left, right, tot counters
        boolean l_down_bound = true, r_up_bound = true; // variables used to control the presence of opponent's disks at
                                                        // the boundaries of Antonio's consecutives disks

        // count downwards to the left
        for (l_down = 1; (((move.i - l_down) >= 0) && ((move.j - l_down) >= 0)); l_down++) {
            if (B.cellState((move.i - l_down), (move.j - l_down)) != myState()) {
                l_down_bound = (B.cellState((move.i - l_down), (move.j - l_down)) == opponentState());
                l_down++;
                break;
            }
        }
        l_down = l_down - 1;

        // count upwards to the right
        for (r_up = 1; (((move.i + r_up) < B.M) && ((move.j + r_up) < B.N)); r_up++) {
            if (B.cellState((move.i + r_up), (move.j + r_up)) != myState()) {
                r_up_bound = (B.cellState((move.i + r_up), (move.j + r_up)) == opponentState());
                r_up++;
                break;
            }
        }
        r_up = r_up - 1;

        // if Antonio is blocked on the right and on the left, the move has no value
        // (diagonally)
        if (r_up_bound && l_down_bound) {
            tot = 0;
        } else {
            tot = r_up + l_down;
        }

        return tot;
    }

    public int evalAntiDiagonal(CXBoard B, CXCell move) {
        // anti-diagonal control
        int l_up, r_down, tot; // left, right, tot counters
        boolean l_up_bound = true, r_down_bound = true; // variables used to control the presence of opponent's disks at
                                                        // the boundaries of Antonio's consecutives disks

        // count downwards to the right
        for (r_down = 1; (((move.i - r_down) >= 0) && ((move.j + r_down) < B.N)); r_down++) {
            if (B.cellState((move.i - r_down), (move.j + r_down)) != myState()) {
                r_down_bound = (B.cellState((move.i - r_down), (move.j + r_down)) == opponentState());
                r_down++;
                break;
            }
        }
        r_down = r_down - 1;

        // count upwards to the left
        for (l_up = 1; (((move.i + l_up) < B.M) && ((move.j - l_up) >= 0)); l_up++) {
            if (B.cellState((move.i + l_up), (move.j - l_up)) != myState()) {
                l_up_bound = (B.cellState((move.i + l_up), (move.j - l_up)) == opponentState());
                l_up++;
                break;
            }
        }
        l_up = l_up - 1;

        // if Antonio is blocked on the right and on the left, the move has no value
        // (anti-diagonally)
        if (l_up_bound && r_down_bound) {
            tot = 0;
        } else {
            tot = l_up + r_down;
        }
        return tot;
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
				board.unmarkColumn();
				return -1;
			}
			Integer[] columns = board.getAvailableColumns();
			Integer force;
			for (Integer col : columns) {
				force = this.checkForced(board, col, winCondition);// recursive
				if (!force.equals(-1)) {
					board.unmarkColumn();
					board.unmarkColumn();
					return force;
				}
			}
			board.unmarkColumn();
		}
			board.unmarkColumn();
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
