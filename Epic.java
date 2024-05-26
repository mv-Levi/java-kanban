import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(int taskId, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(taskId, name, description, status, startTime, duration);
        this.subtasks = new ArrayList<>();
        this.endTime = null;
    }

    public Duration calculateTotalDuration() {
        return subtasks.stream()
                .map(Subtask::getDuration)
                .filter(java.util.Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public LocalDateTime calculateEndTime() {
        return subtasks.stream()
                .map(subtask -> subtask.getStartTime().plusMinutes(subtask.getDuration().toMinutes()))
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateStartTime();
        updateEndTime();
        updateDuration();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStartTime();
        updateDuration();
        updateEndTime();
    }

    public void updateDuration() {
        Duration newDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(java.util.Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        setDuration(newDuration);
    }

    public void updateStartTime() {
        LocalDateTime newStartTime = subtasks.stream()
                .map(Subtask::getStartTime) // используем геттер для безопасного доступа
                .filter(java.util.Objects::nonNull) // фильтруем null значения
                .min(LocalDateTime::compareTo) // находим минимальное время
                .orElse(null);
        setStartTime(newStartTime); // используем сеттер для обновления startTime
    }

    public void updateEndTime() {
        LocalDateTime latestEndTime = subtasks.stream()
                .map(subtask -> {
                    if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                        return subtask.getStartTime().plusMinutes(subtask.getDuration().toMinutes());
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }


    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public boolean isEpicDone() {
        return subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);
    }

    public void setDuration(Duration duration) {
        super.setDuration(duration);
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
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return String.format("Epic %d: %s (%s) [Subtasks: %d], , StartTime: %s, EndTime: %s, Duration: %d minutes",
                getTaskId(),
                getName(),
                getStatus(),
                subtasks.size(),
                (getStartTime() != null ? getStartTime().format(formatter) : "Не указано"),
                (getEndTime() != null ? getEndTime().format(formatter) : "Не указано"),
                (getDuration() != null ? getDuration().toMinutes() : 0));
    }
}
