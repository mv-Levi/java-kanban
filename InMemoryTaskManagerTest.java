import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }
    @Test
    public void testSubtaskHasEpic() {
        Epic epic = new Epic(1, "Epic", "Description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1",
                TaskStatus.NEW, epic, LocalDateTime.of(2022, 1, 1, 9, 0), Duration.ofHours(2));
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals(epic, subtask.getEpic(), "Подзадача должна иметь связанный эпик");
    }

    @Test
    public void shouldBePositiveAddAndRetrieveTasksById() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task(1,"Task 1", "Description 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        Epic epic = new Epic(1,"Epic 1", "Description 1", TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofHours(2));
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1",
                TaskStatus.NEW, epic, LocalDateTime.of(2022, 1, 1, 9, 0), Duration.ofHours(2));

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        assertEquals(task, taskManager.getTaskById(task.getTaskId()));
        assertEquals(epic, taskManager.getEpicById(epic.getTaskId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getTaskId()));
    }

    @Test
    public void shoildBePositiveWhenTaskIdConflictResolution() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task taskWithId = new Task(1,"Task with ID", "Description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));

        Task taskWithoutId = new Task(2,"Task without ID", "Description", TaskStatus.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofHours(2));

        taskManager.createTask(taskWithId);
        taskWithId.setTaskId(42);
        taskManager.createTask(taskWithoutId);

        assertEquals(42, taskWithId.getTaskId());
        assertNotEquals(0, taskWithoutId.getTaskId());
    }

    @Test
    public void testTaskIntervalOverlap() {
        LocalDateTime startTime1 = LocalDateTime.of(2021, Month.JANUARY, 1, 10, 0);
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW,
                startTime1, Duration.ofHours(1));

        LocalDateTime startTime2 = LocalDateTime.of(2021, Month.JANUARY, 1, 10, 30);
        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.NEW,
                startTime2, Duration.ofHours(1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertTrue(taskManager.checkForOverlap(task1, task2), "Задачи должны перекрываться.");
    }

    @Test
    void testGetPrioritizedTasks() {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW, LocalDateTime.of(2022, Month.JANUARY, 1, 12, 0), Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.NEW, LocalDateTime.of(2022, Month.JANUARY, 1, 10, 0), Duration.ofHours(2));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> tasks = new ArrayList<>(taskManager.getPrioritizedTasks());

        assertTrue(tasks.indexOf(task2) < tasks.indexOf(task1), "Task 2 должен быть перед Task 1, так как начинается раньше");
    }

}