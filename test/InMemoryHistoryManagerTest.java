import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void testAddAndGetHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);

        // Добавляем первую задачу
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size()); // Проверяем размер истории
        assertEquals(task1, history.get(0)); // Проверяем наличие первой задачи в истории

        // Добавляем вторую задачу, которая перезапишет первую
        historyManager.add(task2);
        history = historyManager.getHistory();
        assertEquals(1, history.size()); // Проверяем, что размер истории остался 1
        assertEquals(task2, history.get(0)); // Проверяем, что в истории теперь вторая задача
    }


    @Test
    void testRemove() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getTaskId());
        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task1));
    }




    @Test
    void testRemoveNonExistingTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        historyManager.add(task1);

        historyManager.remove(12345);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task1));
    }
}
