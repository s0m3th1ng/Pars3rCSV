import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

public abstract class CSVCreator {

    public static File createCSV(PriorityQueue<Product> products, String[] header, String filename) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(filename));
        writer.writeNext(header);
        products.stream()
                .sorted()
                .forEach(p -> writer.writeNext(p.toStringArray()));
        writer.close();
        return new File(filename);
    }

}
