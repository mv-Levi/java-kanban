import java.util.Objects;

public class Task {
    private int taskId;
    private String name;
    private String description;
    private TaskStatus status;

    public Task(int taskId, String name, String description, TaskStatus status) {
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getname() {
        return name;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public boolean isEpic() {
        return false;
    }

    public boolean isSubtask() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("Task %d: %s (%s)", taskId, name, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(taskId);
    }
}
