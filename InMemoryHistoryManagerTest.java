import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager(); // Предполагаем, что это ваша реализация
    }

    @Test
    public void testEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой сначала");
    }

    @Test
    public void testAddTaskToHistory() {
        Task task1 = new Task(1, "Task 1", "Описание", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size(), "В истории должна быть одна задача");
        assertSame(task1, historyManager.getHistory().get(0), "Задача в истории должна быть той, что была добавлена");
    }

    @Test
    public void testNoDuplicateEntries() {
        Task task1 = new Task(1, "Task 1", "Описание", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2));
        historyManager.add(task1);
        historyManager.add(task1); // Добавление той же задачи снова
        assertEquals(1, historyManager.getHistory().size(), "В истории не должно быть дубликатов");
    }

    @Test
    public void testRemoveFromHistory() {
        Task task1 = new Task(1, "Task 1", "Описание", TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(2));
        Task task2 = new Task(2, "Task 2", "Описание", TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(2));
        Task task3 = new Task(3, "Task 3", "Описание", TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofHours(2));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Удаление задачи из начала
        historyManager.remove(task1.getTaskId());
        assertFalse(historyManager.getHistory().contains(task1), "Task 1 должна быть удалена из истории");

        // Удаление задачи из середины
        historyManager.remove(task2.getTaskId());
        assertFalse(historyManager.getHistory().contains(task2), "Task2 2 должна быть удалена из истории");

        // Удаление задачи с конца
        historyManager.remove(task3.getTaskId());
        assertFalse(historyManager.getHistory().contains(task3), "Task 3 должна быть удалена из истории");
    }

    @Test
    void testAddAndGetHistory() {
        Task task1 = new Task(1, "Task1", "Description 1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(2));
        Task task2 = new Task(2, "Task2", "Description 2", TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofHours(2));

        // Добавляем первую задачу
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size()); // Проверяем размер истории
        assertEquals(task1, history.get(0)); // Проверяем наличие первой задачи в истории

        // Добавляем вторую задачу
        historyManager.add(task2);
        history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(1));
    }

    @Test
    void testRemoveNonExistingTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Task1", "Description 1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofHours(2));
        historyManager.add(task1);

        historyManager.remove(12345);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task1));
    }
}
