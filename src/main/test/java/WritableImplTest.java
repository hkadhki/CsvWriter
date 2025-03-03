import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.writer.WritableImpl;
import org.writer.model.Months;
import org.writer.model.Person;
import org.writer.model.Student;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WritableImplTest {

    @TempDir
    Path tempDir;

    @Test
    void testWriteToFileWithValidData() throws IOException {
        List<Person> data = Arrays.asList(
                new Person("fName1", "lName1", 1, Months.APRIL,2000),
                new Person("fName2", "lName2", 2, Months.APRIL,2001)
        );

        String fileName = "test";
        WritableImpl writable = new WritableImpl();

        writable.writeToFile(data, tempDir.resolve(fileName).toString());

        Path outputFile = tempDir.resolve(fileName + ".csv");
        assertTrue(Files.exists(outputFile));

        String content = Files.readString(outputFile);

        String expectedContent = """
                firstName,lastName,dayOfBirth,monthOfBirth,yearOfBirth
                fName1,lName1,1,APRIL,2000
                fName2,lName2,2,APRIL,2001
                """;
        assertEquals(expectedContent.trim(), content.trim());
    }

    @Test
    void testWriteToFileWithEmptyList() throws IOException {
        List<Person> data = Collections.emptyList();
        String fileName = "empty";
        WritableImpl writable = new WritableImpl();

        writable.writeToFile(data, tempDir.resolve(fileName).toString());

        Path outputFile = tempDir.resolve(fileName + ".csv");
        assertFalse(Files.exists(outputFile));
    }

    @Test
    void testWriteToFileWithNullData() throws IOException {
        List<Person> data = null;
        String fileName = "null";
        WritableImpl writable = new WritableImpl();

        writable.writeToFile(data, tempDir.resolve(fileName).toString());

        Path outputFile = tempDir.resolve(fileName + ".csv");
        assertFalse(Files.exists(outputFile));
    }

    @Test
    void testWriteToFile_WithComplexData() throws IOException {
        List<Student> data = Arrays.asList(
                new Student("name1", List.of("a","b")),
                new Student("name2", List.of("a","b"))
        );

        String fileName = "list";
        WritableImpl writable = new WritableImpl();

        writable.writeToFile(data, tempDir.resolve(fileName).toString());

        Path outputFile = tempDir.resolve(fileName + ".csv");
        assertTrue(Files.exists(outputFile));

        String content = Files.readString(outputFile);
        String expectedContent = """
                name,score
                name1,"[a, b]"
                name2,"[a, b]"
                """;
        assertEquals(expectedContent.trim(), content.trim());
    }

}