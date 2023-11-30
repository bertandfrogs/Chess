package service;

import com.google.gson.Gson;

public class LogoutResponse {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
