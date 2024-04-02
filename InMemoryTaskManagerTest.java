import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    public void shouldBePositiveAddAndRetrieveTasksById() {
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task(1,"Task 1", "Description 1", TaskStatus.NEW);
        Epic epic = new Epic(1,"Epic 1", "Description 1", TaskStatus.DONE);
        Subtask subtask = new Subtask(1,"Subtask 1", "Description 1",
                TaskStatus.IN_PROGRESS, epic);

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

        Task taskWithId = new Task(1,"Task with ID", "Description", TaskStatus.NEW);

        Task taskWithoutId = new Task(2,"Task without ID", "Description", TaskStatus.IN_PROGRESS);

        taskManager.createTask(taskWithId);
        taskWithId.setTaskId(42);
        taskManager.createTask(taskWithoutId);

        assertEquals(42, taskWithId.getTaskId());
        assertNotEquals(0, taskWithoutId.getTaskId());
    }
}