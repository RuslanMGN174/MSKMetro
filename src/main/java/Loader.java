import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Loader {

    private static String htmlFile;
    private static String newFile = "data/new.csv";
    private static String lineName;
    private static String lineNumber;
    private static Map<String, List<String>> stations = new TreeMap<>();

    public static void main(String[] args) {
//        htmlFile = parseFile("data/MSKMetro_wikipage.html");
        String path = "data/MSKMetro_wikipage.html";

        try {
            Document document = Jsoup.parse(new File(path), "UTF-8");

            Element table = document.select("table").get(3);
            Elements rows = table.select("tr");

            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                String lineColor = getLineColor(cols.get(0).attr("style"));
                lineName = cols.get(0).child(1).attr("title");
                lineNumber = clearLineNumber(cols.get(0).child(0).text());
                String stationName = cols.get(1).child(0).text();
                List<String> connectionLineNumbers = cols.get(3).children().eachText();
                List<String> connectionLineStations = cols.get(3).children().eachAttr("title");

                //Список станций на линиях
                if (!stations.containsKey(lineNumber)) {
                    stations.put(lineNumber, new ArrayList<>());
                }

<<<<<<< HEAD
                stations.get(lineNumber).add(stationName);
=======
//                System.out.println(connectionLineStations);
//                    System.out.printf("%s %s %s %s %s %s \n", lineNumber, lineName, lineColor, stationName, connectionLineNumbers);
>>>>>>> 6a5f656ce0349c430267ecf9e2273907a17d6369

                System.out.println(lineNumber);

//                System.out.println(connectionLineStations);
//                    System.out.printf("%s %s %s %s %s %s \n", lineNumber, lineName, lineColor, stationName, connectionLineNumbers);
            }

//            stations.forEach((k, v) -> System.out.printf("Номер линии %s%nСписок станций%n%s%n", k, v));


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

    private static String getLineColor(String text) {
        int first = text.indexOf("#");
        return text.substring(first, first + 6);
    }

    private static String clearLineNumber(String number) {
        return number.length() == 4 ? number.substring(1) : number;
    }
}
