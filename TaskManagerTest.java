import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    public abstract void setUp();

    @Test
    public void testCreateTask() {
        Task task = new Task(1, "Test task", "Description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2)
        );
        taskManager.createTask(task);
        assertNotNull(taskManager.getTaskById(1), "Задача должна быть создана и доступна для извлечения.");
    }

    @Test
    public void testRemoveTask() {
        Task task = new Task(1, "Test task", "Description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2)
        );
        taskManager.createTask(task);
        taskManager.removeTaskById(1);
        assertNull(taskManager.getTaskById(1), "Задача должна быть удалена");
    }

    @Test
    public void testRemoveSubtaskUpdatesEpic() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic(1,"Epic 1", "Description 1", TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofHours(2), taskManager);
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1",
                TaskStatus.NEW, epic.getTaskId(), LocalDateTime.of(2022, 1, 1, 9, 0), Duration.ofHours(2));

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        assertTrue(epic.getSubtaskIds().contains(subtask.getTaskId()));

        taskManager.removeSubtaskById(subtask.getTaskId());

        assertFalse(epic.getSubtaskIds().contains(subtask.getTaskId()));
    }

    @Test
    public void testSettersUpdateManagerData() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task(1,"Task 1", "Description 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        taskManager.createTask(task);

        // Проверяем, что данные менеджера обновляются при изменении данных задачи через сеттеры
        task.setName("New Name");
        task.setDescription("New Description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals("New Name", taskManager.getTaskById(task.getTaskId()).getName());
        assertEquals("New Description", taskManager.getTaskById(task.getTaskId()).getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(task.getTaskId()).getStatus());
    }

    @Test
    public void testSubtaskRemovalFromEpic() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic(1,"Epic 1", "Description 1", TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofHours(2), taskManager);
        Subtask subtask1 = new Subtask(2, "Early Subtask", "Starts early", TaskStatus.NEW, epic.getTaskId(),
                LocalDateTime.of(2022, 1, 1, 9, 0), Duration.ofHours(2));
        Subtask subtask2 = new Subtask(3, "Late Subtask", "Starts later", TaskStatus.NEW, epic.getTaskId(),
                LocalDateTime.of(2022, 1, 1, 11, 0), Duration.ofHours(3));


        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertTrue(epic.getSubtaskIds().contains(subtask1.getTaskId()));
        assertTrue(epic.getSubtaskIds().contains(subtask2.getTaskId()));


        taskManager.removeSubtaskById(subtask1.getTaskId());

        assertFalse(epic.getSubtaskIds().contains(subtask1.getTaskId()));
        assertTrue(epic.getSubtaskIds().contains(subtask2.getTaskId()));
    }

    @Test
    public void testTaskSetterUpdatesManagerData() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task(1,"Task 1", "Description 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        taskManager.createTask(task);

        // Проверяем, что данные менеджера обновляются при изменении данных задачи через сеттеры
        task.setName("New Name");
        task.setDescription("New Description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals("New Name", taskManager.getTaskById(task.getTaskId()).getName());
        assertEquals("New Description", taskManager.getTaskById(task.getTaskId()).getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(task.getTaskId()).getStatus());
    }
}
