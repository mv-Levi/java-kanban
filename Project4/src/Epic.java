import java.util.ArrayList;
public class Epic extends Task{
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description, TaskStatus status) {
        super( name, description, status);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public boolean isEpicDone() {
        return subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }


    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public boolean isEpic() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return getTaskId() == epic.getTaskId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getTaskId());
    }

    @Override
    public String toString() {
        return String.format("Epic %d: %s (%s) [Subtasks: %d]", getTaskId(), getname(), getStatus(), subtasks.size());
    }
}
