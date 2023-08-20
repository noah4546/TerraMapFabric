package ca.tnoah.bteterramapfabric.models;

public class Map {

    private final String name;
    private final String tileUrl;
    private final String projection;
    private final int maxThread;

    public Map(String name, String tileUrl, String projection, int maxThread) {
        this.name = name;
        this.tileUrl = tileUrl;
        this.projection = projection;
        this.maxThread = maxThread;
    }

    public String getName() {
        return name;
    }

    public String getTileUrl() {
        return tileUrl;
    }

    public String getProjection() {
        return projection;
    }

    public int getMaxThread() {
        return maxThread;
    }
}
