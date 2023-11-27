import javax.naming.OperationNotSupportedException;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<String> disks  = new ArrayList<>();
//         Here the function will return list of disks
        try {
            disks = (ArrayList<String>) ScanDevice.getConnectedDisks();
        } catch (OperationNotSupportedException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
        System.out.print("Disks connected to PC -> ");
        for (String elem : disks) {
            System.out.printf("%s ", elem);
        }
        // ------------------------------------------
        System.out.print("\nChoose the disk or input the path to file or directory: ");
        Path pathToFile = Path.of(scanner.nextLine());
        System.out.println(pathToFile);

        Path pathToCommand = Path.of("ClamAV", "clamscan");
        List<String> results = null; // Size of results and databases will be equal
        List<Path> databases = Arrays.asList(
                Path.of("ClamAV", "database", "main.cvd"),
                Path.of("ClamAV", "database", "daily.cvd"),
                Path.of("ClamAV", "database", "bytecode.cvd")
        );
        try {
            results = ScanDevice.runAntivirusScan(pathToCommand, databases, pathToFile);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
        for (int i = 0; i < results.size(); i++) {
            System.out.printf("\nScanned with using database: %s\n%s", databases.get(i), results.get(i));
        }
        scanner.close();
    }
}