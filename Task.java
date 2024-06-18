import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private int taskId;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;
    public Task(int taskId) {
        this.taskId = taskId;
    }


    public Task(int taskId, String name, String description, TaskStatus status,
                LocalDateTime startTime, Duration duration) {
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void updateFrom(Task other) {
        this.name = other.getName();
        this.description = other.getDescription();
        this.status = other.getStatus();
        this.startTime = other.getStartTime();
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    public Duration getDuration() {
        return duration;
    }

    public boolean isEpic() {
        return false;
    }

    public boolean isSubtask() {
        return false;
    }


    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return String.format("Task: %d,%s,%s,%s,%s,%d",
                getTaskId(),
                getName(),
                getStatus(),
                getDescription(),
                (getStartTime() != null ? getStartTime().format(formatter) : ""),
                (getDuration() != null ? getDuration().toMinutes() : 0));
    }

    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return String.format("%d,%s,%s,%s,%s,%s,%d",
                getTaskId(),
                getClass().getSimpleName(),
                getName(),
                getStatus(),
                getDescription(),
                (getStartTime() != null ? getStartTime().format(formatter) : ""),
                (getDuration() != null ? getDuration().toMinutes() : 0));
    }


    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return taskId == task.taskId;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            throw new IllegalArgumentException("Не установлены startTime или duration для задачи с ID: " + taskId);
        }
        return startTime.plusMinutes(duration.toMinutes());
    }

    public int hashCode() {
        return Integer.hashCode(taskId);
    }
}
