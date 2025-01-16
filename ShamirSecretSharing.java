import java.io.FileReader;
import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            // Parse JSON file
            JSONParser parser = new JSONParser();
            JSONObject input = (JSONObject) parser.parse(new FileReader("input.json"));

            // Extract keys and roots
            JSONObject keys = (JSONObject) input.get("keys");
            int n = ((Long) keys.get("n")).intValue();
            int k = ((Long) keys.get("k")).intValue();

            Map<Integer, BigInteger> points = new TreeMap<>();

            // Decode x and y values
            for (Object key : input.keySet()) {
                if (key.equals("keys")) continue;

                int x = Integer.parseInt((String) key);
                JSONObject root = (JSONObject) input.get(key);
                int base = Integer.parseInt((String) root.get("base"));
                String value = (String) root.get("value");

                // Decode y value using the given base
                BigInteger y = new BigInteger(value, base);
                points.put(x, y);
            }

            // Compute the constant term using Lagrange Interpolation
            BigInteger constantTerm = calculateConstantTerm(points, k);

            // Output the result
            System.out.println("Secret constant term (c): " + constantTerm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to calculate the constant term (c) using Lagrange Interpolation
    private static BigInteger calculateConstantTerm(Map<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;

        // Only consider the first k points
        int count = 0;
        for (Map.Entry<Integer, BigInteger> pointI : points.entrySet()) {
            if (count >= k) break;
            count++;

            int xi = pointI.getKey();
            BigInteger yi = pointI.getValue();

            BigInteger li = BigInteger.ONE;

            for (Map.Entry<Integer, BigInteger> pointJ : points.entrySet()) {
                if (pointI.equals(pointJ)) continue;
                int xj = pointJ.getKey();

                li = li.multiply(BigInteger.valueOf(-xj))
                       .divide(BigInteger.valueOf(xi - xj));
            }

            result = result.add(yi.multiply(li));
        }

        return result;
    }
}
