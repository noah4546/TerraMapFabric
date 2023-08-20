package ca.tnoah.bteterramapfabric.models;

import java.util.Map;

public class Projection {

    private final Proj4 proj4;
    private final Map<Integer, TileMatrix> tileMatrices;

    public Projection(Proj4 proj4, Map<Integer, TileMatrix> tileMatrices) {
        this.proj4 = proj4;
        this.tileMatrices = tileMatrices;
    }

    public Proj4 getProj4() {
        return proj4;
    }

    public Map<Integer, TileMatrix> getTileMatrices() {
        return tileMatrices;
    }

    public static class Proj4 {
        private final String name;
        private final String param;

        public Proj4(String name, String param) {
            this.name = name;
            this.param = param;
        }

        public String getName() {
            return name;
        }

        public String getParam() {
            return param;
        }
    }

    public static class TileMatrix {
        private final double[] origin;
        private final double[] size;

        public TileMatrix(double[] origin, double[] size) {
            this.origin = origin;
            this.size = size;
        }

        public double[] getOrigin() {
            return origin;
        }

        public double[] getSize() {
            return size;
        }
    }
}
