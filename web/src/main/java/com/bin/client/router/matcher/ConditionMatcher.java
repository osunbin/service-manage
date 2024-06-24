package com.bin.client.router.matcher;

import com.bin.client.router.RouterContext;

import java.util.Set;

public interface ConditionMatcher {


    boolean isMatch(RouterContext context, boolean isWhenCondition);


    Set<String> getMatches();


    Set<String> getMismatches();

}
