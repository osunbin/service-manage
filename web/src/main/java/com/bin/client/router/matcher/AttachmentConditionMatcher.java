package com.bin.client.router.matcher;



import com.bin.client.router.RouterContext;
import com.bin.webmonitor.common.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * analysis the arguments in the rule.
 * Examples would be like this:
 * "attachments[foo]=bar", whenCondition is that the attachment value of 'foo' is equal to 'bar'.
 */
public class AttachmentConditionMatcher  extends AbstractConditionMatcher {
    private static final Pattern ATTACHMENTS_PATTERN = Pattern.compile("attachments\\[(.+)\\]");


    public AttachmentConditionMatcher(String key) {
        super(key);
    }

    @Override
    protected String getValue(RouterContext context) {
        try {
            // split the rule
            String[] expressArray = key.split("\\.");
            String argumentExpress = expressArray[0];
            final Matcher matcher = ATTACHMENTS_PATTERN.matcher(argumentExpress);
            if (!matcher.find()) {
                return DOES_NOT_FOUND_VALUE;
            }

            // extract the argument index
            String attachmentKey = matcher.group(1);
            if (StringUtil.isEmpty(attachmentKey)) {
                return DOES_NOT_FOUND_VALUE;
            }

            // extract the argument value
            return context.getAttachment(attachmentKey);
        } catch (Exception e) {
            logger.warn(
                    "condition state router attachment match failed" +
                            "Invalid match condition: " + key,
                    e);
        }
        return DOES_NOT_FOUND_VALUE;
    }

}
