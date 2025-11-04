package com.example.mcp.model;

import jakarta.validation.constraints.NotBlank;

public record PingRequest(@NotBlank(message = "text must not be blank") String text) {
}
