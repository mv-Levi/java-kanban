import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int taskId, String name, String description, TaskStatus status, int epicId,
                   LocalDateTime startTime, Duration duration) {
        super(taskId, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return String.format("Subtask %d: %s (%s), [Epic: %d], StartTime: %s, Duration: %d minutes",
                getTaskId(),
                getName(),
                getStatus(),
                epicId,
                (getStartTime() != null ? getStartTime().format(formatter) : "Не указано"),
                (getDuration() != null ? getDuration().toMinutes() : 0));
    }

    @Override
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return String.format("%d,%s,%s,%s,%s,%s,%d,%d",
                getTaskId(),
                getClass().getSimpleName(),
                getName(),
                getStatus(),
                getDescription(),
                (getStartTime() != null ? getStartTime().format(formatter) : ""),
                (getDuration() != null ? getDuration().toMinutes() : 0),
                epicId);
    }
}
