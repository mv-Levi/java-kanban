import java.util.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создаем две задачи
        Task task1 = taskManager.createTask("Task 1", "Описание", TaskStatus.NEW);
        Task task2 = taskManager.createTask("Task 2", "Описание", TaskStatus.IN_PROGRESS);

        // Создаем эпик с двумя подзадачами
        Epic epic1 = taskManager.createEpic("Epic 1", "Описание Epic 1", TaskStatus.NEW);
        Subtask subtask1 = taskManager.createSubtask("Subtask", "Описание Subtask 1", TaskStatus.NEW, epic1);
        Subtask subtask2 = taskManager.createSubtask("Subtask", "Описание Subtask 2", TaskStatus.NEW, epic1);

        // Создаем эпик с одной подзадачей
        Epic epic2 = taskManager.createEpic("Epic 2", "Описание Epic 2", TaskStatus.NEW);
        Subtask subtask3 = taskManager.createSubtask("Subtask", "Описание Subtask 3", TaskStatus.NEW, epic2);

        // Выводим списки эпиков, задач и подзадач
        System.out.println("Epics:");
        for (Task task : taskManager.getAllTasks()) {
            if (task.getType() == TaskType.EPIC) {
                System.out.println(task);
            }
        }

        System.out.println("\nTasks:");
        for (Task task : taskManager.getAllTasks()) {
            if (task.getType() == TaskType.TASK) {
                System.out.println(task);
            }
        }

        System.out.println("\nSubtasks:");
        for (Task task : taskManager.getAllTasks()) {
            if (task.getType() == TaskType.SUBTASK) {
                System.out.println(task);
            }
        }

        // Изменяем статусы созданных объектов
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);
        subtask1.setStatus(TaskStatus.DONE);

        // Обновляем эпики для пересчета статусов
        taskManager.updateEpicStatus(epic1.getTaskId());
        taskManager.updateEpicStatus(epic2.getTaskId());

        // Выводим измененные статусы
        System.out.println("\nОбновленные задания и эпики:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        // Пытаемся удалить одну из задач и один из эпиков
        taskManager.removeTaskById(task1.getTaskId());
        taskManager.removeTaskById(epic2.getTaskId());

        // Выводим списки после удаления
        System.out.println("\nЗадания и эпики после удаления:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
    }
}