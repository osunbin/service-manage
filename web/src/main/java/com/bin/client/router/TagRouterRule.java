package com.bin.client.router;


import com.bin.client.router.tag.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TagRouterRule {
    private List<Tag> tags;

    private final Map<String, Set<String>> addressToTagnames = new HashMap<>();
    private final Map<String, Set<String>> tagnameToAddresses = new HashMap<>();
    private String rule;

    public TagRouterRule() {
    }

    public TagRouterRule(String rule) {
        this.rule = rule;
    }


    public static TagRouterRule parseFromMap(Map<String, Object> map) {
        TagRouterRule tagRouterRule = new TagRouterRule();


        Object tags = map.get("tags");
        if (tags != null && List.class.isAssignableFrom(tags.getClass())) {
            tagRouterRule.setTags(((List<Map<String, Object>>) tags)
                    .stream()
                    .map(objMap -> Tag.parseFromMap(objMap))
                    .collect(Collectors.toList()));
        }

        return tagRouterRule;
    }


    public void init(TagRouter router) {
        // for tags with 'addresses` field set and 'match' field not set
        tags.stream()
                .filter(tag -> {
                    if (tag.getAddresses() != null && tag.getAddresses().size() > 0) {
                        return true;
                    }
                    return false;
                })
                .forEach(tag -> {
                    tagnameToAddresses.put(tag.getName(), new HashSet<>(tag.getAddresses()));
                    tag.getAddresses().forEach(addr -> {
                        Set<String> tagNames = addressToTagnames.computeIfAbsent(addr, k -> new HashSet<>());
                        tagNames.add(tag.getName());
                    });
                });

    }

    public Set<String> getAddresses() {
        return tagnameToAddresses.entrySet().stream()
                .filter(entry -> {
                    if (entry.getValue() != null && entry.getValue().size() > 0)
                        return true;
                    return false;

                })
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toSet());
    }

    public List<String> getTagNames() {
        return tags.stream().map(Tag::getName).collect(Collectors.toList());
    }

    public Map<String, Set<String>> getAddressToTagnames() {
        return addressToTagnames;
    }

    public Map<String, Set<String>> getTagnameToAddresses() {
        return tagnameToAddresses;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
