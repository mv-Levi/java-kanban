import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

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
        Epic epic = null;
        if (parts.length > 5 && !parts[5].isEmpty()) {
            int epicId = Integer.parseInt(parts[5]);
            InMemoryTaskManager taskManager = new InMemoryTaskManager();
            epic = taskManager.getEpicById(epicId);
        }

        switch (type) {
            case "Task":
                return new Task(id, name, description, status);
            case "Epic":
                return new Epic(id, name, description, status);
            case "Subtask":
                return new Subtask(id, name, description, status, epic);
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
        Epic epic = null;
        if (type == TaskType.EPIC) {
            int numSubtasks = Integer.parseInt(parts[5]);
            epic = new Epic(id, name, description, status);
            for (int i = 0; i < numSubtasks; i++) {
                epic.addSubtask(null); // Добавляем пустые подзадачи
            }
        } else {
            int epicId = Integer.parseInt(parts[5]);
            InMemoryTaskManager taskManager = new InMemoryTaskManager();
            epic = taskManager.getEpicById(epicId);// Получаем эпик по его идентификатору
        }
        Task task = new Task(id, name, description, status);
        return task;
    }

    // Метод сохранения истории в строку
    public static String historyToString(HistoryManager manager) {
        List<Task> tasks = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        for (Task task : tasks) {
            sb.append(task.getTaskId()).append(",");
        }
        return sb.toString();
    }

    // Метод восстановления истории из строки
    public static List<Integer> historyFromString(String value) {
        String[] parts = value.split(",");
        List<Integer> taskIds = new ArrayList<>();
        for (String part : parts) {
            taskIds.add(Integer.parseInt(part));
        }
        return taskIds;
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
