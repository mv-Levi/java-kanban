import java.util.Objects;

public class Task {
    private int taskId;
    private String name;
    private String description;
    private TaskStatus status;
    public Task(int taskId) {
        this.taskId = taskId;
    }

    public Task(int taskId, String name, String description, TaskStatus status) {
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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


    public String toString() {
        return String.format("Task %d: %s (%s)", taskId, name, status);
    }

    public String toFileString() {
        return String.format("%d,%s,%s,%s,%s", taskId, getClass().getSimpleName(), name, status, description);
    }


    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return taskId == task.taskId;
    }

    public int hashCode() {
        return Integer.hashCode(taskId);
    }
}
