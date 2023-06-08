package tfms.webserver;

import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class Webserver {
    private static final Logger log = Logger.getLogger(Webserver.class);
    public static void startWebserver(int port)  {
        log.info("Starting webserver");
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/v1/distribute/task", new DistributeTask());
            server.createContext("/api/v1/setStatus/user", new SetUserStatus());
            server.createContext("/api/v1/update/task", new TaskUpdate());
            server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
            server.start();
            log.info("Webserver started success on port " + port);
        } catch (Exception e){
            log.error("Starting webserver ERROR\n"+e);
        }
    }
}
