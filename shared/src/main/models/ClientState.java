package models;

public enum ClientState {
    logged_in,
    logged_out,
    playing_game_white,
    playing_game_black,
    observing_game;

    public String getRole(ClientState state) {
        return switch (state) {
            case playing_game_black -> "BLACK";
            case playing_game_white -> "WHITE";
            case observing_game -> "OBSERVER";
            case logged_out, logged_in -> "";
        };
    }
}
