package server.services;

import com.google.gson.Gson;
import server.ServerException;
import spark.*;

import java.util.HashMap;
import java.util.Map;

public class Service {
    <T> T getBody(Request request, Class<T> classType) throws ServerException {
        var body = new Gson().fromJson(request.body(), classType);
        if (body == null) {
            throw new ServerException(400, "missing request body");
        }
        return body;
    }

    String send(Object... props) {
        Map<Object, Object> map = new HashMap<>();
        for (var i = 0; i + 1 < props.length; i = i + 2) {
            map.put(props[i], props[i + 1]);
        }
        return new Gson().toJson(map);
    }
}
