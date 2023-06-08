package tfms.webserver;

import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;
import tfms.main.MainClass;

import java.net.InetSocketAddress;

public class Webserver {
    private static final Logger log = Logger.getLogger(Webserver.class);
    public static void startWebserver(int port)  {
        log.info("Starting webserver");
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/v1/put/task", new PutTaskHandler());
            server.createContext("/api/v1/get/task", new GetTaskHandler());
            server.createContext("/api/v1/get/appoint_task_id", new GetAppointTaskIDHandler());
            server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
            server.start();
            log.info("Webserver started success on port " + port);
        } catch (Exception e){
            log.error("Starting webserver ERROR\n"+e);
            System.out.println(e);
        }
    }
}
