import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Loader {

    private static Map<String, List<String>> stations = new TreeMap<>();
    private static final JSONArray connectionsJSON = new JSONArray();
    private static final JSONArray linesJSON = new JSONArray();
    private static String dataFile = "data/MSKMetro.json";


    public static void main(String[] args) {
        String path = "data/MSKMetro_wikipage.html";

        try {
            Document document = Jsoup.parse(new File(path), "UTF-8");
            parseFile(document.select("table").get(3));

            JSONObject metroStations = parseFile(document.select("table").get(3));

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String metroStationsGson = gson.toJson(metroStations);

            PrintWriter pw = new PrintWriter(dataFile);
            pw.println(metroStationsGson);
            pw.flush();
            pw.close();

            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(getJsonFile());

            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            parseStations(stationsObject);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static JSONObject parseFile(Element element) {
        Elements rows = element.select("tr");

        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");

            String lineColor = getLineColor(cols.get(0).attr("style"));
            String lineName = cols.get(0).child(1).attr("title");
            List<String> lineNumber = cols.get(0).children().eachText();
            String stationName = cols.get(1).text();
            List<String> connectionLineNumbers = cols.get(3).children().eachText();
            List<String> connectionLineStations = cols.get(3).children().eachAttr("title");


            /*----------------Список станций на линиях-----------------------------*/
            String clearLineNumber = clearLineNumber(lineNumber.get(0));
            if (!stations.containsKey(clearLineNumber)) {
                stations.put(clearLineNumber, new ArrayList<>());

                //Список линий
                Map<String, String> map = new TreeMap<>();
                map.put("number", clearLineNumber);
                map.put("name", lineName);
                map.put("color", lineColor);
                JSONObject lines = new JSONObject(map);
                linesJSON.add(lines);

            }
            if (lineNumber.size() == 2) {
                stations.get(clearLineNumber).add(stationName);
            } else if (lineNumber.size() == 3) {
                stations.get(lineNumber.get(1)).add(stationName);
                stations.get(clearLineNumber).add(stationName);
            }

            /*----------------Список переходов на другие станции--------------------*/
            if (connectionLineNumbers.size() != 0) {
                JSONArray array = new JSONArray();
                JSONObject station1 = new JSONObject();
                station1.put("line", lineNumber.get(0));
                station1.put("station", stationName);
                array.add(station1);

                for (int j = 0; j < connectionLineNumbers.size(); j++) {
                    final JSONObject station2 = new JSONObject();
                    station2.put("line", connectionLineNumbers.get(j));
                    station2.put("station", connectionLineStations.get(j));
                    array.add(station2);
                }
                connectionsJSON.add(array);
            }
        }


        JSONObject stationsJSON = new JSONObject(stations);

        JSONObject allMetroElements = new JSONObject();
        allMetroElements.put("stations", stationsJSON);
        allMetroElements.put("connections", connectionsJSON);
        allMetroElements.put("lines", linesJSON);

        return allMetroElements;
    }

    private static String getLineColor(String text) {
        int first = text.indexOf("#");
        return text.substring(first, first + 6);
    }

    private static String clearLineNumber(String number) {
        return number.length() == 4 ? number.substring(1) : number;
    }

    private static String getJsonFile() {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(dataFile));
            lines.forEach(builder::append);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private static void parseStations(JSONObject stationsObject) {

        stationsObject.keySet().forEach(lineNumberObject ->
        {
            String lineNumber = (String) lineNumberObject;
            JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
            System.out.printf("Линия - %s, количество станций - %s%n", lineNumber, stationsArray.size());
        });
    }
}
