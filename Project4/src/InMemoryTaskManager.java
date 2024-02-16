import java.lang.reflect.Array;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private ArrayList<Task> viewedTasks = new ArrayList<>();
    private HistoryManager historyManager;
    private int taskIdCounter = 1;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager(); // Создаем InMemoryHistoryManager по умолчанию
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public void createTask(Task task) {
        task.setTaskId(taskIdCounter++);
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setTaskId(taskIdCounter++);
        epics.put(epic.getTaskId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setTaskId(taskIdCounter++);
        subtasks.put(subtask.getTaskId(), subtask);
        subtask.getEpic().addSubtask(subtask);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            removeTaskAndSubtasks(epic);
        }
        epics.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task); // Добавляем задачу в историю при просмотре
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            updateViewedTasks(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            updateViewedTasks(epic);
        }
        return epic;
    }

    @Override
    public void updateTaskById(int id, Task updatedTask) {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Task with id " + id + " does not exist.");
        }

        // Получаем текущую задачу
        Task currentTask = tasks.get(id);

        // Обновляем поля задачи
        currentTask.setName(updatedTask.getName());
        currentTask.setDescription(updatedTask.getDescription());
        currentTask.setStatus(updatedTask.getStatus());

        // Обновляем задачу в истории просмотров
        historyManager.add(currentTask);
    }

    @Override
    public void updateSubtaskById(int subtaskId, Subtask newSubtask) {
        if (subtasks.containsKey(subtaskId)) {
            Subtask existingSubtask = subtasks.get(subtaskId);

            existingSubtask.setName(newSubtask.getName());
            existingSubtask.setDescription(newSubtask.getDescription());
            existingSubtask.setStatus(newSubtask.getStatus());

            Epic epic = existingSubtask.getEpic();
            updateEpicStatus(epic, existingSubtask);

        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    private void updateEpicStatus(Epic epic, Subtask updatedSubtask) {
        TaskStatus newEpicStatus = calculateEpicStatus(epic);
        if (newEpicStatus != epic.getStatus()) {
            epic.setStatus(newEpicStatus);

            for (Subtask subtask : epic.getSubtasks()) {
                if (subtask.equals(updatedSubtask)) {
                    updateEpicStatus(epic, subtask);
                }
            }
        }
    }

    private TaskStatus calculateEpicStatus(Epic epic) {
        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask subtask : epic.getSubtasks()) {
            switch (subtask.getStatus()) {
                case NEW:
                    allDone = false;
                    anyInProgress = true;
                    break;
                case IN_PROGRESS:
                    allDone = false;
                    anyInProgress = true;
                    break;
                case DONE:
                    break;
            }

            if (anyInProgress) {
                break;
            }
        }
        if (allDone) {
            return TaskStatus.DONE;
        } else if (anyInProgress) {
            return TaskStatus.IN_PROGRESS;
        } else {
            return TaskStatus.NEW;
        }
    }


    @Override
    public void updateEpicById(int epicId, Epic newEpic) {
        if (epics.containsKey(epicId)) {
            newEpic.setTaskId(epicId);
            epics.put(epicId, newEpic);
        } else {
            System.out.println("Эпик не найден");
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        Task taskToRemove = tasks.remove(taskId);
        if (taskToRemove != null) {
            removeTaskAndSubtasks(taskToRemove);
            historyManager.remove(taskId);
        } else {
            System.out.println("Task not found");
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        Subtask subtaskToRemove = subtasks.remove(subtaskId);
        if (subtaskToRemove != null) {
            historyManager.remove(subtaskId);

            // Удаляем подзадачу из списка подзадач в соответствующем эпике, если она есть
            for (Epic epic : epics.values()) {
                if (epic.getSubtasks().contains(subtaskToRemove)) {
                    epic.removeSubtask(subtaskToRemove);
                    break; // Предполагаем, что подзадача может принадлежать только одному эпику
                }
            }
        } else {
            System.out.println("Subtask not found");
        }
    }



    @Override
    public void removeEpicById(int epicId) {
        Epic epicToRemove = epics.remove(epicId);
        if (epicToRemove != null) {
            removeTaskAndSubtasks(epicToRemove);
            historyManager.remove(epicId);
        } else {
            System.out.println("Epic not found");
        }
    }

    public ArrayList<Task> getHistory() {
        return new ArrayList<>(viewedTasks);
    }

    private void updateViewedTasks(Task task) {
        // Добавляем задачу в начало списка просмотренных
        viewedTasks.add(0, task);

        // Ограничиваем список до последних 10 просмотренных
        if (viewedTasks.size() > 10) {
            viewedTasks.remove(viewedTasks.size() - 1);
        }
    }

    private void removeTaskAndSubtasks(Task task) {
        if (task.isEpic()) {
            Epic epic = (Epic) task;
            epics.remove(epic.getTaskId());

            for (Subtask subtask : epic.getSubtasks()) {
                removeTaskAndSubtasks(subtask);
            }
            historyManager.remove(epic.getTaskId());
        } else if (task.isSubtask()) {
            subtasks.remove(task.getTaskId());
            historyManager.remove(task.getTaskId());
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        return epics.get(epicId).getSubtasks();
    }
}