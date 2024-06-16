import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private TaskManager taskManager;
    @Test
    public void shouldBeEqualWhenTasksHaveEqualId() {
        Task task1 = new Task(1,"Task 1", "Description 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        Task task2 = new Task(2,"Task 2", "Description 2", TaskStatus.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofHours(2));

        task1.setTaskId(1);
        task2.setTaskId(1);

        assertEquals(task1, task2);
    }

    @Test
    public void shouldBePositiveWhentestTaskInheritanceEqualityById() {
        Epic epic1 = new Epic(1,"Epic 1", "Description 1", TaskStatus.DONE,
                LocalDateTime.now(), Duration.ofHours(2), taskManager);
        Epic epic2 = new Epic(2,"Epic 2", "Description 2", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2), taskManager);

        epic1.setTaskId(1);
        epic2.setTaskId(1);

        assertEquals(epic1, epic2);
    }
}