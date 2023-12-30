package connectEnzo;

import connectx.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Enzo implements CXPlayer {
	private int M;
	private int N;
	private int X;
	private boolean isFirst;
	private int timeout;

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
		if (first) {
			this.myWin = CXGameState.WINP1;
			this.hisWin = CXGameState.WINP2;
		} else {
			this.myWin = CXGameState.WINP2;
			this.hisWin = CXGameState.WINP1;
		}

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

		
		long start = System.nanoTime();		//measure time,to remove

		this.max = 0;
		this.eval = new ArrayList<>(Collections.nCopies(N, 0));
		Integer[] column = board.getAvailableColumns();

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
		temp = this.deepAnalysis(board.copy(), column);
		if (temp != -1) {
			return temp;
		}
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
	void generalAnalysis(CXBoard b, Integer[] column) { // temporarly random
		Random rand = new Random(System.currentTimeMillis());
		for (Integer col : column) { // for each column that does not lead lo loss
			if (this.eval.get(col).equals(-1)) {
				continue;
			}
			this.eval.set(col, rand.nextInt(100));
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
	Integer deepAnalysis(CXBoard board, Integer[] column) { // TODO
		return -1;
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
	//deep especially for copycat
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
