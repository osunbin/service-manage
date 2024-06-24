package com.bin.client.router.tag;

import com.bin.client.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tag {

    private static final Logger logger = LogManager.getLogger(Tag.class);

    private String name;
    private List<ParamMatch> match;
    private List<String> addresses;

    public static Tag parseFromMap(Map<String, Object> map) {
        Tag tag = new Tag();
        tag.setName((String) map.get("name"));

        if (map.get("match") != null) {
            tag.setMatch(((List<Map<String, Object>>) map.get("match"))
                    .stream()
                    .map((objectMap) -> {
                        try {
                            return Utils.mapToPojo(objectMap, ParamMatch.class);
                        } catch (ReflectiveOperationException e) {
                            logger.error(
                                    " Failed to parse tag rule " +
                                    String.valueOf(objectMap) +
                                    "Error occurred when parsing rule component.",
                                    e);
                        }
                        return null;
                    })
                    .collect(Collectors.toList()));
        } else {
            logger.warn(
                    String.valueOf(map) +
                            "It's recommended to use 'match' instead of 'addresses' for  tag rule.");
        }


        Object addresses = map.get("addresses");
        if (addresses != null && List.class.isAssignableFrom(addresses.getClass())) {
            tag.setAddresses(
                    ((List<Object>) addresses).stream().map(String::valueOf).collect(Collectors.toList()));
        }

        return tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<ParamMatch> getMatch() {
        return match;
    }

    public void setMatch(List<ParamMatch> match) {
        this.match = match;
    }
}
