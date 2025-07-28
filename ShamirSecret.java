import java.io.FileReader;
import java.util.*;
import java.math.BigInteger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PolynomialSecretSolver {

    static class Coordinate {
        int x;
        long y;

        Coordinate(int x, long y) {
            this.x = x;
            this.y = y;
        }
    }

   
    public static long parseBaseEncodedValue(String encoded, int base) {
        return new BigInteger(encoded, base).longValue();
    }

    public static long interpolateSecretAtZero(List<Coordinate> coords) {
        double secret = 0;

        for (int i = 0; i < coords.size(); i++) {
            double term = coords.get(i).y;

            for (int j = 0; j < coords.size(); j++) {
                if (i != j) {
                    double numerator = 0.0 - coords.get(j).x;
                    double denominator = coords.get(i).x - coords.get(j).x;
                    term *= (numerator / denominator);
                }
            }

            secret += term;
        }

        return Math.round(secret);
    }

    public static List<Coordinate> extractValidCoordinates(String filePath) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject fullData = (JSONObject) parser.parse(new FileReader(filePath));
        JSONObject meta = (JSONObject) fullData.get("keys");

        int threshold = ((Long) meta.get("k")).intValue();
        List<Coordinate> resultList = new ArrayList<>();

        for (Object key : fullData.keySet()) {
            String label = (String) key;
            if (label.equals("keys")) continue;

            int x = Integer.parseInt(label);
            JSONObject point = (JSONObject) fullData.get(label);
            String encoded = (String) point.get("value");
            int base = Integer.parseInt((String) point.get("base"));

            long y = parseBaseEncodedValue(encoded, base);
            resultList.add(new Coordinate(x, y));

            if (resultList.size() == threshold) break;
        }

        return resultList;
    }

    public static void main(String[] args) {
        try {
            String[] testCases = {"sample_test_case.json", "sample_test_case2.json"};
            for (int i = 0; i < testCases.length; i++) {
                List<Coordinate> coordinates = extractValidCoordinates(testCases[i]);
                long secret = interpolateSecretAtZero(coordinates);
                System.out.println("Recovered secret from test case " + (i + 1) + ": " + secret);
            }
        } catch (Exception e) {
            System.err.println("An error occurred during secret recovery:");
            e.printStackTrace();
        }
    }
}
