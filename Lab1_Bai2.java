import java.util.Random;

public class Lab1_Bai2 {
    public static void main(String[] args) {
        int totalPoints = 1000000;
        int pointsInsideCircle = 0;

        Random random = new Random();

        for (int i = 0; i < totalPoints; i++) {
            double x = random.nextDouble() * 2 - 1;
            double y = random.nextDouble() * 2 - 1;

            if (x * x + y * y <= 1) {
                pointsInsideCircle++;
            }
        }

        double pi = 4.0 * pointsInsideCircle / totalPoints;
        System.out.println("\nXấp xỉ giá trị của π: " + pi);
    }
}