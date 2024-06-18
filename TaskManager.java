import java.util.Collection;
import java.util.List;

public interface TaskManager {
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask);

    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getAllSubtasks();

    void removeAllTasks();
    void removeAllEpics();
    void removeAllSubtasks();

    Task getTaskById(int taskId) throws NotFoundException;
    Subtask getSubtaskById(int subtaskId) throws NotFoundException;
    Epic getEpicById(int epicId) throws NotFoundException;

    void updateTaskById(int taskId, Task task);
    void updateSubtaskById(int subtaskId, Subtask subtask);
    void updateEpicById(int epicId, Epic epic);

    void removeTaskById(int taskId) throws NotFoundException;
    void removeSubtaskById(int subtaskId) throws NotFoundException;
    void removeEpicById(int epicId) throws NotFoundException;

    List<Task> getHistory();

    Collection<Task> getPrioritizedTasks();

    List<Subtask> getSubtasksOfEpic(int epicId) throws NotFoundException;
}
