package com.example.mcp.config;

import com.example.mcp.model.PingRequest;
import com.example.mcp.model.PingResponse;
import com.example.mcp.service.McpToolService;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.transport.WebMvcStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
public class McpServerConfiguration {

    private static final String SERVER_NAME = "Yaby MCP Server";
    private static final String SERVER_VERSION = "0.1.0";

    @Bean
    public WebMvcStreamableServerTransportProvider mcpTransportProvider() {
        return WebMvcStreamableServerTransportProvider.builder()
                .mcpEndpoint("/mcp")
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> mcpRouter(WebMvcStreamableServerTransportProvider transportProvider) {
        return transportProvider.getRouterFunction();
    }

    @Bean
    public McpAsyncServer mcpAsyncServer(WebMvcStreamableServerTransportProvider transportProvider,
                                         McpToolService toolService) {
        Map<String, Object> pingProperties = Map.<String, Object>of(
                "text", Map.of(
                        "type", "string",
                        "description", "Text echoed back to verify connectivity"
                )
        );

        McpSchema.JsonSchema pingSchema = new McpSchema.JsonSchema(
                "object",
                pingProperties,
                List.of("text"),
                Boolean.FALSE,
                null,
                null
        );

        McpSchema.Tool pingTool = McpSchema.Tool.builder()
                .name("ping")
                .title("Ping")
                .description("Echoes text to confirm the host can reach this MCP server.")
                .inputSchema(pingSchema)
                .build();

        return McpServer.async(transportProvider)
                .serverInfo(SERVER_NAME, SERVER_VERSION)
                .instructions("Use the ping tool to validate round-trip communication.")
                .toolCall(pingTool, (exchange, request) -> {
                    String text = Optional.ofNullable(request.arguments())
                            .map(args -> args.get("text"))
                            .map(Object::toString)
                            .filter(str -> !str.isBlank())
                            .orElse("pong");

                    PingResponse response = toolService.ping(new PingRequest(text));

                    return Mono.just(McpSchema.CallToolResult.builder()
                            .addTextContent(response.text())
                            .structuredContent(Map.of(
                                    "status", response.status(),
                                    "echo", response.text()
                            ))
                            .build());
                })
                .build();
    }

    @Bean
    public DisposableBean mcpShutdownHook(McpAsyncServer mcpAsyncServer) {
        return () -> mcpAsyncServer.closeGracefully().block(Duration.ofSeconds(5));
    }
}
