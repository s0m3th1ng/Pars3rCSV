import DataCollector.CSVDataCollector;
import FileCreator.CSVCreator;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Slf4j
public class SelectorTests {

    private CheapestProductsSelector selector;
    private SelectorProperties properties;

    private final String pathToCorrectFile = "csv/222.csv";
    private final String pathToIncorrectFile = "csv/empty.csv";
    private final String incorrectPath = "csv/incorrect.csv";
    private final String packagePath = "csv";

    @BeforeEach
    public void setUp() {
        properties = new SelectorProperties(
                "Product ID,Name,Condition,State,Price".split(","),
                ',',
                "output.csv"
        );
        selector = new CheapestProductsSelector(
                new CSVDataCollector(properties.getHeader().length, properties.getSeparator()),
                new CSVCreator(properties.getHeader(), properties.getOutputFilename())
        );
    }

    @Test
    public void stableCorrectUsing1() {
        int maxLength = 7, maxId = 3;
        try {
            File output = selector.getCheapestProducts(new File(pathToCorrectFile), maxLength, maxId);
            assertNotNull(output);
            checkFileSizeAndIdsCount(output, maxLength, maxId);
        } catch (IOException | CsvValidationException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void stableCorrectUsing2() {
        int maxLength = 1, maxId = 1;
        try {
            File output = selector.getCheapestProducts(new File(pathToCorrectFile), maxLength, maxId);
            assertNotNull(output);
            checkFileSizeAndIdsCount(output, maxLength, maxId);
        } catch (IOException | CsvValidationException e) {
            log.error(e.getMessage());
        }
    }

    public void checkFileSizeAndIdsCount(File output, int maxLength, int maxId) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReaderBuilder(
                new FileReader(output))
                .withSkipLines(1)
                .build();
        ArrayList<int[]> idsCount = new ArrayList<>(); //[0] - id, [1] - count
        for (String[] s = reader.readNext(); s != null; s = reader.readNext()) {
            maxLength--;
            int id = Integer.parseInt(s[0]);
            Optional<int[]> idCount = idsCount.stream().filter(c -> c[0] == id).findFirst();
            if (idCount.isPresent()) {
                idCount.get()[1]++;
            } else {
                idsCount.add(new int[] {id, 1});
            }
        }
        assertTrue(maxLength >= 0);
        for (int[] idCount : idsCount) {
            assertTrue(idCount[1] <= maxId);
        }
    }

    @Test
    public void stableIncorrectUsing() {
        String header = Arrays.stream(properties.getHeader())
                .collect(Collectors.joining(",", "", "\n"));
        try {
            File output = selector.getCheapestProducts(new File(pathToCorrectFile), 0, 0);
            assertNotNull(output);
            assertEquals(header.length(), output.length());

            output = selector.getCheapestProducts(new File(pathToCorrectFile), 1, 0);
            assertNotNull(output);
            assertEquals(header.length(), output.length());

            output = selector.getCheapestProducts(new File(pathToIncorrectFile), 3, 3);
            assertNotNull(output);
            assertEquals(header.length(), output.length());

            assertThrows(NullPointerException.class, () -> selector.getCheapestProducts(null, 3, 3));

            assertThrows(IOException.class, () -> selector.getCheapestProducts(new File(incorrectPath), 3, 3));

            assertThrows(IOException.class, () -> selector.getCheapestProducts(new File(packagePath), 3, 3));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void unstableOrProtectedFromWriting() {
        CSVCreator creator = new CSVCreator(new String[] {}, "C:/windows/1.csv");
        assertThrows(IOException.class, () -> creator.createFile(new PriorityQueue<>()));
    }

}
