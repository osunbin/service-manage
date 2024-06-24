package com.bin.webmonitor.common.util;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static final MediaType JSON = MediaType.parse("application/json");

    private static final OkHttpClient client = new OkHttpClient();


    public static  <T> T post(String uri, Object parameters,  Class<T> clazz) {
        String result = call("post", uri, parameters);
        return JsonHelper.fromJson(result,clazz);
    }

    public static  String post(String uri, Object parameters) {
        return call("post", uri, parameters);
    }

    public static <T> T get(String url,  Class<T> clazz) throws RuntimeException {
        String resp = get(url);
       return JsonHelper.fromJson(resp,clazz);
    }

    public static String get(String url) throws RuntimeException {
        Request.Builder builder = new Request.Builder().url(url);
        try {
            logger.info("[ARCH_CC_CLIENT_http_get_req]:{}", url);
            Response response = client.newCall(builder.build()).execute();
            ResponseBody body = response.body();
            if (null != body) {
                String resp = body.string();
                logger.info("[ARCH_CC_CLIENT_http_get_resp]:{}", resp);
                return resp;
            }
            throw new RuntimeException("ResponseBody is empty");
        } catch (Exception e) {
            logger.error("[ARCH_CC_CLIENT_http_get]error:{}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String call(String method, String uri, Object parameters) {
        try {
            Request.Builder builder;
            if ("post".equalsIgnoreCase(method)) {

                RequestBody body = RequestBody.create(JSON, JsonHelper.toJson(parameters));
                builder = new Request.Builder().url(uri).post(body);
            } else {
                throw new IllegalArgumentException("un support http method !");
            }
            Response response = client.newCall(builder.build()).execute();
            logger.debug("http response code : {}", response.code());
            String result = response.body().string();
            logger.debug("http result : {}", new Object[]{result});
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
