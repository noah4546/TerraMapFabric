package ca.tnoah.bteterramapfabric.projection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

public class JsonTileProjection extends TileProjection {


    private final Proj4jProjection projection;
    private final Map<Integer, TileMatrix> matrices;

    public JsonTileProjection(
            Proj4jProjection projection,
            Map<Integer, TileMatrix> matrices
    ) {
        this.projection = projection;
        this.matrices = matrices;
    }

    @JsonCreator
    public JsonTileProjection(
            @JsonProperty(value = "projection", required = true) Proj projection,
            @JsonProperty(value = "tileMatrices", required = true) Map<Integer, TileMatrix> matrices
    ) {
        this(new Proj4jProjection(projection.name, projection.param), matrices);
    }


    @Override
    protected int[] toTileCoord(double longitude, double latitude, int absoluteZoom) throws OutOfProjectionBoundsException {
        double[] coordinate = this.projection.fromGeo(longitude, latitude);
        TileMatrix matrix = this.matrices.get(absoluteZoom);

        int tileX = (int) Math.floor((coordinate[0] - matrix.pointOfOrigin[0]) / matrix.tileSize[0]);
        int tileY = (int) Math.floor((matrix.pointOfOrigin[1] - coordinate[1]) / matrix.tileSize[1]);

        return new int[] { tileX, tileY };
    }

    @Override
    protected double[] toGeoCoord(int tileX, int tileY, int absoluteZoom) throws OutOfProjectionBoundsException {
        TileMatrix matrix = this.matrices.get(absoluteZoom);

        double tileCoordinateX = tileX * matrix.tileSize[0] + matrix.pointOfOrigin[0];
        double tileCoordinateY = matrix.pointOfOrigin[1] - tileY * matrix.tileSize[1];

        return this.projection.toGeo(tileCoordinateX, tileCoordinateY);
    }


    @Override
    public TileProjection clone() {
        return new JsonTileProjection(this.projection, this.matrices);
    }


    @Override
    public boolean isAbsoluteZoomAvailable(int absoluteZoom) {
        return this.matrices.containsKey(absoluteZoom);
    }

    @JsonDeserialize
    public static class Proj {
        final String name;
        final String param;

        @JsonCreator
        Proj(
                @JsonProperty(value = "name", required = true) String name,
                @JsonProperty(value = "param", required = true) String param
        ) {
            this.name = name;
            this.param = param;
        }
    }

    @JsonDeserialize
    public static class TileMatrix {
        final double[] pointOfOrigin, tileSize;

        @JsonCreator
        TileMatrix(
                @JsonProperty(value = "origin", required = true) double[] pointOfOrigin,
                @JsonProperty(value = "size", required = true) double[] tileSize
        ) {
            this.pointOfOrigin = pointOfOrigin;
            this.tileSize = tileSize;
        }
    }

}

