package tfms.main;


import org.apache.log4j.Logger;
import tfms.webserver.Webserver;

public class MainClass {
    private static final Logger log = Logger.getLogger(MainClass.class);
    public static void main(String[] args) {
        Webserver.startWebserver(8004);
        log.info("START");
    }
}
