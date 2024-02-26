package com.ej.hgj.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.util.Assert;

public abstract class JsonUtils {
    public JsonUtils() {
    }

    public static String writeEntiry2JSON(Object obj) throws Exception {
        return writeEntiry2JSON(obj, (PropertyNamingStrategy.PropertyNamingStrategyBase)null);
    }

    public static String writeEntiry2JSON(Object obj, PropertyNamingStrategy.PropertyNamingStrategyBase propertyNamingStrategy) throws Exception {
        Assert.notNull(obj, "obj is null");
        ObjectMapper objectMapper = new ObjectMapper();
        if (propertyNamingStrategy != null) {
            objectMapper.setPropertyNamingStrategy(propertyNamingStrategy);
        }

        return objectMapper.writeValueAsString(obj);
    }
}
