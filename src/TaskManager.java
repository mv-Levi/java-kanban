import java.util.ArrayList;
import java.util.List;
public interface TaskManager {
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask);

    ArrayList<Task> getAllTasks();
    ArrayList<Epic> getAllEpics();
    ArrayList<Subtask> getAllSubtasks();

    void removeAllTasks();
    void removeAllEpics();
    void removeAllSubtasks();

    Task getTaskById(int taskId);
    Subtask getSubtaskById(int subtaskId);
    Epic getEpicById(int epicId);

    void updateTaskById(int taskId, Task newTask);
    void updateSubtaskById(int subtaskId, Subtask newSubtask);
    void updateEpicById(int epicId, Epic newEpic);

    void removeTaskById(int taskId);
    void removeSubtaskById(int subtaskId);
    void removeEpicById(int epicId);

    List<Task> getHistory();

    ArrayList<Subtask> getSubtasksOfEpic(int epicId);
}
