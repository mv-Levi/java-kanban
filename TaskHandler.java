import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");

            if ("GET".equals(method)) {
                if (splitPath.length == 2) {
                    sendText(exchange, gson.toJson(taskManager.getAllTasks()));
                } else if (splitPath.length == 3) {
                    // GET /tasks/{id}
                    int id = Integer.parseInt(splitPath[2]);
                    Task task = taskManager.getTaskById(id);
                    if (task != null) {
                        sendText(exchange, gson.toJson(task));
                    } else {
                        sendNotFound(exchange, "Task not found");
                    }
                } else {
                    sendNotFound(exchange, "Invalid path");
                }
            } else if ("POST".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);

                if (task.getTaskId() == 0) {
                    taskManager.createTask(task);
                    exchange.sendResponseHeaders(201, -1);
                } else {
                    Task existingTask = taskManager.getTaskById(task.getTaskId());
                    if (existingTask == null) {
                        taskManager.createTask(task);
                        exchange.sendResponseHeaders(201, -1);
                    } else {
                        taskManager.updateTaskById(task.getTaskId(), task);
                        exchange.sendResponseHeaders(200, -1);
                    }
                }
                exchange.close();
            } else if ("DELETE".equals(method)) {
                if (splitPath.length == 2) {
                    taskManager.removeAllTasks();
                    exchange.sendResponseHeaders(200, -1);
                    exchange.close();
                } else if (splitPath.length == 3) {
                    int id = Integer.parseInt(splitPath[2]);
                    taskManager.removeTaskById(id);
                    exchange.sendResponseHeaders(200, -1);
                    exchange.close();
                } else {
                    sendNotFound(exchange, "Неправильный путь");
                }
            } else {
                sendNotFound(exchange, "Метод не поддерживается");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (NotAcceptableException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalError(exchange, e.getMessage());
        }
    }
}
