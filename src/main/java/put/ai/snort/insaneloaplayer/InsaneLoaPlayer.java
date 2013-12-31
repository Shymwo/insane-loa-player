/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.snort.insaneloaplayer;

import java.util.List;
import java.util.Random;
import put.ai.snort.game.Board;
import put.ai.snort.game.Move;
import put.ai.snort.game.Player;

public class InsaneLoaPlayer extends Player {

    private Random random=new Random(0xdeadbeef);

    @Override
    public String getName() {
        return "Bartosz Radliński 106577 \n" +
        		"Szymon Weihs 106519";
    }

    @Override
    public Move nextMove(Board b) {
        List<Move> moves = b.getMovesFor(getColor());
        return moves.get(random.nextInt(moves.size()));
    }
}
