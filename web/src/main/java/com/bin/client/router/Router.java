package com.bin.client.router;



import java.util.List;

public interface Router {

    List<Invoker> route(RouterContext context) throws RouterException;


}
