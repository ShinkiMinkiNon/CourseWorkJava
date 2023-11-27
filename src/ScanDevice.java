import javax.naming.OperationNotSupportedException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScanDevice {
    public static List<String> runAntivirusScan(Path pathToCommand, List<Path> databases, Path pathToFile)
            throws FileNotFoundException {
        if (!Files.exists(pathToFile)) throw new FileNotFoundException("File or directory does not exist");
        List<String> command = new ArrayList<>(
                Arrays.asList(
                        pathToCommand.toString(),
                        "--infected",
                        pathToFile.toString()
                )
        );
        List<String> results = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (Files.isDirectory(pathToFile)) command.add(1, "--recursive");

        String output;
        try {
            for (var database : databases) {
                command.add(1, "--database=" + database.toString());
                processBuilder.command(command);
                Process process = processBuilder.start();
                output = getCommandOutput(process);
                results.add(output);
                command.remove(1);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return results;
    }

    public static List<String> getConnectedDisks() throws OperationNotSupportedException {
        String output = "";
        String[] disks;
        String[] linuxCommand = {"lsblk", "-o", "NAME", "--noheadings"};
        String[] windowsCommand = {"wmic", "logicaldisk", "get", "caption"};
        List<String> result = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // There is a code here for POSIX-compatible system
            processBuilder.command(linuxCommand);
        } else if (os.contains("win")) {
            // There is a code here for Windows system
            processBuilder.command(windowsCommand);
        } else {
            throw new OperationNotSupportedException("Unsupported operating system");
        }
        try {
            Process process = processBuilder.start();
            output = getCommandOutput(process);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        disks = output.split("\n");
        String element;
        for (int i = os.contains("win") ? 1 : 0; i < disks.length; i++) {
            element = disks[i];
            if (!element.isEmpty()) {
                element = element.trim();
                result.add(element);
            }
        }
        return result;
    }

    public static void updateDatabases(Path pathToDatabase) {
        // Perhaps someday in future I will write this method. But not today...
    }

    private static String getCommandOutput(Process process) throws IOException {
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append('\n');
        }
        reader.close();
        inputStream.close();
        return output.toString();
    }
}
