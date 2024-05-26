import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    private int taskId;
    private String name;
    private TaskStatus status;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = createTaskFromString(line);
                taskManager.createTask(task);
            }
        }
        return taskManager;
    }

    private static Task createTaskFromString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        LocalDateTime startTime = parts.length > 5 ? LocalDateTime.parse(parts[5], formatter) : null;
        Duration duration = parts.length > 6 ? Duration.ofMinutes(Long.parseLong(parts[6])) : null;
        Epic epic = null;
        if (parts.length > 7 && !parts[7].isEmpty()) {
            int epicId = Integer.parseInt(parts[7]);
            InMemoryTaskManager taskManager = new InMemoryTaskManager();
            epic = taskManager.getEpicById(epicId);
        }

        switch (type) {
            case "Task":
                return new Task(id, name, description, status, startTime, duration);
            case "Epic":
                return new Epic(id, name, description, status, startTime, duration);
            case "Subtask":
                return new Subtask(id, name, description, status, epic, startTime, duration);
            default:
                throw new IllegalArgumentException("Неизвестный тип задания: " + type);
        }
    }




    @Override
    public String toString() {
        return String.valueOf(taskId);
    }

    // Метод создания задачи из строки
    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        LocalDateTime startTime = LocalDateTime.parse(parts[6]); // Пример для dateTime
        Duration duration = Duration.parse(parts[7]); // Пример для duration
        Epic epic = null;
        if (type == TaskType.EPIC) {
            int numSubtasks = Integer.parseInt(parts[5]);
            epic = new Epic(id, name, description, status, startTime, duration);
            for (int i = 0; i < numSubtasks; i++) {
                epic.addSubtask(null); // Добавляем пустые подзадачи
            }
        } else {
            int epicId = Integer.parseInt(parts[5]);
            InMemoryTaskManager taskManager = new InMemoryTaskManager();
            epic = taskManager.getEpicById(epicId);// Получаем эпик по его идентификатору
        }
        Task task = new Task(id, name, description, status, startTime, duration );
        return task;
    }

    // Метод сохранения истории в строку
    public static String historyToString(HistoryManager manager) {
        List<Task> tasks = manager.getHistory();
        return tasks.stream()
                .map(task -> String.valueOf(task.getTaskId()))
                .collect(Collectors.joining(","));
    }

    // Метод восстановления истории из строки
    public static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(","))
                .filter(part -> !part.isEmpty()) // Убедитесь, что части не пустые
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public void saveToFile() throws ManagerSaveException {
        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : getAllTasks()) {
                try {
                    writer.write(task.toFileString() + "\n");
                } catch (IOException e) {
                    throw new ManagerSaveException("Error saving manager state to file.", e);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error creating FileWriter.", e);
        }
    }



    @Override
    public void createTask(Task task) {
        super.createTask(task);
        try {
            saveToFile();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTaskById(int taskId, Task task) {
        super.updateTaskById(taskId, task);
        try {
            saveToFile();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        try {
            saveToFile();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            saveToFile();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEpicById(int epicId, Epic epic) {
        super.updateEpicById(epicId, epic);
        try {
            saveToFile();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        try {
            saveToFile();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        try {
            saveToFile();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSubtaskById(int subtaskId, Subtask subtask) {
        super.updateSubtaskById(subtaskId, subtask);
        try {
            saveToFile();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        try {
            saveToFile();
        } catch (ManagerSaveException e) {
            e.printStackTrace();
        }
    }
}
