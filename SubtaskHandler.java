import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    // Константы для методов HTTP
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case GET:
                    handleGetRequest(exchange, path);
                    break;
                case POST:
                    handlePostRequest(exchange);
                    break;
                case DELETE:
                    handleDeleteRequest(exchange, path);
                    break;
                default:
                    sendNotFound(exchange, "Method not supported");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange, "Internal server error: " + e.getMessage());
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        String[] splitPath = path.split("/");
        if (splitPath.length == 2) {
            System.out.println("Getting all subtasks");
            String response = gson.toJson(taskManager.getAllSubtasks());
            sendText(exchange, response, 200);
        } else if (splitPath.length == 3) {
            int id = Integer.parseInt(splitPath[2]);
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null) {
                System.out.println("Subtask found: " + subtask);
                String response = gson.toJson(subtask);
                sendText(exchange, response, 200);
            } else {
                System.out.println("Subtask not found with id: " + id);
                sendNotFound(exchange, "Subtask not found");
            }
        } else {
            System.out.println("Invalid path: " + path);
            sendNotFound(exchange, "Invalid path");
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Received body: " + body);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (subtask.getTaskId() == 0) {
            taskManager.createSubtask(subtask);
            System.out.println("Subtask created: " + subtask);
            String response = gson.toJson(subtask);
            sendText(exchange, response, 201);
        } else {
            Subtask existingSubtask = taskManager.getSubtaskById(subtask.getTaskId());
            if (existingSubtask == null) {
                taskManager.createSubtask(subtask);
                System.out.println("Subtask created: " + subtask);
                String response = gson.toJson(subtask);
                sendText(exchange, response, 201);
            } else {
                taskManager.updateSubtaskById(subtask.getTaskId(), subtask);
                System.out.println("Subtask updated: " + subtask);
                String response = gson.toJson(subtask);
                sendText(exchange, response, 200);
            }
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        String[] splitPath = path.split("/");
        if (splitPath.length == 2) {
            taskManager.removeAllSubtasks();
            System.out.println("All subtasks removed");
            sendText(exchange, "", 200);
        } else if (splitPath.length == 3) {
            int id = Integer.parseInt(splitPath[2]);
            taskManager.removeSubtaskById(id);
            System.out.println("Subtask removed with id: " + id);
            sendText(exchange, "", 200);
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
