import java.util.LinkedList;
 import java.awt.Point;

/**
 * Created by Ilya Gazman on 10/17/2018.
 */
public class Maze {

    private static final boolean DEBUG = false;

    public Point[] findPath(int[][] map, Point position, Point destination) {
        if (isOutOfMap(map, position)) {
            return null;
        }
        if (isOutOfMap(map, destination)) {
            return null;
        }
        if (isBlocked(map, position)) {
            return null;
        }
        if (isBlocked(map, destination)) {
            return null;
        }
        LinkedList<Point> queue1 = new LinkedList<>();
        LinkedList<Point> queue2 = new LinkedList<>();

        queue1.add(position);
        map[position.y][position.x] = -1;
        int stepCount = 2;
        while (!queue1.isEmpty()) {
            if(queue1.size() >= map.length * map[0].length){
                throw new Error("Map overload");
            }
            for (Point point : queue1) {
                if (point.x == destination.x && point.y == destination.y) {
                    Point[] optimalPath = new Point[stepCount - 1];
                    computeSolution(map, point.x, point.y, stepCount - 1, optimalPath);
                    resetMap(map);
                    return optimalPath;
                }
                LinkedList<Point> finalQueue = queue2;
                int finalStepCount = stepCount;
                lookAround(map, point, (x, y) -> {
                    if (isBlocked(map, x, y)) {
                        return;
                    }
                    Point e = new Point(x, y);

                    finalQueue.add(e);
                    map[e.y][e.x] = -finalStepCount;
                });
            }

            if (DEBUG) {
                printMap(map);
            }

            queue1 = queue2;
            queue2 = new LinkedList<>();
            stepCount++;
        }
        resetMap(map);
        return null;
    }

    private void resetMap(int[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] < 0) {
                    map[y][x] = 0;
                }
            }
        }
    }

    private boolean isBlocked(int[][] map, Point p) {
        return isBlocked(map, p.x, p.y);
    }

    private boolean isBlocked(int[][] map, int x, int y) {
        int i = map[y][x];
        return i < 0 || i == 1;
    }

    private void printMap(int[][] map) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, mapLength = map.length; i < mapLength; i++) {
            int[] aMap = map[i];
            for (int x = 0; x < map[0].length; x++) {
                System.out.print(aMap[x] + "\t");
            }
            System.out.println();
        }
        System.out.println("****************************************");
    }

    private void computeSolution(int[][] map, int x, int y, int stepCount, Point[] optimalPath) {
        if (isOutOfMap(map, x, y) || map[y][x] == 0) {
            return;
        }

        if ( -stepCount != map[y][x]) {
            return;
        }

        Point p = new Point(x, y);
        optimalPath[stepCount - 1] = p;
        lookAround(map, p, (x1, y1) -> computeSolution(map, x1, y1, stepCount - 1, optimalPath));
    }

    private void lookAround(int[][] map, Point p, Callback callback) {
        callback.look(map, p.x + 1, p.y + 1);
        callback.look(map, p.x - 1, p.y + 1);
        callback.look(map, p.x - 1, p.y - 1);
        callback.look(map, p.x + 1, p.y - 1);
        callback.look(map, p.x + 1, p.y);
        callback.look(map, p.x - 1, p.y);
        callback.look(map, p.x, p.y + 1);
        callback.look(map, p.x, p.y - 1);
    }

    private static boolean isOutOfMap(int[][] map, Point p) {
        return isOutOfMap(map, p.x, p.y);
    }

    private static boolean isOutOfMap(int[][] map, int x, int y) {
        if (x < 0 || y < 0) {
            return true;
        }
        return map.length <= y || map[0].length <= x;
    }

    private interface Callback {
        default void look(int[][] map, int x, int y) {
            if (isOutOfMap(map, x, y)) {
                return;
            }
            onLook(x, y);
        }

        void onLook(int x, int y);
    }
    public static void main(String... args) {
        int[][] myMap = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 1, 1, 1, 1},
                {0, 0, 0, 1, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 1, 1, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 0},
        };
    
        Point[] path = new Maze().findPath(myMap, new Point(8, 0), new Point(8, 2));
        for (Point point : path) {
            System.out.println(point.x + ", " + point.y);
        }
    }
}