import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.math.BigInteger;

public class SharmirSecretSharing {

    public static void main(String[] args) {
        String[] filenames = {"testcase1.json", "testcase2.json"};

        for (String filename : filenames) {
            try {
                // Read and parse JSON file
                JSONObject jsonData = (JSONObject) new JSONParser().parse(new FileReader(filename));

                // Extract keys (n and k)
                JSONObject keys = (JSONObject) jsonData.get("keys");
                int n = Integer.parseInt(keys.get("n").toString());
                int k = Integer.parseInt(keys.get("k").toString());

                // Extract and decode (x, y) pairs
                List<double[]> points = new ArrayList<>();
                for (Object key : jsonData.keySet()) {
                    if (!key.equals("keys")) {
                        int x = Integer.parseInt(key.toString());
                        JSONObject valueObj = (JSONObject) jsonData.get(key);
                        int base = Integer.parseInt(valueObj.get("base").toString());
                        BigInteger y = new BigInteger(valueObj.get("value").toString(), base);
                        points.add(new double[]{x, y.doubleValue()});
                    }
                }

                // Sort points based on x value
                points.sort(Comparator.comparingDouble(p -> p[0]));

                // Select the first k points required for interpolation
                double[][] selectedPoints = new double[k][2];
                for (int i = 0; i < k; i++) {
                    selectedPoints[i] = points.get(i);
                }

                // Compute the secret constant term using Lagrange interpolation
                double constantTerm = lagrangeInterpolation(selectedPoints);

                System.out.println("Secret for " + filename + " : " + Math.round(constantTerm));

            } catch (IOException | ParseException e) {
                System.err.println("Error reading/parsing JSON file: " + e.getMessage());
            }
        }
    }

    // Lagrange Interpolation to find the constant term (c)
    public static double lagrangeInterpolation(double[][] points) {
        double secret = 0.0;
        int k = points.length;

        for (int i = 0; i < k; i++) {
            double xi = points[i][0];
            double yi = points[i][1];
            double term = yi;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    double xj = points[j][0];
                    term *= (-xj) / (xi - xj);
                }
            }
            secret += term;
        }

        return secret;
    }
}