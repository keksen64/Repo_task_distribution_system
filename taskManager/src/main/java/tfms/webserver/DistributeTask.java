package tfms.webserver;



import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Logger;
import tfms.entity.User;
import tfms.utils.JSONParser;
import tfms.utils.TaskAppointer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;


public class DistributeTask implements HttpHandler {
    private static final Logger log = Logger.getLogger(DistributeTask.class);
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
                log.debug("start work handler");
                String rb = ExtractorRequestBody.extract(httpExchange);
                User u1 = JSONParser.parse(rb);
                log.debug("start work handler\n body: " + rb);
                String response = TaskAppointer.create(u1);
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
        }catch (Exception e){
                log.error(e.toString());
                String responseErr = e.toString();
                byte[] bytesErr = responseErr.getBytes(StandardCharsets.UTF_8);
                String utf8EncodedStringErr = new String(bytesErr, StandardCharsets.UTF_8);
                httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", " *");
                httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                // t.sendResponseHeaders("Access-Control-Allow-Origin: *");
                httpExchange.sendResponseHeaders(400, 0);
                OutputStream os = httpExchange.getResponseBody();
                Writer ow = new OutputStreamWriter(os);
                ow.write(utf8EncodedStringErr);
                ow.close();
                os.close();
        }
    }
}
