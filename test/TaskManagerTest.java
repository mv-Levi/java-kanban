import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    @Test
    public void testRemoveSubtaskUpdatesEpic() {
        TaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Description 1", TaskStatus.DONE);
        Subtask subtask = new Subtask("Subtask 1", "Description 1",
                TaskStatus.IN_PROGRESS, epic);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        assertTrue(epic.getSubtasks().contains(subtask));

        taskManager.removeSubtaskById(subtask.getTaskId());

        assertFalse(epic.getSubtasks().contains(subtask));
    }

    @Test
    public void testSettersUpdateManagerData() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
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

        Epic epic = new Epic("Epic 1", "Description 1", TaskStatus.DONE);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", TaskStatus.IN_PROGRESS, epic);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", TaskStatus.IN_PROGRESS, epic);

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertTrue(epic.getSubtasks().contains(subtask1));
        assertTrue(epic.getSubtasks().contains(subtask2));

        // Удаляем subtask1
        taskManager.removeSubtaskById(subtask1.getTaskId());

        assertFalse(epic.getSubtasks().contains(subtask1));
        assertTrue(epic.getSubtasks().contains(subtask2));
    }

    @Test
    public void testTaskSetterUpdatesManagerData() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
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
