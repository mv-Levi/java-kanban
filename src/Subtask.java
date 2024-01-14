
public class Subtask extends Task {
    private Epic epic;

    public Subtask(int taskId, String name, String description, TaskStatus status, Epic epic) {
        super(taskId, name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean isSubtask() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Subtask subtask = (Subtask) obj;
        return getTaskId() == subtask.getTaskId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getTaskId());
    }

    @Override
    public String toString() {
        return String.format("Subtask %d: %s (%s) [Epic: %s]", getTaskId(), getname(), getStatus(), epic.getname());
    }
}
