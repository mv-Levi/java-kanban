/*import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class SubtaskTest {
    //Объект Subtask нельзя сделать своим же эпиком:
    @Test
    public void testSetSubtaskEpicToItself() {
        Epic epic = new Epic("Epic 1", "Description 1", TaskStatus.DONE);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", TaskStatus.IN_PROGRESS, epic);

        // Пытаемся сделать subtask своим эпиком
        subtask.setEpic(subtask); // Ожидаем ошибку
    }
}*/