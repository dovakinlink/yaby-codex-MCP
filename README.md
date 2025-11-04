# 临床试验信息读取 MCP 服务

本项目提供一个兼容 OpenAI Agent Builder 的最小化 MCP（Model Context Protocol）HTTP 服务示例，基于 Spring Boot 实现。服务目前暴露一个简单的 `ping` 工具，便于在 Agent Builder 中验证 MCP 工作流。

## 环境准备

- JDK 17 或更高版本
- Maven 3.9+（如需使用仓库自带的 Maven Wrapper，请先在本地安装 Maven 以生成依赖）

> **提示**：首次启动会自动下载 Spring Boot 依赖，需保证能够访问 Maven 中央仓库。

## 启动方式

```bash
# 在仓库根目录执行
mvn spring-boot:run
```

启动后服务默认监听 `http://localhost:8080`。

## 可用端点

| 方法 | 路径              | 说明                          |
| ---- | ----------------- | ----------------------------- |
| GET  | `/mcp/schema`     | 返回 MCP 协议与资源描述信息   |
| GET  | `/mcp/tools`      | 列出服务当前可用的工具清单     |
| POST | `/mcp/tools/ping` | 调用 `ping` 工具并回显请求文本 |

### `ping` 工具示例

```bash
curl -X POST \
  http://localhost:8080/mcp/tools/ping \
  -H 'Content-Type: application/json' \
  -d '{"text":"hello mcp"}'
```

示例响应：

```json
{
  "text": "hello mcp",
  "status": "ok"
}
```

## 与 OpenAI Agent Builder 集成

1. 在 Agent Builder 中创建一个新的 MCP 连接。
2. 将服务 URL（例如 `http://localhost:8080`）填入并配置下列端点：
   - Schema Endpoint: `/mcp/schema`
   - Tools Endpoint: `/mcp/tools`
   - Ping Tool Endpoint: `/mcp/tools/ping`
3. 在工具调用配置中，按照 `ping` 工具的输入 schema 提供 `text` 字段，即可看到服务返回的回显结果。

如需扩展更多 MCP 工具，可在 `com.example.mcp.service.McpToolService` 中实现业务逻辑，并在 `com.example.mcp.controller.McpController` 中注册新的端点和工具描述。
