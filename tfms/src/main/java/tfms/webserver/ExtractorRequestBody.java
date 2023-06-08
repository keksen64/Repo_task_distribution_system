package tfms.webserver;

import com.sun.net.httpserver.HttpExchange;

import java.util.Scanner;

public class ExtractorRequestBody {
    public static String extract(HttpExchange httpExchange){
        Scanner s = new Scanner(httpExchange.getRequestBody()).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        return result;
    }
}
