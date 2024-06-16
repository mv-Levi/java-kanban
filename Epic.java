import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;
    private transient TaskManager taskManager;

    public Epic(int taskId, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(taskId, name, description, status, startTime, duration);
    }

    public Epic(int taskId, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration, TaskManager taskManager) {
        super(taskId, name, description, status, startTime, duration);
        this.taskManager = taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtask(Subtask subtask) {
        subtaskIds.add(subtask.getTaskId());
        System.out.println("Подзадача " + subtask.getTaskId() + " добавлена в эпик " + this.getTaskId());
    }
    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public void updateTimings() {
        updateStartTime();
        updateEndTime();
    }
    public void updateStartTime() {
        LocalDateTime earliest = null;
        for (int subtaskId : subtaskIds) {
            Subtask subtask = taskManager.getSubtaskById(subtaskId);
            if (earliest == null || (subtask.getStartTime() != null && subtask.getStartTime().isBefore(earliest))) {
                earliest = subtask.getStartTime();
            }
        }
        this.setStartTime(earliest);
    }


    public void updateEndTime() {
        LocalDateTime latest = null;
        for (int subtaskId : subtaskIds) {
            Subtask subtask = taskManager.getSubtaskById(subtaskId);
            LocalDateTime subtaskEndTime = subtask.getStartTime().plus(subtask.getDuration());
            if (latest == null || (subtaskEndTime != null && subtaskEndTime.isAfter(latest))) {
                latest = subtaskEndTime;
            }
        }
        this.endTime = latest;
    }


    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
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
        return Objects.hash(getTaskId());
    }

    @Override
    public String toString() {
        return String.format("Epic %d: %s (%s), StartTime: %s, Duration: %d minutes, Subtasks: %s",
                getTaskId(),
                getName(),
                getStatus(),
                getStartTime(),
                getDuration().toMinutes(),
                subtaskIds);
    }

}
