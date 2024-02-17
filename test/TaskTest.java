import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    public void shouldBeEqualWhenTasksHaveEqualId() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);

        task1.setTaskId(1);
        task2.setTaskId(1);

        assertEquals(task1, task2);
    }

    @Test
    public void shouldBePositiveWhentestTaskInheritanceEqualityById() {
        Epic epic1 = new Epic("Epic 1", "Description 1", TaskStatus.DONE);
        Epic epic2 = new Epic("Epic 2", "Description 2", TaskStatus.NEW);

        epic1.setTaskId(1);
        epic2.setTaskId(1);

        assertEquals(epic1, epic2);
    }
}