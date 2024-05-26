import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFile;
    @Override
    @BeforeEach
    public void setUp() {
        try {
            testFile = File.createTempFile("tasks", ".txt");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Не удалось создать временный файл для тестов.");
        }
        taskManager = new FileBackedTaskManager(testFile);
    }


    @Test
    public void testTaskCreationAndRetrieval() {
        taskManager.createTask(new Task(1, "Test task", "Description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2)));
        Task retrieved = taskManager.getTaskById(1);
        assertNotNull(retrieved, "Задача должна быть доступна после создания");
        assertEquals("Test task", retrieved.getName(),
                "Название задачи должно соответствовать созданной задаче.");
    }

    @Test
    public void testSaveEmptyFile() {
        assertDoesNotThrow(() -> taskManager.saveToFile());
        assertTrue(testFile.exists());
        assertEquals(0, testFile.length());
    }

    @Test
    public void testSaveAndLoad() {
        Task task = new Task(1, "Test task", "Description", TaskStatus.NEW,
                LocalDateTime.of(2024, 5, 26, 12, 0), Duration.ofHours(2));
        taskManager.createTask(task);

        assertDoesNotThrow(() -> taskManager.saveToFile());

        FileBackedTaskManager loadedManager = null;
        try {
            loadedManager = FileBackedTaskManager.loadFromFile(testFile);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Не удалось загрузить менеджер задач из файла.");
        }

        assertNotNull(loadedManager);
        Task loadedTask = loadedManager.getTaskById(1);
        assertNotNull(loadedTask);
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());
    }

    @AfterEach
    public void tearDown() {
        // Удаляем файл после каждого теста
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

}
