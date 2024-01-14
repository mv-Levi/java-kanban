import java.util.*;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int taskIdCounter = 1;

    public Task createTask(String name, String description, TaskStatus status) {
        Task task = new Task(taskIdCounter++, name, description, status);
        tasks.put(task.getTaskId(), task);
        return task;
    }

    public Epic createEpic(String name, String description, TaskStatus status) {
        Epic epic = new Epic(taskIdCounter++, name, description, status);
        epics.put(epic.getTaskId(), epic);
        tasks.put(epic.getTaskId(), epic);
        return epic;
    }

    public Subtask createSubtask(String name, String description, TaskStatus status, Epic epic) {
        Subtask subtask = new Subtask(taskIdCounter++, name, description, status, epic);
        subtasks.put(subtask.getTaskId(), subtask);
        tasks.put(subtask.getTaskId(), subtask);
        epic.addSubtask(subtask);
        return subtask;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }


    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }


    public void updateTask(Task newTask) {
        if (tasks.containsKey(newTask.getTaskId())) {
            tasks.put(newTask.getTaskId(), newTask);
            if (newTask.isEpic()) {
                epics.put(newTask.getTaskId(), (Epic) newTask);
            } else if (newTask.isSubtask()) {
                subtasks.put(newTask.getTaskId(), (Subtask) newTask);
            }
        } else {
            // Обработка ошибки: задача не найдена
            System.out.println("Задача не найдена");
        }
    }

    public void removeTaskById(int taskId) {
        Task taskToRemove = tasks.remove(taskId);
        if (taskToRemove != null) {
            removeTaskAndSubtasks(taskToRemove);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    private void removeTaskAndSubtasks(Task task) {
        if (task.isEpic()) {
            Epic epic = (Epic) task;
            epics.remove(epic.getTaskId());

            for (Subtask subtask : epic.getSubtasks()) {
                removeTaskAndSubtasks(subtask);
            }
        } else if (task.isSubtask()) {
            subtasks.remove(task.getTaskId());
        }
    }

    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        return epics.get(epicId).getSubtasks();
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            if (epic.getSubtasks().isEmpty() || epic.getSubtasks().stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW)) {
                epic.setStatus(TaskStatus.NEW);
            } else if (epic.getSubtasks().stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE)) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
            updateTask(epic); // Обновляем эпик в коллекции задач
        } else {
            // Обработка ошибки: эпик не найден
            System.out.println("Epic not found");
        }
    }
}