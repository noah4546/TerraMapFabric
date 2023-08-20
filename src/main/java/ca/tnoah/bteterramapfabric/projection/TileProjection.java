package ca.tnoah.bteterramapfabric.projection;

import ca.tnoah.bteterramapfabric.tile.TileMapService;

public abstract class TileProjection {

    protected int defaultZoom = TileMapService.DEFAULT_ZOOM;


    /**
     * Converts the player's position into the tile coordinates
     *
     * @param longitude Longitude of the player's position
     * @param latitude Latitude of the player's position
     * @param relativeZoom Tile zoom relative to the default one
     * @return Tile coordinate
     * @throws OutOfProjectionBoundsException When the player is out of bounds from the projection
     */
    public final int[] geoCoordToTileCoord(double longitude, double latitude, int relativeZoom)
            throws OutOfProjectionBoundsException {

        return this.toTileCoord(longitude, latitude, relativeZoomToAbsolute(relativeZoom));
    }


    protected abstract int[] toTileCoord(double longitude, double latitude, int absoluteZoom)
            throws OutOfProjectionBoundsException;

    /**
     * Converts a tile coordinates into its corresponding geographic coordinates (WGS84)
     * @param tileX Tile X
     * @param tileY Tile Y
     * @param relativeZoom Tile zoom relative to the default one
     * @return Geographic coordinate (WGS84)
     * @throws OutOfProjectionBoundsException When the tile is out of bounds from the projection
     */
    public final double[] tileCoordToGeoCoord(int tileX, int tileY, int relativeZoom)
            throws OutOfProjectionBoundsException {

        return this.toGeoCoord(tileX, tileY, relativeZoomToAbsolute(relativeZoom));
    }

    protected abstract double[] toGeoCoord(int tileX, int tileY, int absoluteZoom)
            throws OutOfProjectionBoundsException, OutOfProjectionBoundsException;


    @Override
    public abstract TileProjection clone() throws CloneNotSupportedException;


    public final int relativeZoomToAbsolute(int relativeZoom) {
        return defaultZoom + relativeZoom;
    }
    public final boolean isRelativeZoomAvailable(int relativeZoom) {
        return this.isAbsoluteZoomAvailable(defaultZoom + relativeZoom);
    }
    public abstract boolean isAbsoluteZoomAvailable(int absoluteZoom);

    public void setDefaultZoom(int defaultZoom) {
        this.defaultZoom = defaultZoom;
    }
}
