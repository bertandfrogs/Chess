package utils;

import chess.Game;
import models.GameData;

public interface ClientDisplay {
    void showNotification(String message);
    void updateGameData(GameData gameData) throws InterruptedException;
    void updateGameState(Game.State gameState);
    void showError(String error);
}
