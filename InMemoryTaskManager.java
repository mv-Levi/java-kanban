import java.lang.reflect.Array;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private List<Task> viewedTasks = new ArrayList<>();
    private HistoryManager historyManager;
    private int taskIdCounter = 1;
    private TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager(); // Создаем InMemoryHistoryManager по умолчанию
        this.prioritizedTasks = new TreeSet<>(new TaskComparator());
    }

    @Override
    public void createTask(Task task) {
        tasks.put(task.getTaskId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        } else {
            System.out.println("Задача " + task.getTaskId() + " не добавлена в приоритетный список из-за отсутствия startTime.");
        }
        System.out.println("Задача добавлена: " + task);
    }

    public Collection<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        subtask.getEpic().setStatus(calculateEpicStatus(subtask.getEpic()));
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
        epics.values().forEach(epic -> epic.getSubtasks().clear());
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.values().forEach(this::removeTaskAndSubtasks);
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
        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private TaskStatus calculateEpicStatus(Epic epic) {
        boolean hasNew = false;
        boolean hasDone = false;

        for (Subtask subtask : epic.getSubtasks()) {
            switch (subtask.getStatus()) {
                case NEW:
                    hasNew = true;
                    break;
                case DONE:
                    hasDone = true;
                    break;
                case IN_PROGRESS:
                    return TaskStatus.IN_PROGRESS;  // Возвращает IN_PROGRESS, если любая подзадача в процессе
            }
        }

        if (hasNew && hasDone) {
            return TaskStatus.IN_PROGRESS;  // Возвращает IN_PROGRESS, если есть и выполненные, и не начатые подзадачи
        } else if (hasDone) {
            return TaskStatus.DONE;  // Все подзадачи выполнены
        } else {
            return TaskStatus.NEW;  // Все подзадачи новые или нет подзадач
        }
    }

    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        // Проверяем, что один интервал начинается до окончания другого и заканчивается после начала другого
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    public void addTask(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime(); // Предположим, что в Task есть метод getEndTime

        boolean isOverlapping = prioritizedTasks.stream()
                .anyMatch(existingTask ->
                        isOverlapping(startTime, endTime, existingTask.getStartTime(), existingTask.getEndTime())
                );

        if (!isOverlapping) {
            tasks.put(task.getTaskId(), task);
            prioritizedTasks.add(task);
            taskIdCounter++;
        } else {
            throw new IllegalArgumentException("Task overlaps with existing tasks.");
        }
    }

    public void updateTaskById(int taskId, Task updatedTask) {
        Task currentTask = tasks.get(taskId);
        if (currentTask == null) {
            throw new IllegalArgumentException("Task with id " + taskId + " does not exist.");
        }

        LocalDateTime startTime = updatedTask.getStartTime();
        LocalDateTime endTime = updatedTask.getEndTime();

        boolean isOverlapping = prioritizedTasks.stream()
                .filter(t -> t.getTaskId() != taskId) // Исключаем саму задачу из проверки
                .anyMatch(existingTask ->
                        isOverlapping(startTime, endTime, existingTask.getStartTime(), existingTask.getEndTime())
                );

        if (!isOverlapping) {
            prioritizedTasks.remove(currentTask);
            tasks.put(taskId, updatedTask);
            prioritizedTasks.add(updatedTask);
        } else {
            throw new IllegalArgumentException("Updated task overlaps with existing tasks.");
        }
    }

    public boolean checkForOverlap(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getStartTime().plusMinutes(task1.getDuration().toMinutes());

        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getStartTime().plusMinutes(task2.getDuration().toMinutes());

        return start1.isBefore(end2) && start2.isBefore(end1);
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
            prioritizedTasks.remove(taskToRemove); // Удаление из приоритетного списка
            removeTaskAndSubtasks(taskToRemove); // Дополнительные действия для удаления
            historyManager.remove(taskId); // Удаление из истории
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
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpic() != null && subtask.getEpic().getTaskId() == epicId)
                .collect(Collectors.toList());
    }
}