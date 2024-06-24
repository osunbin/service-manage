package com.bin.client.router;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListenableStateRouter implements Router{
    private  final Logger logger = LogManager.getLogger(ListenableStateRouter.class);


    private List<String> conditions;

    private volatile List<ConditionRouter> conditionRouters = Collections.emptyList();



    public synchronized void process(RouterChangedEvent event) {
        if (logger.isInfoEnabled()) {
            logger.info("Notification of condition rule, change type is: " + event.getChangeType() + ", raw rule is:\n "
                    + event.getContent());
        }

        if ("delete".equals(event.getChangeType())) {
            conditions = null;
            conditionRouters = Collections.emptyList();
        } else {
            try {
                conditions = ConditionRouter.parse(event.getContent());
                generateConditions(conditions);
            } catch (Exception e) {
                logger.error(
                        "Failed to parse the raw condition rule" +
                        "Failed to parse the raw condition rule and it will not take effect, please check "
                                + "if the condition rule matches with the template, the raw rule is:\n "
                                + event.getContent(),
                        e);
            }
        }
    }

    private void generateConditions(List<String> rule) {
        if (rule != null) {
            this.conditionRouters = rule.stream()
                    .map(condition ->
                            new ConditionRouter(condition))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<Invoker> route(RouterContext context) throws RouterException {
        Set<Invoker> invokers = new HashSet<>();
        for (Router router : conditionRouters) {
            invokers.addAll(router.route(context));
        }
        return new ArrayList<>(invokers);
    }
}
