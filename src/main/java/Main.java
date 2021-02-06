import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        File directory = getDirectory(sc);
        if (directory != null) {
            int[] limits = getLimits(sc);
            if (limits != null) {
                int productsLimit = limits[0];
                int idLimit = limits[1];
                CSVSelector.parseFolder(directory, productsLimit, idLimit);
            }
        }
    }

    public static File getDirectory(Scanner sc) {
        System.out.print("Enter directory name: ");
        File directory = new File(sc.next());
        if (!directory.exists()) {
            System.out.println("Directory not found");
            return null;
        }
        return directory;
    }

    public static int[] getLimits(Scanner sc) {
        int[] limits = new int[2];
        try {
            System.out.print("Enter products limit: ");
            limits[0] = sc.nextInt();
            System.out.print("Enter id limit: ");
            limits[1] = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Incorrect value, try again");
            return null;
        } finally {
            sc.close();
        }
        return limits;
    }
}
