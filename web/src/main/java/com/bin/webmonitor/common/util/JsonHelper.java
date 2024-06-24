package com.bin.webmonitor.common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.boot.json.GsonJsonParser;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonHelper {
    private static final Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }


    public static <T> List<T> strToList(String gsonString, Class<T> cls) {
        return gson.fromJson(gsonString, new TypeToken<List<T>>() {
        }.getType());
    }


    public static Map<String, ?> jsonToMap(String data) {
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        return gson.fromJson(data, mapType);
    }

    public static Integer parseInt(String content,String param) {
        Map<String, Object> soMap = new GsonJsonParser().parseMap(content);
        Object o = soMap.get(param);
        if (o != null) {
           return Integer.parseInt(o.toString());
        }
        return 0;
    }

    public static class JsonParse{
        Map<String, Object> json;

        public JsonParse(String content) {
            json = new GsonJsonParser().parseMap(content);
        }

        public String getStr(String param) {
            Object o = json.get(param);
            if (o != null)
                return o.toString();
            return "";
        }

        public Iterator<Map.Entry<String, Object>> entrySet() {
           return json.entrySet().iterator();
        }

    }

}
