import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(TaskManager.class, new TaskManagerTypeAdapter())
                .create();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        createContextHandlers();
    }

    private void createContextHandlers() {
        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedTaskHandler(taskManager, gson));
    }

    public void start() {
        if (server.getAddress() != null) {
            server.start();
            System.out.println("Сервер запущен на " + PORT + " порту.");
        }
    }

    public void stop() {
        if (server.getAddress() != null) {
            server.stop(0);
            System.out.println("Сервер остановлен.");
        }
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(new InMemoryTaskManager());
        server.start();
    }
}
