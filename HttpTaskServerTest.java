import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private Gson gson;
    private HttpClient client;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testCreateAndGetTask() throws IOException, InterruptedException {
        Task task = new Task(0, "Test task", "Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2));

        // Create task
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        // Get task
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/0"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        Task returnedTask = gson.fromJson(getResponse.body(), Task.class);
        assertNotNull(returnedTask);
        assertEquals(task.getName(), returnedTask.getName());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 5, 27, 14, 0), Duration.ofHours(3));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Get all tasks
        HttpRequest getAllRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> getAllResponse = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getAllResponse.statusCode());

        Task[] tasks = gson.fromJson(getAllResponse.body(), Task[].class);
        assertNotNull(tasks);
        assertEquals(2, tasks.length);
        assertEquals(task1.getName(), tasks[0].getName());
        assertEquals(task2.getName(), tasks[1].getName());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Test task", "Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2));
        taskManager.createTask(task);

        // Delete task
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());

        // Get all tasks
        HttpRequest getAllRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> getAllResponse = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getAllResponse.statusCode());

        Task[] tasks = gson.fromJson(getAllResponse.body(), Task[].class);
        assertEquals(0, tasks.length);
    }

    @Test
    public void testCreateAndGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Test Epic", "Epic Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2), taskManager);

        // Create epic
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        // Get epic
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        Epic returnedEpic = gson.fromJson(getResponse.body(), Epic.class);
        assertNotNull(returnedEpic);
        assertEquals(epic.getName(), returnedEpic.getName());
    }


    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2), taskManager);
        Epic epic2 = new Epic(2, "Epic 2", "Description 2", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 5, 27, 14, 0), Duration.ofHours(3), taskManager);

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        // Get all epics
        HttpRequest getAllRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();
        HttpResponse<String> getAllResponse = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getAllResponse.statusCode());

        Epic[] epics = gson.fromJson(getAllResponse.body(), Epic[].class);
        assertNotNull(epics);
        assertEquals(2, epics.length);
        assertEquals(epic1.getName(), epics[0].getName());
        assertEquals(epic2.getName(), epics[1].getName());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Test Epic", "Epic Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2), taskManager);
        taskManager.createEpic(epic);

        // Delete epic
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());

        // Get all epics
        HttpRequest getAllRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();
        HttpResponse<String> getAllResponse = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getAllResponse.statusCode());

        Epic[] epics = gson.fromJson(getAllResponse.body(), Epic[].class);
        assertEquals(0, epics.length);
    }

    @Test
    public void testCreateAndGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Test Epic", "Epic Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2));
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Test Subtask", "Subtask Description", TaskStatus.NEW,
                epic.getTaskId(), LocalDateTime.of(2024, 5, 26, 13, 0), Duration.ofHours(1));

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        Subtask returnedSubtask = gson.fromJson(getResponse.body(), Subtask.class);
        assertNotNull(returnedSubtask);
        assertEquals(subtask.getName(), returnedSubtask.getName());
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Test Epic", "Epic Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2), taskManager);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", TaskStatus.NEW,
                epic.getTaskId(), LocalDateTime.of(2024, 5, 26, 13, 0), Duration.ofHours(1));
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description 2", TaskStatus.IN_PROGRESS,
                epic.getTaskId(), LocalDateTime.of(2024, 5, 27, 14, 0), Duration.ofHours(1));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Get all subtasks
        HttpRequest getAllRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();
        HttpResponse<String> getAllResponse = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response received: " + getAllResponse.body());
        assertEquals(200, getAllResponse.statusCode());

        Subtask[] subtasks = gson.fromJson(getAllResponse.body(), Subtask[].class);
        assertNotNull(subtasks);
        assertEquals(2, subtasks.length);
        assertEquals(subtask1.getName(), subtasks[0].getName());
        assertEquals(subtask2.getName(), subtasks[1].getName());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Test Epic", "Epic Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2), taskManager);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Test Subtask", "Subtask Description", TaskStatus.NEW,
                epic.getTaskId(), LocalDateTime.of(2024, 5, 26, 13, 0), Duration.ofHours(1));
        taskManager.createSubtask(subtask);

        // Delete subtask
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());

        // Get all subtasks
        HttpRequest getAllRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();
        HttpResponse<String> getAllResponse = client.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getAllResponse.statusCode());

        Subtask[] subtasks = gson.fromJson(getAllResponse.body(), Subtask[].class);
        assertEquals(0, subtasks.length);
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.IN_PROGRESS,
                LocalDateTime.now().plusHours(1), Duration.ofHours(2));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
        assertEquals("Task 1", tasks[0].getName());
        assertEquals("Task 2", tasks[1].getName());
    }

    @Test
    public void testMethodNotAllowed() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());
        assertEquals("Метод не поддерживается", response.body());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.IN_PROGRESS,
                LocalDateTime.now().plusHours(1), Duration.ofHours(2));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.getTaskById(1); // Добавляем задачи в историю
        taskManager.getTaskById(2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, history.length);
        System.out.println(Arrays.toString(history));
    }

}
