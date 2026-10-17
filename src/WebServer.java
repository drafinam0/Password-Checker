package src;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * WebServer provides an embedded zero-dependency HTTP server
 * using standard Java SE library (com.sun.net.httpserver).
 * 
 * Exposes:
 * - Static file serving for web UI (HTML, CSS, JS)
 * - POST /api/check: Analyzes password and returns JSON result
 * - GET /api/generate: Generates a strong random password
 * - GET /api/health: System health and backend status check
 */
public class WebServer {

    private final int port;
    private HttpServer server;
    private final Path webRoot;

    public WebServer(int port, String webDirRelative) {
        this.port = port;
        this.webRoot = Paths.get(webDirRelative).toAbsolutePath().normalize();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // API Contexts
        server.createContext("/api/check", new CheckHandler());
        server.createContext("/api/generate", new GenerateHandler());
        server.createContext("/api/health", new HealthHandler());

        // Static File Context (serves index.html, style.css, app.js)
        server.createContext("/", new StaticFileHandler(webRoot));

        server.setExecutor(null); // default executor
        server.start();
        System.out.println("==================================================");
        System.out.println("  PASSWORD STRENGTH CHECKER - JAVA BACKEND ACTIVE");
        System.out.println("==================================================");
        System.out.println("  Local URL:   http://localhost:" + port + "/");
        System.out.println("  API Check:   http://localhost:" + port + "/api/check");
        System.out.println("  Web Root:    " + webRoot);
        System.out.println("==================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public int getPort() {
        return port;
    }

    /**
     * Handler for /api/check
     * Accepts POST or GET requests to evaluate password strength using PasswordAnalyzer.
     */
    private static class CheckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String password = "";

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                
                // Parse simple JSON or form body: {"password":"..."}
                if (body.startsWith("{") && body.contains("\"password\"")) {
                    int keyIdx = body.indexOf("\"password\"");
                    int colonIdx = body.indexOf(":", keyIdx);
                    int firstQuote = body.indexOf("\"", colonIdx);
                    if (firstQuote != -1) {
                        int secondQuote = body.lastIndexOf("\"");
                        if (secondQuote > firstQuote) {
                            password = body.substring(firstQuote + 1, secondQuote);
                            // Unescape basic json characters
                            password = password.replace("\\\"", "\"").replace("\\\\", "\\");
                        }
                    }
                } else if (body.startsWith("password=")) {
                    password = URLDecoder.decode(body.substring("password=".length()), StandardCharsets.UTF_8);
                } else {
                    password = body;
                }
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getRawQuery();
                if (query != null && query.contains("password=")) {
                    for (String param : query.split("&")) {
                        if (param.startsWith("password=")) {
                            password = URLDecoder.decode(param.substring("password=".length()), StandardCharsets.UTF_8);
                            break;
                        }
                    }
                }
            }

            // Run analysis through pure Java PasswordAnalyzer
            PasswordResult result = PasswordAnalyzer.analyze(password);
            byte[] responseBytes = result.toJson().getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    /**
     * Handler for /api/generate
     * Generates a 14-character strong random password guaranteed to score 5/5.
     */
    private static class GenerateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            int length = 14;
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("length=")) {
                try {
                    for (String part : query.split("&")) {
                        if (part.startsWith("length=")) {
                            length = Integer.parseInt(part.substring(7));
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }

            String generated = PasswordGenerator.generate(length);
            PasswordResult result = PasswordAnalyzer.analyze(generated);

            String jsonResponse = "{\"generatedPassword\":\"" + escapeJson(generated) + "\",\"analysis\":" + result.toJson() + "}";
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    /**
     * Handler for /api/health
     */
    private static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String json = "{\"status\":\"active\",\"engine\":\"Java SE Built-in HttpServer\",\"analyzer\":\"src.PasswordAnalyzer\"}";
            byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    /**
     * Handler for static files (HTML, CSS, JS, etc.)
     */
    private static class StaticFileHandler implements HttpHandler {
        private final Path webRoot;

        public StaticFileHandler(Path webRoot) {
            this.webRoot = webRoot;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String pathStr = exchange.getRequestURI().getPath();
            if (pathStr == null || pathStr.equals("/") || pathStr.isEmpty()) {
                pathStr = "/index.html";
            }

            // Remove leading slash
            if (pathStr.startsWith("/")) {
                pathStr = pathStr.substring(1);
            }

            Path resolved = webRoot.resolve(pathStr).normalize();

            // Prevent directory traversal attacks
            if (!resolved.startsWith(webRoot) || !Files.exists(resolved) || Files.isDirectory(resolved)) {
                String notFound = "<!DOCTYPE html><html><body style='background:#000;color:#fff;'><h1>404 - File Not Found</h1></body></html>";
                byte[] bytes = notFound.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
                return;
            }

            String contentType = getMimeType(resolved.getFileName().toString());
            byte[] fileBytes = Files.readAllBytes(resolved);

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }

        private String getMimeType(String filename) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
            if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".ico")) return "image/x-icon";
            return "application/octet-stream";
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
