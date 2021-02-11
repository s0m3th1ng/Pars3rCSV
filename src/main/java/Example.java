import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

@Slf4j
public class Example {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        File input = getInputCSV(sc);
        if (input == null) {
            return;
        }
        int[] limits = getLimits(sc);
        if (limits == null) {
            return;
        }
        int productsLimit = limits[0];
        int idLimit = limits[1];
        char separator = getDivider(sc);
        CheapestProductsSelector selector = new CheapestProductsSelector(
                "Product ID,Name,Condition,State,Price".split(","),
                "output.csv",
                separator
        );
        File output = selector.getCheapestProducts(input, productsLimit, idLimit);
        checkOutput(output);
    }

    public static File getInputCSV(Scanner sc) {
        log.info("Enter full path to file: ");
        File input = new File(sc.next());
        if (!input.exists() || !input.isFile()) {
            log.error("File not found");
            sc.close();
            return null;
        }
        return input;
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
            sc.close();
            return null;
        }
        return limits;
    }

    public static char getDivider(Scanner sc) {
        log.info("Enter divider character (only the first entered character is considered): ");
        return sc.next().charAt(0);
    }

    private static void checkOutput(File file) {
        if (file != null) {
            log.info(String.format("Result path: %s", file.getAbsolutePath()));
        } else {
            log.error("Can not create file");
        }
    }
}