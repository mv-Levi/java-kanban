import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTest {
    private TaskManager taskManager;
    LocalDateTime startTime = LocalDateTime.of(2022, 1, 1, 9, 0);
    Duration duration = Duration.between(startTime, LocalDateTime.of(2022, 1, 1,
            14, 0));

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testEpicStatusAllNew() {
        Epic epic = new Epic(1, "Complex Epic", "Testing timings", TaskStatus.NEW, startTime,
                duration);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1",
                TaskStatus.NEW, epic, LocalDateTime.of(2022, 1, 1, 9, 0),
                Duration.ofHours(2));
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description 2",
                TaskStatus.IN_PROGRESS, epic, LocalDateTime.of(2022, 1, 1, 11, 0),
                Duration.ofHours(3));
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Epic должен быть NEW Когда все subtasks - NEW");
    }

    @Test
    public void testEpicStatusAllDone() {
        Epic epic = new Epic(1, "Epic", "Description", TaskStatus.NEW, startTime, duration);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", TaskStatus.DONE, epic, startTime, duration);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description 2", TaskStatus.DONE, epic, startTime.plusHours(1), duration);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус Epic должен быть DONE когда все подзадачи DONE");
    }

    @Test
    public void testEpicStatusMixed() {
        Epic epic = new Epic(1, "Epic", "Description", TaskStatus.NEW, startTime, duration);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", TaskStatus.NEW, epic, startTime, duration);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description 2", TaskStatus.DONE, epic, startTime.plusHours(1), duration);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Epic должен быть IN_PROGRESS когда подзадачи имеют разные статусы");
    }

    @Test
    public void testEpicStatusInProgress() {
        Epic epic = new Epic(1, "Epic", "Description", TaskStatus.NEW, startTime, duration);
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", TaskStatus.IN_PROGRESS, epic, startTime, duration);
        taskManager.createSubtask(subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Epic должен быть IN_PROGRESS когда любая подзадача IN_PROGRESS");
    }

    @Test
    public void testEpicTimingUpdates() {
        Epic epic = new Epic(1, "Complex Epic", "Testing timings", TaskStatus.NEW, startTime, duration);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1",
                TaskStatus.NEW, epic, LocalDateTime.of(2022, 1, 1, 9, 0),
                Duration.ofHours(2));
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description 2",
                TaskStatus.IN_PROGRESS, epic, LocalDateTime.of(2022, 1, 1, 11,
                0), Duration.ofHours(3));

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        epic.updateStartTime();
        epic.updateEndTime();

        assertEquals(LocalDateTime.of(2022, 1, 1, 9, 0),
                epic.getStartTime(), "Epic должен начинаться когда начинается первый subtask");
        assertEquals(LocalDateTime.of(2022, 1, 1, 14, 0),
                epic.getEndTime(), "Epic должен заканчиваться когда заканчивается последний subtask");
    }
}
