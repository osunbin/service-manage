package com.bin.client.router;

import com.bin.client.router.matcher.ArgumentConditionMatcher;
import com.bin.client.router.matcher.AttachmentConditionMatcher;
import com.bin.client.router.matcher.ConditionMatcher;
import com.bin.client.router.matcher.ParamConditionMatcher;
import com.bin.webmonitor.common.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionRouter implements Router{
    private  final Logger logger = LogManager.getLogger(ConditionRouter.class);

    /**
     * %YAML1.2
     *
     * scope: application
     * runtime: true
     * force: false
     * conditions:
     *   - >
     *     method!=sayHello =>
     *   - >
     *     ip=127.0.0.1
     *     =>
     *     1.1.1.1
     */
    public static List<String> parse(String rawRule) {
        // rawRule  JSON
        Map<String, Object> map = new HashMap<>();
        return null;
    }

    protected static final Pattern ROUTE_PATTERN = Pattern.compile("([&!=,]*)\\s*([^&!=,\\s]+)");
    protected Map<String, ConditionMatcher> whenCondition;
    protected Map<String, ConditionMatcher> thenCondition;

    private   String rule;
    public ConditionRouter(String rule) {
     this.rule =rule;
        init(rule);
    }

    public void init(String rule) {
        try {
            if (rule == null || rule.trim().length() == 0) {
                throw new IllegalArgumentException("Illegal route rule!");
            }
            rule = rule.replace("consumer.", "").replace("provider.", "");
            int i = rule.indexOf("=>");
            String whenRule = i < 0 ? null : rule.substring(0, i).trim();
            String thenRule = i < 0 ? rule.trim() : rule.substring(i + 2).trim();
            Map<String, ConditionMatcher> when =
                    StringUtil.isBlank(whenRule) || "true".equals(whenRule) ? new HashMap<>() : parseRule(whenRule);
            Map<String, ConditionMatcher> then =
                    StringUtil.isBlank(thenRule) || "false".equals(thenRule) ? null : parseRule(thenRule);
            // NOTE: It should be determined on the business level whether the `When condition` can be empty or not.
            this.whenCondition = when;
            this.thenCondition = then;
        } catch (ParseException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private Map<String, ConditionMatcher> parseRule(String rule) throws ParseException {
        Map<String, ConditionMatcher> condition = new HashMap<>();
        if (StringUtil.isBlank(rule)) {
            return condition;
        }
        // Key-Value pair, stores both match and mismatch conditions
        ConditionMatcher matcherPair = null;
        // Multiple values
        Set<String> values = null;
        final Matcher matcher = ROUTE_PATTERN.matcher(rule);
        while (matcher.find()) { // Try to match one by one
            String separator = matcher.group(1);
            String content = matcher.group(2);
            // Start part of the condition expression.
            if (StringUtil.isEmpty(separator)) {
                matcherPair = this.getMatcher(content);
                condition.put(content, matcherPair);
            }
            // The KV part of the condition expression
            else if ("&".equals(separator)) {
                if (condition.get(content) == null) {
                    matcherPair = this.getMatcher(content);
                    condition.put(content, matcherPair);
                } else {
                    matcherPair = condition.get(content);
                }
            }
            // The Value in the KV part.
            else if ("=".equals(separator)) {
                if (matcherPair == null) {
                    throw new ParseException(
                            "Illegal route rule \""
                                    + rule + "\", The error char '" + separator
                                    + "' at index " + matcher.start() + " before \""
                                    + content + "\".",
                            matcher.start());
                }

                values = matcherPair.getMatches();
                values.add(content);
            }
            // The Value in the KV part.
            else if ("!=".equals(separator)) {
                if (matcherPair == null) {
                    throw new ParseException(
                            "Illegal route rule \""
                                    + rule + "\", The error char '" + separator
                                    + "' at index " + matcher.start() + " before \""
                                    + content + "\".",
                            matcher.start());
                }

                values = matcherPair.getMismatches();
                values.add(content);
            }
            // The Value in the KV part, if Value have more than one items.
            else if (",".equals(separator)) { // Should be separated by ','
                if (values == null || values.isEmpty()) {
                    throw new ParseException(
                            "Illegal route rule \""
                                    + rule + "\", The error char '" + separator
                                    + "' at index " + matcher.start() + " before \""
                                    + content + "\".",
                            matcher.start());
                }
                values.add(content);
            } else {
                throw new ParseException(
                        "Illegal route rule \"" + rule
                                + "\", The error char '" + separator + "' at index "
                                + matcher.start() + " before \"" + content + "\".",
                        matcher.start());
            }
        }
        return condition;
    }

    private ConditionMatcher getMatcher(String key) {
       if (key.startsWith("arguments")) {
         return new ArgumentConditionMatcher(key);
       } else if (key.startsWith("attachments")) {
        return new AttachmentConditionMatcher(key);
       } else {
           return new ParamConditionMatcher(key);
       }
    }


     public List<Invoker> route(
             RouterContext context)
            throws RouterException {
         List<Invoker> invokers = context.getInvokers();
        try {
            if (!matchWhen(context)) {
                 logger.info("Directly return. Reason: WhenCondition not match.");

                return invokers;
            }
            if (thenCondition == null) {
                logger.warn(
                        "condition state router thenCondition is empty" +
                        "The current consumer in the service blocklist. "
                                + ", tag: {}" , context.getTags());

                return null;
            }
            List<Invoker> result = invokers;
            // URLParamMap
            result.removeIf(invoker -> !matchThen(context));

            if (!result.isEmpty()) {

                return result;
            } else  {
                logger.warn(
                        "execute condition state router result list is empty. and force=true" +
                        "The route result is empty and force execute. "
                                + ", tag: {}, router: {} ",context.getTags(),rule);

                return result;
            }
        } catch (Throwable t) {
            logger.error(
                    "execute condition state router exception" +
                    "Failed to execute condition router rule: " + rule + ", invokers: " + invokers + ", cause: "
                            + t.getMessage(),
                    t);
        }
        return invokers;
    }




    boolean matchWhen(RouterContext context) {
        if (whenCondition == null || whenCondition.size() == 0) {
            return true;
        }

        return doMatch(context, whenCondition, true);
    }

    private boolean matchThen(RouterContext context) {
        if (thenCondition == null || thenCondition.size() == 0) {
            return false;
        }

        return doMatch(context, thenCondition, false);
    }

    private boolean doMatch(
            RouterContext context,
            Map<String, ConditionMatcher> conditions,
            boolean isWhenCondition) {
        for (Map.Entry<String, ConditionMatcher> entry : conditions.entrySet()) {
            ConditionMatcher matchPair = entry.getValue();
            if (!matchPair.isMatch(context, isWhenCondition)) {
                return false;
            }
        }
        return true;
    }
}
