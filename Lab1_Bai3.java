import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

class Point implements Comparable<Point> {
    int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Point p) {
        if (this.y == p.y) {
            return this.x - p.x;
        }
        return this.y - p.y;
    }
}

public class Lab1_Bai3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nNhập số lượng trạm phát sóng: ");
        int n = scanner.nextInt();

        System.out.print("Nhập tọa độ các trạm phát sóng: ");
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            points.add(new Point(x, y));
        }

        List<Point> hull = findConvexHull(points);

        for (Point p : hull) {
            System.out.println(p.x + " " + p.y);
        }

        scanner.close();
    }

    private static List<Point> findConvexHull(List<Point> points) {
        Collections.sort(points);
        Stack<Point> hull = new Stack<>();

        // Lower hull
        for (Point p : points) {
            while (hull.size() >= 2 && cross(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p) <= 0) {
                hull.pop();
            }
            hull.push(p);
        }

        // Upper hull
        int t = hull.size() + 1;
        for (int i = points.size() - 2; i >= 0; i--) {
            Point p = points.get(i);
            while (hull.size() >= t && cross(hull.get(hull.size() - 2), hull.get(hull.size() - 1), p) <= 0) {
                hull.pop();
            }
            hull.push(p);
        }

        hull.pop(); // Remove the last point because it is the same as the first one
        System.out.print("Tọa độ các trạm cảnh báo: ");
        return new ArrayList<>(hull);
    }

    private static int cross(Point o, Point a, Point b) {
        return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
    }
}