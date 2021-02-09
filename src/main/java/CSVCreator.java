import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

@Slf4j
@AllArgsConstructor
public class CSVCreator implements IFileCreator {

    @Getter
    private String[] header;
    private String filename;

    @Override
    public File createFile(PriorityQueue<Product> products) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            writer.writeNext(header);
            products.stream()
                    .sorted()
                    .forEach(p -> writer.writeNext(p.toStringArray()));
        } catch (IOException e) {
            log.error(String.format("Unable to create file %s", filename));
        }
        return new File(filename);
    }

}
