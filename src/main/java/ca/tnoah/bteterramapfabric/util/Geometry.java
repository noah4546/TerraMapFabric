package ca.tnoah.bteterramapfabric.util;

public class Geometry {

    public static class Point<T extends Number> {
        public T x;
        public T y;

        public Point(T x, T y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Rect<T extends Number> extends Point<T> {
        public T width;
        public T height;

        public Rect(T x, T y, T width, T height) {
            super(x, y);
            this.width = width;
            this.height = height;
        }

        public Rect(Point<T> point, T width, T height) {
            this(point.x, point.y, width, height);
        }
    }

}
