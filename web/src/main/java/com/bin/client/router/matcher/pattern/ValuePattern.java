package com.bin.client.router.matcher.pattern;


import com.bin.client.router.RouterContext;

public interface ValuePattern {

    boolean shouldMatch(String pattern);


    boolean match(String pattern, String value, RouterContext context, boolean isWhenCondition);

}
