package chess;

import chess.adapters.ChessAdapter;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static chess.adapters.ChessAdapter.getGson;

public class GameTest {
    Game testGame;

    @BeforeEach
    void setup() {
        testGame = new Game();
        testGame.newGame();
    }

    @Test
    void testJSON() {
        Gson gson = getGson();
        String serialized = gson.toJson(testGame);
        System.out.println(serialized);

        Game newGameFromOld = gson.fromJson(serialized, Game.class);
    }
}
