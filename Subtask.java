import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private Epic epic;


    public Subtask(int taskId, String name, String description, TaskStatus status, Epic epic, LocalDateTime startTime, Duration duration) {
        super(taskId, name, description, status, startTime, duration);
        this.epic = epic;
    }


    public void setEpic(Epic newEpic) {
        if (newEpic != this.epic) {
            this.epic = newEpic;
        } else {
            System.out.println("Error: Cannot set subtask's epic to itself.");
        }

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
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return String.format("Subtask %d: %s (%s) [Epic: %s], StartTime: %s, Duration: %d minutes",
                getTaskId(),
                getName(),
                getStatus(),
                epic.getName(),
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
                getEpic().getTaskId());
    }


}
