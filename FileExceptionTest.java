import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FileExceptionTest {
    @Test
    public void testFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            FileReader reader = new FileReader("non_existent_file.txt");
        }, "Должно возникнуть исключение, так как файл не существует");
    }

    @Test
    public void testSuccessfulFileWrite() {
        assertDoesNotThrow(() -> {
            FileWriter writer = new FileWriter("test_file.txt");
            writer.write("Тестовая запись");
            writer.close();
        }, "Запись в файл должна проходить без исключений");
    }
}
