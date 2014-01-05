/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.snort.insaneloaplayer;

import java.util.List;

import put.ai.snort.game.Board;
import put.ai.snort.game.Move;
import put.ai.snort.game.Player;

public class InsaneLoaPlayer extends Player {

	private Integer[][] cHBoard;

	@Override
    public String getName() {
        return "Bartosz Radliński 106577 \n" +
        		"Szymon Weihs 106519";
    }

    @Override
    public Move nextMove(Board b) {
//    	Float actualValue = getValue(b);

        List<Move> moves = b.getMovesFor(getColor());
        Move bestMove = moves.get(0);
        Float bestValue = Float.MIN_VALUE;

        for (Move m: moves) {
        	Board bc = b.clone();
        	bc.doMove(m);
        	Float value = getValue(bc);
        	if (value > bestValue) {
        		bestValue = value;
        		bestMove = m;
        	}
        }

    	return bestMove;
    }

    // Heuristic Value - Summary
    public Float getValue(Board b) {
    	return getQValue(b, getColor()) + getQValue(b, getOpponent(getColor()))
    			+ getCHValue(b, getColor()) + getCHValue (b, getOpponent(getColor()));
    }

    // Quad Heuristic Value
    public Float getQValue(Board b, Color c) {
    	Float value = 0f;
    	int size = b.getSize();
    	for (int x = -1; x <= size; x++)
    		for (int y = -1; y <= size; y++) {
    			int count = 0;
    			int lineCount = 0;
    			if (b.getState(x, y) == c) { count++; lineCount++; }
    			if (b.getState(x, y+1) == c) { count++; lineCount++; }
    			if (b.getState(x+1, y) == c) count++;
    			if (b.getState(x+1, y+1) == c) count++;

    			if (count == 1) value += 0.25f;
    			if (count == 3) value -= 0.25f;
    			if (count == 2 && lineCount == 1) value -= 0.5f;
    		}
    	return -value;
    }

    // Centralization Heuristic Value
    public Integer getCHValue(Board b, Color c) {
    	if (cHBoard == null) initCHBoard(b.getSize());
    	Integer value = 0;
    	for (int x = 0; x < b.getSize(); x++)
    		for (int y = 0; y < b.getSize(); y++)
    			if (b.getState(x, y) == c)
    				value += cHBoard[x][y];
    	return value;
    }

	// Initialize value board for Centralization Heuristic
	public void initCHBoard(int size) {
		cHBoard = new Integer[size][size];
		int middle = ((size+1) / 2) - 1;
		int x;
		for (x = 0; x <= middle; x++) {
			int y;
			for (y = 0; y <= middle; y++)
				cHBoard[x][y] = Math.min(x,y);
			for (; y < size; y++) {
				int yr = (size - 1) - y;
				cHBoard[x][y] = Math.min(x, yr);
			}
		}
		for (; x < size; x++) {
			int y;
			int xr = (size - 1) - x;
			for (y = 0; y <= middle; y++)
				cHBoard[x][y] = Math.min(xr,y);
			for (; y < size; y++) {
				int yr = (size - 1) - y;
				cHBoard[x][y] = Math.min(xr, yr);
			}
		}
	}

	public Integer[][] getCHBoard() {
		return cHBoard;
	}

	public void setCHBoard(Integer[][] chBoard) {
		this.cHBoard = chBoard;
	}

}
