package com.bin.client.router.matcher;


import com.bin.client.router.RouterContext;

public class ParamConditionMatcher extends AbstractConditionMatcher {

    public ParamConditionMatcher(String key) {
        super(key);
    }

    @Override
    protected String getValue(RouterContext context) {
        return getSampleValueFromUrl(key, context);
    }

}
