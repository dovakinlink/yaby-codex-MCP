# 临床试验信息读取 MCP 服务


本项目提供一个兼容最新 OpenAI Agent Builder 的 Model Context Protocol (MCP) 远程服务示例，
使用 Spring Boot 和官方 Java SDK 的 Streamable HTTP 传输实现。服务当前暴露一个用于连通性
校验的 `ping` 工具，帮助快速验证 Agent Builder 与自建 MCP 服务器之间的端到端流程。


## 环境准备

- JDK 17 或更高版本
- Maven 3.9+

首次启动会自动下载所需依赖，请保证能够访问 Maven 中央仓库。

## 启动服务

```bash
mvn spring-boot:run
```

应用默认监听 `http://localhost:8080`，并注册以下 MCP 相关端点：

| 路径 | 说明 |
| ---- | ---- |
| `/.well-known/mcp.json` | MCP 服务器清单 (server manifest)，提供给 Agent Builder 等客户端进行能力发现 |
| `/mcp` | Streamable HTTP (SSE) 传输端点，承载 `initialize`、`tools/list`、`tools/call` 等 JSON-RPC 交互 |

### 查看 MCP Manifest

Manifest 会根据请求自动补全当前主机信息，可通过 `curl` 验证：

```bash
curl http://localhost:8080/.well-known/mcp.json | jq
```

关键字段说明：
- `name` / `title` / `description` 描述服务器能力。
- `version` 表示当前实现版本。
- `remotes[0].type` 为 `streamable-http`，`url` 指向 JSON-RPC 交互端点 `/mcp`。

### 使用 MCP Inspector 调用 `ping`

OpenAI 官方提供的 MCP Inspector 可以直接对接本服务：

```bash
npx -y @modelcontextprotocol/inspector
```

在浏览器中选择 “Connect to MCP server” → “Remote (HTTP/Streamable)” 并填写：

- **Manifest URL**: `http://localhost:8080/.well-known/mcp.json`

连接成功后可在工具列表中看到 `ping`。执行工具时传入 `{"text": "hello"}`，应返回：

```json
{
  "status": "ok",
  "echo": "hello"
}
```

### 在 OpenAI Agent Builder 中集成

1. 启动本服务并确保 Agent Builder 能访问到服务器地址。
2. 在 Agent Builder → “Tools & Integrations” → “Model Context Protocol (MCP)” 中创建新服务器。
3. 选择 “Remote HTTP (Streamable)” 类型，填写 Manifest URL `http://<服务器地址>:8080/.well-known/mcp.json`。
4. 保存后 Agent Builder 会读取 Manifest 并与 `/mcp` 端点建立会话，`ping` 工具将出现在工具列表中。
5. 在对话或自动化流程中引用 `ping` 工具即可完成连通性验证。

如需扩展其他工具，可在 `com.example.mcp.service.McpToolService` 添加业务逻辑，
并在 `com.example.mcp.config.McpServerConfiguration` 中注册对应的工具描述与执行处理。

