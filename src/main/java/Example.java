import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

@Slf4j
public class Example {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        File directory = getDirectory(sc);
        if (directory != null) {
            int[] limits = getLimits(sc);
            if (limits != null) {
                int productsLimit = limits[0];
                int idLimit = limits[1];
                CheapestProductsSelector selector = new CheapestProductsSelector(
                        "Product ID,Name,Condition,State,Price".split(","),
                        "output.csv"
                );
                File output = selector.getCheapestProducts(directory, productsLimit, idLimit);
                checkOutput(output);
            }
        }
    }

    public static File getDirectory(Scanner sc) {
        log.info("Enter directory name: ");
        File directory = new File(sc.next());
        if (!directory.exists()) {
            log.error("Directory not found");
            sc.close();
            return null;
        }
        return directory;
    }

    public static int[] getLimits(Scanner sc) {
        int[] limits = new int[2];
        try {
            log.info("Enter products limit: ");
            limits[0] = sc.nextInt();
            log.info("Enter id limit: ");
            limits[1] = sc.nextInt();
        } catch (InputMismatchException e) {
            log.error("Incorrect value, try again");
            return null;
        } finally {
            sc.close();
        }
        return limits;
    }

    private static void checkOutput(File file) {
        if (file != null) {
            log.info(String.format("Result path: %s", file.getAbsolutePath()));
        } else {
            log.error("Can not create file");
        }
    }
}