package com.bin.client.router;


import com.bin.webmonitor.common.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class TagRouter implements Router{
    private  final Logger logger = LogManager.getLogger(TagRouter.class);


    private volatile TagRouterRule tagRouterRule;


    // 变化
    public synchronized void process(RouterChangedEvent event) {
        if (logger.isInfoEnabled()) {
            logger.info("Notification of tag rule, change type is: " + event.getChangeType() + ", raw rule is:\n "
                    + event.getContent());
        }

        try {
            if ("delete".equals(event.getChangeType())) {
                this.tagRouterRule = null;
            } else {
                TagRouterRule rule = new TagRouterRule(event.getContent());
                rule.init(this);
                this.tagRouterRule = rule;
            }
        } catch (Exception e) {
            logger.error(
                    "Failed to parse the raw tag router rule" +
                    "Failed to parse the raw tag router rule and it will not take effect, please check if the "
                            + "rule matches with the template, the raw rule is:\n ",
                    e);
        }
    }

    public List<Invoker> route(
            RouterContext context)
            throws RouterException {
        List<Invoker> invokers = context.getInvokers();
        if (invokers == null || invokers.size() > 0) {

            return invokers;
        }

        // since the rule can be changed by config center, we should copy one to use.
        final TagRouterRule tagRouterRuleCopy = tagRouterRule;
        if (tagRouterRuleCopy == null) {

            return filterUsingStaticTag(invokers,  context);
        }

        List<Invoker> result = invokers;

        String tag = context.getTags();

        // if we are requesting for a Provider with a specific tag
        if (StringUtil.isNotEmpty(tag)) {
            Set<String> addresses =
                    tagRouterRuleCopy.getTagnameToAddresses().get(tag);
            // filter by dynamic tag group first
            if (addresses != null) { // null means tag not set
                Iterator<Invoker> iterator = result.iterator();
                while (iterator.hasNext()) {
                    Invoker invoker = iterator.next();
                    if (!addresses.contains(invoker.getAddresses())) {
                        iterator.remove();
                    }

                }
                // if result is not null OR it's null but force=true, return result directly
                if (result != null || result.size() > 0)  {
                    return result;
                }
            } else {
                // dynamic tag group doesn't have any item about the requested app OR it's null after filtered by
                // dynamic tag group but force=false. check static tag
                result = filterInvoker(invokers, invoker -> tag.equals(invoker.getTags()));
            }
            // If there's no tagged providers that can match the current tagged request. force.tag is set by default
            // to false, which means it will invoke any providers without a tag unless it's explicitly disallowed.
            if (result != null || result.size() > 0) {

                return result;
            }
            // FAILOVER: return all Providers without any tags.
            else {
                List<Invoker> tmp = filterInvoker(
                        invokers, invoker -> {
                            if(tagRouterRuleCopy.getAddresses()!= null)
                                return true;
                             return false;
                        });

                return filterInvoker(
                        tmp, invoker -> StringUtil.isEmpty(invoker.getTags()));
            }
        } else {
            // List<String> addresses = tagRouterRule.filter(providerApp);
            // return all addresses in dynamic tag group.
            Set<String> addresses = tagRouterRuleCopy.getAddresses();
            if (addresses != null) {
                result = filterInvoker(invokers, invoker -> addresses != null);
                // 1. all addresses are in dynamic tag group, return empty list.
                if (result == null || result.size() > 0) {

                    return result;
                }
                // 2. if there are some addresses that are not in any dynamic tag group, continue to filter using the
                // static tag group.
            }

            return filterInvoker(result, invoker -> {
                String localTag = invoker.getTags();
                return StringUtil.isEmpty(localTag);
            });
        }
    }

    private List<Invoker> filterUsingStaticTag(List<Invoker> invokers, RouterContext context) {
        List<Invoker> result;
        // Dynamic param
        String tag = context.getTags();
        // Tag request
        if (!StringUtil.isEmpty(tag)) {
            result = filterInvoker(
                    invokers, invoker -> tag.equals(invoker.getTags()));
            if (result == null ) {
                result = filterInvoker(
                        invokers,
                        invoker -> StringUtil.isEmpty(invoker.getTags()));
            }
        } else {
            result = filterInvoker(
                    invokers, invoker -> StringUtil.isEmpty(invoker.getTags()));
        }
        return result;
    }

    private  List<Invoker> filterInvoker(List<Invoker> invokers, Predicate<Invoker> predicate) {
        if (invokers.stream().allMatch(predicate)) {
            return invokers;
        }

        List<Invoker> newInvokers = invokers;
        newInvokers.removeIf(invoker -> !predicate.test(invoker));

        return newInvokers;
    }








}
