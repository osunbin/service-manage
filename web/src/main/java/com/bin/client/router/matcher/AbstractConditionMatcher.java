package com.bin.client.router.matcher;

import com.bin.client.router.RouterContext;
import com.bin.client.router.matcher.pattern.RangeValuePattern;
import com.bin.client.router.matcher.pattern.ValuePattern;
import com.bin.client.router.matcher.pattern.WildcardValuePattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The abstract implementation of ConditionMatcher, records the match and mismatch patterns of this matcher while at the same time
 * provides the common match logics.
 */
public abstract class AbstractConditionMatcher implements ConditionMatcher{
    public static final String DOES_NOT_FOUND_VALUE = "not found argument condition value";

    protected   final Logger logger = LogManager.getLogger(getClass());

    final Set<String> matches = new HashSet<>();
    final Set<String> mismatches = new HashSet<>();

    private final List<ValuePattern> valueMatchers = new ArrayList<>();
    protected final String key;

    public AbstractConditionMatcher(String key) {
        this.key = key;
        valueMatchers.add(new RangeValuePattern());
        valueMatchers.add(new WildcardValuePattern());
    }


    public static String getSampleValueFromUrl(String conditionKey, RouterContext context) {
        String sampleValue = "";
        // get real invoked method name from invocation
        if (context != null && ("method".equals(conditionKey) || "method".equals(conditionKey))) {
            sampleValue = context.getMethodName();
        } else {
            sampleValue = context.getAttachment(conditionKey);
        }
        return sampleValue;
    }


    @Override
    public boolean isMatch(RouterContext context, boolean isWhenCondition) {
        String value = getValue(context);
        if (value == null) {
            // if key does not present in whichever of url, invocation or attachment based on the matcher type, then
            // return false.
            return false;
        }

        if (!matches.isEmpty() && mismatches.isEmpty()) {
            for (String match : matches) {
                if (doPatternMatch(match, value,  context, isWhenCondition)) {
                    return true;
                }
            }
            return false;
        }

        if (!mismatches.isEmpty() && matches.isEmpty()) {
            for (String mismatch : mismatches) {
                if (doPatternMatch(mismatch, value,  context, isWhenCondition)) {
                    return false;
                }
            }
            return true;
        }

        if (!matches.isEmpty() && !mismatches.isEmpty()) {
            // when both mismatches and matches contain the same value, then using mismatches first
            for (String mismatch : mismatches) {
                if (doPatternMatch(mismatch, value, context, isWhenCondition)) {
                    return false;
                }
            }
            for (String match : matches) {
                if (doPatternMatch(match, value,  context, isWhenCondition)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public Set<String> getMatches() {
        return matches;
    }

    @Override
    public Set<String> getMismatches() {
        return mismatches;
    }

    // range, equal or other methods
    protected boolean doPatternMatch(
            String pattern, String value,  RouterContext context, boolean isWhenCondition) {
        for (ValuePattern valueMatcher : valueMatchers) {
            if (valueMatcher.shouldMatch(pattern)) {
                return valueMatcher.match(pattern, value, context, isWhenCondition);
            }
        }
        // this should never happen.
        logger.error(
                "Executing condition rule value match expression error." +
                "pattern is " + pattern + ", value is " + value + ", condition type "
                        + (isWhenCondition ? "when" : "then") +
                "There should at least has one ValueMatcher instance that applies to all patterns, will force to use wildcard matcher now.");

       throw new RuntimeException("Executing condition rule value match expression error");
    }

    /**
     * Used to get value from different places of the request context, for example, url, attachment and invocation.
     * This makes condition rule possible to check values in any place of a request.
     */
    protected abstract String getValue(RouterContext context);
}
