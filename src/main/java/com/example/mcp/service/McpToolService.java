package com.example.mcp.service;

import com.example.mcp.model.PingRequest;
import com.example.mcp.model.PingResponse;
import org.springframework.stereotype.Service;

@Service
public class McpToolService {

    public PingResponse ping(PingRequest request) {
        String payload = request.text();
        return new PingResponse(payload, "ok");
    }
}
