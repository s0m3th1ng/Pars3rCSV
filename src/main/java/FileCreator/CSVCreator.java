package FileCreator;

import Product.Product;
import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

@Slf4j
@AllArgsConstructor
public class CSVCreator implements IFileCreator {

    private static final String ERROR_MESSAGE = "Error while creating file \"%s\"";

    private String[] header;
    private String filename;

    @Override
    public File createFile(PriorityQueue<Product> products) throws IOException {
        File output = new File(filename);
        try (CSVWriter writer = new CSVWriter(new FileWriter(output))) {
            writer.writeNext(header, false);
            products.stream()
                    .sorted()
                    .forEach(p -> writer.writeNext(p.toStringArray(), false));
        } catch (IOException e) {
            log.error(String.format(ERROR_MESSAGE, filename));
            output.delete();
            throw new IOException(String.format(ERROR_MESSAGE, filename));
        }
        return output;
    }

}
