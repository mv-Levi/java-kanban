import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Создаем менеджер задач
        TaskManager taskManager = Managers.getDefault();

        // Создаем менеджер истории
        HistoryManager historyManager = Managers.getDefaultHistory();


        // Создание объектов Task
        LocalDateTime startTimeTask1 = LocalDateTime.of(2022, 5, 20, 8, 30);
        Duration durationTask1 = Duration.ofHours(3);

        LocalDateTime startTimeTask2 = LocalDateTime.of(2022, 5, 20, 10, 0);
        Duration durationTask2 = Duration.ofHours(2);

        Task task1 = new Task(1, "Task 1", "Description for Task 1", TaskStatus.NEW, startTimeTask1, durationTask1);
        Task task2 = new Task(2, "Task 2", "Description for Task 2", TaskStatus.IN_PROGRESS, startTimeTask2, durationTask2);

        // Создание объектов Epic
        LocalDateTime startTimeEpic1 = LocalDateTime.of(2022, 5, 21, 9, 0);
        Duration durationEpic1 = Duration.ofHours(6);

        LocalDateTime startTimeEpic2 = LocalDateTime.of(2022, 5, 22, 10, 0);
        Duration durationEpic2 = Duration.ofHours(4);

        Epic epic1 = new Epic(1, "Epic 1", "Description for Epic 1", TaskStatus.NEW, startTimeEpic1, durationEpic1);
        Epic epic2 = new Epic(2, "Epic 2", "Description for Epic 2", TaskStatus.DONE, startTimeEpic2, durationEpic2);

        // Создаем подзадачи для первого эпика
        Subtask subtask1 = new Subtask(
                1,
                "Subtask 1",
                "Description for Subtask 1",
                TaskStatus.NEW,
                epic1,
                LocalDateTime.now(),
                Duration.ofHours(2)
        );

        Subtask subtask2 = new Subtask(
                2,
                "Subtask 2",
                "Description for Subtask 2",
                TaskStatus.IN_PROGRESS,
                epic1,
                LocalDateTime.now().plusHours(1),
                Duration.ofHours(3)
        );

        Subtask subtask3 = new Subtask(
                3,
                "Subtask 3",
                "Description for Subtask 3",
                TaskStatus.DONE,
                epic1,
                LocalDateTime.now().plusHours(2),
                Duration.ofHours(1)
        );

        // Добавляем задачи в менеджер задач
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        // Запросы на задачи в разных порядках
        System.out.println("Requesting tasks in different order:");
        System.out.println(taskManager.getTaskById(1));
        historyManager.add(task1);
        System.out.println(taskManager.getTaskById(2));
        historyManager.add(task2);
        System.out.println(taskManager.getEpicById(3));
        historyManager.add(epic1);
        System.out.println(taskManager.getEpicById(4));
        historyManager.add(epic2);

        // Печатаем историю после каждого запроса
        System.out.println("History after each request:");
        System.out.println(historyManager.getHistory());

        // Удаление задачи, которая есть в истории
        System.out.println("\nAfter removing Task 1:");
        taskManager.removeTaskById(1);
        historyManager.remove(1);
        System.out.println("History after removing Task 1:");
        System.out.println(historyManager.getHistory());

        // Удаление эпика с тремя подзадачами
        System.out.println("\nAfter removing Epic 1:");
        taskManager.removeEpicById(3);
        historyManager.remove(3);
        System.out.println("History after removing Epic 1:");
        System.out.println(historyManager.getHistory());
    }
}
