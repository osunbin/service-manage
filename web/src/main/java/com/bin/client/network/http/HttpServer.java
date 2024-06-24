package com.bin.client.network.http;

import com.bin.client.CommandListener;
import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HttpServer extends NanoHTTPD {

    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);


    public HttpServer(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT);
        logger.info("NanoHTTPD port:{} Running!",port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = normalizeUri(session.getUri());
        Map<String, List<String>> parameters = session.getParameters();


        CommandListener instance = CommandListener.getInstance();


        return newFixedLengthResponse("ok");
    }





    public static String normalizeUri(String value) {
        if (value == null) {
            return value;
        }
        if (value.startsWith("/")) {
            value = value.substring(1);
        }
        if (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;

    }
}
