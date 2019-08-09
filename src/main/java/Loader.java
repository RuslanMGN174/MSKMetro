import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Loader {

    private static String htmlFile;

    public static void main(String[] args) {
        htmlFile = parseFile("data/MSKMetro_wikipage.html");

        

    }

    private static String parseFile(String s) {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(s));
            lines.forEach(line -> builder.append(line).append("\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
