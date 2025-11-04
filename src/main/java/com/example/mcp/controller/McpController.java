package com.example.mcp.controller;

import com.example.mcp.model.PingRequest;
import com.example.mcp.model.PingResponse;
import com.example.mcp.service.McpToolService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/mcp", produces = MediaType.APPLICATION_JSON_VALUE)
public class McpController {

    private final McpToolService toolService;

    public McpController(McpToolService toolService) {
        this.toolService = toolService;
    }

    @GetMapping("/schema")
    public Map<String, Object> schema() {
        return Map.of(
                "protocol", Map.of(
                        "version", "2024-05-14",
                        "transport", "rest",
                        "capabilities", List.of("schema", "tools")
                ),
                "resources", Map.of("tools_endpoint", "/mcp/tools")
        );
    }

    @GetMapping("/tools")
    public Map<String, Object> tools() {
        Map<String, Object> pingTool = Map.of(
                "name", "ping",
                "description", "Echoes the provided text back to the caller.",
                "input_schema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "text", Map.of(
                                        "type", "string",
                                        "description", "Text to be echoed back to the caller"
                                )
                        ),
                        "required", List.of("text"),
                        "additionalProperties", false
                ),
                "endpoint", "/mcp/tools/ping"
        );

        return Map.of("tools", List.of(pingTool));
    }

    @PostMapping(value = "/tools/ping", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PingResponse ping(@Valid @RequestBody PingRequest request) {
        return toolService.ping(request);
    }
}
