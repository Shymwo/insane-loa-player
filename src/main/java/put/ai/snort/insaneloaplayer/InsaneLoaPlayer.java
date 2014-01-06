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

	// set depth of alpha-beta algorithm
	private static int maxDepth = 3;

	private Integer[][] cHBoard;

	@Override
    public String getName() {
        return "Bartosz Radliński 106577 \n" +
        		"Szymon Weihs 106519";
    }

    @Override
    public Move nextMove(Board b) {
    	Color c = getColor();
        List<Move> moves = b.getMovesFor(c);
        Move bestMove = moves.get(0);
        Float bestValue = Float.NEGATIVE_INFINITY;

        int depth = 1;
        Float alpha = Float.NEGATIVE_INFINITY;
        Float beta = Float.POSITIVE_INFINITY;

        if (depth == maxDepth) {
        	Float actualValue = getValue(b, c);
            for (Move m: moves) {
            	Float value = getNewValue(actualValue, (MoveMove) m, b, c);
            	if (value > bestValue) {
            		bestValue = value;
            		bestMove = m;
            	}
            }
        } else {
        	// alphaValue - first iteration
	        for (Move m: moves) {
	        	Board bc = b.clone();
	        	bc.doMove(m);
	        	Float newValue = getBetaValue(bc, alpha, beta, depth + 1);
	        	if (newValue > bestValue) {
	        		bestValue = newValue;
	        		bestMove = m;
	        	}
	        	if (bestValue >= beta) {
	        		return bestMove;
	        	}
	        	alpha = Math.max(bestValue, alpha);
	        }
        }

    	return bestMove;
    }

    public Float getAlphaValue(Board b, Float alpha, Float beta, int depth) {
    	Color c = getColor();
        List<Move> moves = b.getMovesFor(c);
        Float bestValue = Float.NEGATIVE_INFINITY;

        if (depth == maxDepth) {
        	Float actualValue = getValue(b, c);
            for (Move m: moves) {
            	Float value = getNewValue(actualValue, (MoveMove) m, b, c);
            	if (value > bestValue)
            		bestValue = value;
            }
        } else {
	        for (Move m: moves) {
	        	Board bc = b.clone();
	        	bc.doMove(m);
	        	bestValue = Math.max(bestValue, getBetaValue(bc, alpha, beta, depth + 1));
	        	if (bestValue >= beta)
	        		return bestValue;
	        	alpha = Math.max(bestValue, alpha);
	        }
        }

    	return bestValue;
    }

    public Float getBetaValue(Board b, Float alpha, Float beta, int depth) {
    	Color c = getOpponent(getColor());
        List<Move> moves = b.getMovesFor(c);
        Float bestOpponentValue = Float.POSITIVE_INFINITY;

        if (depth == maxDepth) {
        	Float actualValue = getValue(b, c);
            for (Move m: moves) {
            	Float value = -getNewValue(actualValue, (MoveMove) m, b, c);
            	if (value < bestOpponentValue)
            		bestOpponentValue = value;
            }
        } else {
	        for (Move m: moves) {
	        	Board bc = b.clone();
	        	bc.doMove(m);
	        	bestOpponentValue = Math.min(bestOpponentValue,
	        			getAlphaValue(bc, alpha, beta, depth + 1));
	        	if (bestOpponentValue <= alpha)
	        		return bestOpponentValue;
	        	beta = Math.min(bestOpponentValue, beta);
	        }
        }

    	return bestOpponentValue;
    }

    //fast heuristic
//  @Override
//  public Move nextMove(Board b) {
//  	Float actualValue = getValue(b);
//
//      List<Move> moves = b.getMovesFor(getColor());
//      Move bestMove = moves.get(0);
//      Float bestValue = Float.NEGATIVE_INFINITY;
//
//      for (Move m: moves) {
//      	Float value = getNewValue(actualValue, (MoveMove) m, b);
//      	if (value > bestValue) {
//      		bestValue = value;
//      		bestMove = m;
//      	}
//      }
//
//  	return bestMove;
//  }

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
    public Float getNewValue(Float oldValue, MoveMove m, Board b, Color c) {

    	Float newValue = oldValue;
    	newValue = getNewQValue(newValue, m, b, c);
    	newValue = getNewCHValue(newValue, m, b, c);

    	return newValue;
    }

    // Fast Heuristic - correct Quad value
    public Float getNewQValue(Float value, MoveMove m, Board b, Color c) {
    	Board bc = b.clone();
    	bc.doMove(m);
    	for (int x = m.getSrcX() - 1; x <= m.getSrcX(); x++)
    		for (int y = m.getSrcY() - 1; y <= m.getSrcY(); y++) {
    			value += analyzeQuad(b, c, x, y);
    			value -= analyzeQuad(bc, c, x, y);
    		}
    	for (int x = m.getDstX() - 1; x <= m.getDstX(); x++)
    		for (int y = m.getDstY() - 1; y <= m.getDstY(); y++) {
    			value += analyzeQuad(b, c, x, y);
    			value -= analyzeQuad(bc, c, x, y);
    	    	if (b.getState(m.getDstX(), m.getDstY()) == getOpponent(c)) {
    	    		value -= analyzeQuad(b, getOpponent(c), x, y);
    	    		value += analyzeQuad(bc, getOpponent(c), x, y);
    	    	}
    		}
    	return value;
    }

    // Fast Heuristic - correct CH value
    public Float getNewCHValue(Float value, MoveMove m, Board b, Color c) {
    	value -= cHBoard[m.getSrcX()][m.getSrcY()];
    	value += cHBoard[m.getDstX()][m.getDstY()];
    	if (b.getState(m.getDstX(), m.getDstY()) == getOpponent(c))
    		value += cHBoard[m.getDstX()][m.getDstY()];
    	return value;
    }

    // Overall Heuristic Value
    public Float getValue(Board b, Color c) {
    	return (getQValue(b, c) - getQValue(b, getOpponent(c)))
    			+ (getCHValue(b, c) - getCHValue (b, getOpponent(c)));
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

	public static int getMaxDepth() {
		return maxDepth;
	}

	public static void setMaxDepth(int maxDepth) {
		InsaneLoaPlayer.maxDepth = maxDepth;
	}

}
