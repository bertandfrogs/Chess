package server.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerLogger {
    static Logger logger = Logger.getLogger("serverLogger"); // create or find logger named serverLogger

    public static void main(String[] args) throws IOException {
        var h = new FileHandler("server.log", true);
        h.setFormatter(new SimpleFormatter());
//        logger.addHandler();
    }
}

