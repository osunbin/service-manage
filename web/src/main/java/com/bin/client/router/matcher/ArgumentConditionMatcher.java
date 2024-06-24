package com.bin.client.router.matcher;


import com.bin.client.router.RouterContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * analysis the arguments in the rule.
 * Examples would be like this:
 * "arguments[0]=1", whenCondition is that the first argument is equal to '1'.
 * "arguments[1]=a", whenCondition is that the second argument is equal to 'a'.
 */
public class ArgumentConditionMatcher extends AbstractConditionMatcher{
    private static final Pattern ARGUMENTS_PATTERN = Pattern.compile("arguments\\[([0-9]+)\\]");

    public ArgumentConditionMatcher(String key) {
        super(key);
    }

    @Override
    protected String getValue(RouterContext context) {
        try {
            // split the rule
            String[] expressArray = key.split("\\.");
            String argumentExpress = expressArray[0];
            final Matcher matcher = ARGUMENTS_PATTERN.matcher(argumentExpress);
            if (!matcher.find()) {
                return DOES_NOT_FOUND_VALUE;
            }

            // extract the argument index
            int index = Integer.parseInt(matcher.group(1));
            if (index < 0 || index > context.getArguments().length) {
                return DOES_NOT_FOUND_VALUE;
            }

            // extract the argument value
            return String.valueOf(context.getArguments()[index]);
        } catch (Exception e) {
            logger.warn(
                    "Parse argument match condition failed Invalid , will ignore., ", e);
        }
        return DOES_NOT_FOUND_VALUE;
    }
}
