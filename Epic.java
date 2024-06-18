import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

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
        Map<Integer, Subtask> subtaskMap = new HashMap<>();
        for (int subtaskId : subtaskIds) {
            Subtask subtask = taskManager.getSubtaskById(subtaskId);
            subtaskMap.put(subtaskId, subtask);
        }

        LocalDateTime earliest = subtaskMap.values().stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.setStartTime(earliest);
    }


    public void updateEndTime() {
        Map<Integer, Subtask> subtaskMap = new HashMap<>();
        for (int subtaskId : subtaskIds) {
            Subtask subtask = taskManager.getSubtaskById(subtaskId);
            subtaskMap.put(subtaskId, subtask);
        }

        LocalDateTime latest = subtaskMap.values().stream()
                .map(subtask -> subtask.getStartTime().plus(subtask.getDuration()))
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        this.setEndTime(latest);
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
        return String.format("Epic %d: %s (%s), StartTime: %s, Duration: %d minutes, subtasks: %s",
                getTaskId(),
                getName(),
                getStatus(),
                getStartTime(),
                getDuration().toMinutes(),
                subtaskIds);
    }

}
