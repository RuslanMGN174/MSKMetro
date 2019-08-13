import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Loader {

    private static String htmlFile;

    public static void main(String[] args) {
//        htmlFile = parseFile("data/MSKMetro_wikipage.html");
        String path = "data/MSKMetro_wikipage.html";

        try {
            Document document = Jsoup.parse(new File(path), "UTF-8");

            Element table = document.select("table").get(3);
            Elements rows = table.select("tr");

            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");
                cols.forEach(attr -> System.out.println(attr.text()));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


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
