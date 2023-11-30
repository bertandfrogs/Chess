package service;

import com.google.gson.Gson;

public class ListGamesResponse {
    public GameResponse[] games;
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
