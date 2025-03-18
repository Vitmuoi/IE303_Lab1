import java.util.Random;
import java.util.Scanner;

public class Lab1_Bai1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nNhập bán kính r: ");
        double r = scanner.nextDouble();

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
        double area = pi * r * r;
        System.out.println("Diện tích hình tròn: " + area);
        scanner.close();
    }
}