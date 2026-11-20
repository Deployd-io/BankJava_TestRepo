package com.webapp.bankingportal.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface JsonUtil {
	Logger log = LoggerFactory.getLogger(JsonUtil.class);
    public static final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    /**
     * Convert an object to a JSON string.
     *
     * @param obj the object to be converted to JSON
     * @return the JSON string representation of the object
     */
    public static String toJson(Object obj) {
		log.error(">>> Entering toJson(obj={})", obj);
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
		log.error("Exception in toJson(obj={}): {}", obj, e.getMessage(), e);
        }

		log.warn("<<< Exiting toJson(obj={})", obj);
        return null;
    }

}
