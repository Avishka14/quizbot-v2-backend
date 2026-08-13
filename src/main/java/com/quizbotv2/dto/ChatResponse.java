package com.quizbotv2.dto;

import java.util.List;

public record ChatResponse(List<Choice> choices) {
    public record Choice(ChatMessage message) {}
}
