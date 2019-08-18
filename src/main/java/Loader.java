import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Loader {

    private static String htmlFile;
    private static String newFile = "data/new.csv";
    private static String lineName;
    private static List<String> lineNumber;
    private static Map<String, List<String>> stations = new TreeMap<>();
    private static List<String> connectionLineNumbers;
    private static List<String> connectionLineStations;
    private static List<String> lines = new ArrayList<>();
    private static List<List<String>> connections = new ArrayList<>();

    private static final JSONArray connectionsJSON = new JSONArray();
    private static final JSONArray linesJSON = new JSONArray();

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
                lineNumber = cols.get(0).children().eachText();
                String stationName = cols.get(1).child(0).text();
                connectionLineNumbers = cols.get(3).children().eachText();
                connectionLineStations = cols.get(3).children().eachAttr("title");


                /*----------------Список переходов на другие станции--------------------*/

                if (connectionLineNumbers.size() != 0) {
                    connections.add(new ArrayList<String>() {{
                        for (int j = 0; j < connectionLineNumbers.size(); j++) {
                            add("line: " + connectionLineNumbers.get(j) + ", " + "station: " + connectionLineStations.get(j));
                        }
                    }});
                }


                /*----------------Список станций на линиях-----------------------------*/

                String clearLineNumber = clearLineNumber(lineNumber.get(0));
                if (!stations.containsKey(clearLineNumber)) {
                    stations.put(clearLineNumber, new ArrayList<>());

                    //Список линий
                    lines.add("number: " + clearLineNumber
                            + " " + "name: " + lineName
                            + " " + "color: " + lineColor);

                    //Список линий JSON
                    JSONObject lines = new JSONObject();
                    lines.put("number", clearLineNumber);
                    lines.put("name", lineName);
                    lines.put("color", lineColor);

                    linesJSON.add(lines);

                }
                if (lineNumber.size() == 2) {
                    stations.get(clearLineNumber).add(stationName);
                } else if (lineNumber.size() == 3) {
                    stations.get(lineNumber.get(1)).add(stationName);
                    stations.get(clearLineNumber).add(stationName);
                }





                /*----------------Список переходов на другие станции JSON--------------------*/
                if (connectionLineNumbers.size() != 0) {
                    JSONArray array = new JSONArray();
                    JSONObject object = new JSONObject();
                    object.put("line", lineNumber.get(0));
                    object.put("station", stationName);
                    array.add(object);

                    for (int j = 0; j < connectionLineNumbers.size(); j++) {
                        final JSONObject station2 = new JSONObject();
                        station2.put("line", connectionLineNumbers.get(j));
                        station2.put("station", connectionLineStations.get(j));
                        array.add(station2);
                    }
                    connectionsJSON.add(array);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject stationsJSON = new JSONObject(stations);


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String conn = gson.toJson(connectionsJSON);
        String lin = gson.toJson(linesJSON);
        String sta = gson.toJson(stationsJSON);

        JSONObject allMetroElements = new JSONObject();
        allMetroElements.put("stations", stationsJSON);
        allMetroElements.put("connections", connectionsJSON);
        allMetroElements.put("lines", linesJSON);

        String all = gson.toJson(allMetroElements);


        System.out.println(all);



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
