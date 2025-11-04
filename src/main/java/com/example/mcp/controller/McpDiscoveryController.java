package com.example.mcp.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
public class McpDiscoveryController {

    private static final String SCHEMA_URL = "https://static.modelcontextprotocol.io/schemas/2025-10-17/server.schema.json";

    @GetMapping(value = "/.well-known/mcp.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> manifest(HttpServletRequest request) {
        UriComponentsBuilder baseBuilder = UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .host(request.getServerName());

        int port = request.getServerPort();
        boolean isDefaultHttp = "http".equalsIgnoreCase(request.getScheme()) && port == 80;
        boolean isDefaultHttps = "https".equalsIgnoreCase(request.getScheme()) && port == 443;
        if (!isDefaultHttp && !isDefaultHttps) {
            baseBuilder.port(port);
        }

        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank()) {
            baseBuilder.path(contextPath);
        }

        String endpointUrl = baseBuilder.cloneBuilder()
                .path("/mcp")
                .build()
                .toUriString();

        return Map.of(
                "$schema", SCHEMA_URL,
                "name", "com.example/mcp-service",
                "title", "Yaby MCP Server",
                "description", "Exposes a ping diagnostic tool over the Model Context Protocol.",
                "version", "0.1.0",
                "remotes", List.of(Map.of(
                        "type", "streamable-http",
                        "url", endpointUrl
                ))
        );
    }
}
