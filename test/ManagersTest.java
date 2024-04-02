import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    //Утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров:
    @Test
    void shouldBePositiveWhenUseGetDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }
}