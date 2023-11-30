package service;

import com.google.gson.Gson;

public class GameJoinResponse {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
