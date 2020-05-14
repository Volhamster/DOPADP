package src.main.java;

import org.apache.spark.api.java.JavaRDD;

public class CSVParser {

    public static JavaRDD<String> parseAirport(JavaRDD<String> distFile) {

        distFile = distFile
                .filter(s -> !s.contains("Code"))
                .map(s -> s=s.replace("\"",""));
        return distFile;
    }

    public static JavaRDD<String> parseFlight(JavaRDD<String> distFile) {

        distFile = distFile
                .filter(s -> !s.contains("YEAR"));
        return distFile;
    }
}