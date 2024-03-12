import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("task_manager", ".txt");
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    public void testSaveEmptyFile() throws ManagerSaveException {
        taskManager.saveToFile(); // Сохраняем пустой файл
        assertTrue(tempFile.exists()); // Проверяем, что файл существует
        assertEquals(0, tempFile.length()); // Проверяем, что размер файла равен нулю
    }

    @Test
    void testSaveAndLoadTasks() throws ManagerSaveException, IOException {
        File tempFile = File.createTempFile("temp_tasks", ".txt");
        tempFile.deleteOnExit();

        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task(1,"Task1", "Description task1", TaskStatus.NEW);
        Task task2 = new Task(2,"Task2", "Description task2", TaskStatus.IN_PROGRESS);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.saveToFile();

        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedTaskManager.getAllTasks();

        assertEquals(2, loadedTasks.size()); // Эта строка вызывает ошибку
        assertEquals(task1, loadedTasks.get(0));
        assertEquals(task2, loadedTasks.get(1));
    }
}
