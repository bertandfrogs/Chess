package utils;

import chess.interfaces.ChessMove;
import models.GameData;

public interface ClientDisplay {
    void showNotification(String message);
    void updateGameData(GameData gameData);
    void updateGameWithMove(ChessMove move);
    void showError(String error);
}
