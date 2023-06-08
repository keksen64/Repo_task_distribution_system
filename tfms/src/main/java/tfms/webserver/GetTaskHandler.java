package tfms.webserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Logger;
import tfms.entity.Task;
import tfms.utils.DataSource;
import tfms.utils.JSONParser;
import tfms.utils.TaskDBCreator;
import tfms.utils.TaskGetter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class GetTaskHandler implements HttpHandler {
    private static final Logger log = Logger.getLogger(GetTaskHandler.class);
    Connection connection = null;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        try{
            log.debug("start work handler");
            String rb = ExtractorRequestBody.extract(httpExchange);
            Task t1 = JSONParser.parse(rb);
            log.debug("start work handler\n body: " + rb);
            String response = TaskGetter.get(t1);
            httpExchange.setAttribute("Content-Type", "text/html; charset=UTF-8");
            httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", " *");
            httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            OutputStream outputStream = httpExchange.getResponseBody();
            byte[] responseb = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(200, responseb.length);
            outputStream.write(responseb);
            outputStream.flush();
            outputStream.close();
            httpExchange.getRequestBody().close();
        }
        catch (Exception e){

        }

    }
}
