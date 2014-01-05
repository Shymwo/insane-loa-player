/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.snort.insaneloaplayer;

import java.util.List;

import put.ai.snort.game.Board;
import put.ai.snort.game.Move;
import put.ai.snort.game.Player;
import put.ai.snort.game.moves.MoveMove;

public class InsaneLoaPlayer extends Player {

	private Integer[][] cHBoard;

	@Override
    public String getName() {
        return "Bartosz Radliński 106577 \n" +
        		"Szymon Weihs 106519";
    }

	// faster heuristic
    @Override
    public Move nextMove(Board b) {
    	Float actualValue = getValue(b);

        List<Move> moves = b.getMovesFor(getColor());
        Move bestMove = moves.get(0);
        Float bestValue = Float.NEGATIVE_INFINITY;

        for (Move m: moves) {
        	Float value = getNewValue(actualValue, (MoveMove) m, b);
        	System.out.println(value);
        	if (value > bestValue) {
        		bestValue = value;
        		bestMove = m;
        	}
        }

        System.out.println(bestValue);

    	return bestMove;
    }

    //first heuristic
//    @Override
//    public Move nextMove(Board b) {
//
//        List<Move> moves = b.getMovesFor(getColor());
//        Move bestMove = moves.get(0);
//        Float bestValue = Float.NEGATIVE_INFINITY;
//
//        for (Move m: moves) {
//        	Board bc = b.clone();
//        	bc.doMove(m);
//        	Float value = getValue(bc);
//        	if (value > bestValue) {
//        		bestValue = value;
//        		bestMove = m;
//        	}
//        }
//
//    	return bestMove;
//    }

    // Fast Heuristic Value using old value and move parameters
    public Float getNewValue(Float oldValue, MoveMove m, Board b) {

    	Float newValue = oldValue;
    	newValue = getNewQValue(newValue, m, b);
    	newValue = getNewCHValue(newValue, m, b);

    	return newValue;
    }

    // Fast Heuristic - correct Quad value
    public Float getNewQValue(Float value, MoveMove m, Board b) {
    	Board bc = b.clone();
    	bc.doMove(m);
    	for (int x = m.getSrcX() - 1; x <= m.getSrcX(); x++)
    		for (int y = m.getSrcY() - 1; y <= m.getSrcY(); y++) {
    			value += analyzeQuad(b, getColor(), x, y);
    			value -= analyzeQuad(bc, getColor(), x, y);
    		}
    	for (int x = m.getDstX() - 1; x <= m.getDstX(); x++)
    		for (int y = m.getDstY() - 1; y <= m.getDstY(); y++) {
    			value += analyzeQuad(b, getColor(), x, y);
    			value -= analyzeQuad(bc, getColor(), x, y);
    	    	if (b.getState(m.getDstX(), m.getDstY()) == getOpponent(getColor())) {
    	    		value -= analyzeQuad(b, getOpponent(getColor()), x, y);
    	    		value += analyzeQuad(bc, getOpponent(getColor()), x, y);
    	    	}
    		}
    	return value;
    }

    // Fast Heuristic - correct CH value
    public Float getNewCHValue(Float value, MoveMove m, Board b) {
    	value -= cHBoard[m.getSrcX()][m.getSrcY()];
    	value += cHBoard[m.getDstX()][m.getDstY()];
    	if (b.getState(m.getDstX(), m.getDstY()) == getOpponent(getColor()))
    		value += cHBoard[m.getDstX()][m.getDstY()];
    	return value;
    }

    // Overall Heuristic Value
    public Float getValue(Board b) {
    	return (getQValue(b, getColor()) - getQValue(b, getOpponent(getColor())))
    			+ (getCHValue(b, getColor()) - getCHValue (b, getOpponent(getColor())));
    }

    // Quad Heuristic Value
    public Float getQValue(Board b, Color c) {
    	Float value = 0f;
    	int size = b.getSize();
    	for (int x = -1; x <= size; x++)
    		for (int y = -1; y <= size; y++)
    			value += analyzeQuad(b, c, x, y);
    	return -value;
    }

	private Float analyzeQuad(Board b, Color c, int x, int y) {
		int count = 0;
		int crossCount = 0;
		if (b.getState(x, y) == c) { count++; crossCount++; }
		if (b.getState(x, y+1) == c) count++;
		if (b.getState(x+1, y) == c) count++;
		if (b.getState(x+1, y+1) == c) { count++; crossCount++; }

		Float v = 0f;
		if (count == 1) v += 0.25f;
		if (count == 3) v -= 0.25f;
		if (count == 2 && crossCount != 1) v -= 0.5f;
		return v;
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
