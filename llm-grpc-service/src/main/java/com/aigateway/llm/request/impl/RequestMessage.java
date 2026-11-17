package com.aigateway.llm.request.impl;

import com.aigateway.llm.request.RequestMap;

import java.util.Map;

public record RequestMessage(String prompt) implements RequestMap {
    public Map<String, Object> toMap() {
        return Map.of(
                "role", "user",
                "content", prompt
        );
    }
}
