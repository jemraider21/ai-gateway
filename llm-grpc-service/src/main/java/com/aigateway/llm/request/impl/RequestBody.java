package com.aigateway.llm.request.impl;

import com.aigateway.llm.request.RequestMap;

import java.util.List;
import java.util.Map;

public record RequestBody(String model, List<RequestMessage> messages, boolean stream) implements RequestMap {
    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "model", model,
                "messages", messages.stream().map(RequestMessage::toMap).toList(),
                "stream", stream
        );
    }
}
