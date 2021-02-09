import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

@AllArgsConstructor
public class CSVCreator implements IFileCreator {

    @Getter
    private String[] header;
    private String filename;

    @Override
    public File createFile(PriorityQueue<Product> products) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            writer.writeNext(header);
            products.stream()
                    .sorted()
                    .forEach(p -> writer.writeNext(p.toStringArray()));
        }
        return new File(filename);
    }

}
