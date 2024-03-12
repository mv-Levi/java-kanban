public class Main {
    public static void main(String[] args) {
        // Создаем менеджер задач
        TaskManager taskManager = Managers.getDefault();

        // Создаем менеджер истории
        HistoryManager historyManager = Managers.getDefaultHistory();

        // Создаем задачи
        Task task1 = new Task(1, "Description for Task 1", "ad", TaskStatus.NEW);
        Task task2 = new Task(2, "Description for Task 2", "dada", TaskStatus.IN_PROGRESS);
        Epic epic1 = new Epic(1,"Epic 1", "Description for Epic 1", TaskStatus.NEW);
        Epic epic2 = new Epic(2,"Epic 2", "Description for Epic 2", TaskStatus.DONE);

        // Создаем подзадачи для первого эпика
        Subtask subtask1 = new Subtask(1,"Subtask 1", "Description for Subtask 1", TaskStatus.NEW, epic1);
        Subtask subtask2 = new Subtask(2,"Subtask 2", "Description for Subtask 2", TaskStatus.IN_PROGRESS, epic1);
        Subtask subtask3 = new Subtask(3,"Subtask 3", "Description for Subtask 3", TaskStatus.DONE, epic1);

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
        historyManager.add(task1); // Добавляем задачу 1 в историю
        System.out.println(taskManager.getTaskById(2));
        historyManager.add(task2); // Добавляем задачу 2 в историю
        System.out.println(taskManager.getEpicById(3));
        historyManager.add(epic1); // Добавляем эпик 1 в историю
        System.out.println(taskManager.getEpicById(4));
        historyManager.add(epic2); // Добавляем эпик 2 в историю

        // Печатаем историю после каждого запроса
        System.out.println("History after each request:");
        System.out.println(historyManager.getHistory());

        // Удаление задачи, которая есть в истории
        System.out.println("\nAfter removing Task 1:");
        taskManager.removeTaskById(1);
        historyManager.remove(1); // Удаляем задачу 1 из истории
        System.out.println("History after removing Task 1:");
        System.out.println(historyManager.getHistory());

        // Удаление эпика с тремя подзадачами
        System.out.println("\nAfter removing Epic 1:");
        taskManager.removeEpicById(3);
        historyManager.remove(3); // Удаляем эпик 1 из истории
        System.out.println("History after removing Epic 1:");
        System.out.println(historyManager.getHistory());
    }
}
