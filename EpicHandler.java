import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Handling request: " + exchange.getRequestMethod() + " " + exchange.getRequestURI().toString());
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");

            if ("GET".equals(method)) {
                handleGetRequest(exchange, splitPath, path);
            } else if ("POST".equals(method)) {
                handlePostRequest(exchange);
            } else if ("DELETE".equals(method)) {
                handleDeleteRequest(exchange, splitPath, path);
            } else {
                sendNotFound(exchange, "Method not supported");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange, "Internal server error: " + e.getMessage());
        }
    }

    private void handleGetRequest(HttpExchange exchange, String[] splitPath, String path) throws IOException {
        if (splitPath.length == 2) {
            // GET /epics
            System.out.println("Getting all epics");
            String response = gson.toJson(taskManager.getAllEpics());
            sendText(exchange, response, 200);
        } else if (splitPath.length == 3) {
            // GET /epics/{id}
            int id = Integer.parseInt(splitPath[2]);
            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                System.out.println("Epic found: " + epic);
                String response = gson.toJson(epic);
                sendText(exchange, response, 200);
            } else {
                System.out.println("Epic not found with id: " + id);
                sendNotFound(exchange, "Epic not found");
            }
        } else {
            System.out.println("Invalid path: " + path);
            sendNotFound(exchange, "Invalid path");
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Received body: " + body);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getTaskId() == 0) {
            taskManager.createEpic(epic);
            System.out.println("Epic created: " + epic);
            String response = gson.toJson(epic);
            sendText(exchange, response, 201); // Resource created
        } else {
            Epic existingEpic = taskManager.getEpicById(epic.getTaskId());
            if (existingEpic == null) {
                taskManager.createEpic(epic);
                System.out.println("Epic created: " + epic);
                String response = gson.toJson(epic);
                sendText(exchange, response, 201); // Resource created
            } else {
                taskManager.updateEpicById(epic.getTaskId(), epic);
                System.out.println("Epic updated: " + epic);
                String response = gson.toJson(epic);
                sendText(exchange, response, 200); // OK
            }
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String[] splitPath, String path) throws IOException {
        if (splitPath.length == 2) {
            taskManager.removeAllEpics();
            System.out.println("All epics removed");
            sendText(exchange, "", 200); // OK
        } else if (splitPath.length == 3) {
            int id = Integer.parseInt(splitPath[2]);
            taskManager.removeEpicById(id);
            System.out.println("Epic removed with id: " + id);
            sendText(exchange, "", 200); // OK
        } else {
            System.out.println("Invalid path: " + path);
            sendNotFound(exchange, "Invalid path");
        }
    }

    private void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }
}
