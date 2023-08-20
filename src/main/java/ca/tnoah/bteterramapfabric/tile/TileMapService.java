package ca.tnoah.bteterramapfabric.tile;

import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import ca.tnoah.bteterramapfabric.loader.CategoryMapData;
import ca.tnoah.bteterramapfabric.loader.ProjectionJsonLoader;
import ca.tnoah.bteterramapfabric.projection.OutOfProjectionBoundsException;
import ca.tnoah.bteterramapfabric.projection.TileProjection;
import ca.tnoah.bteterramapfabric.render.TileRenderer;
import ca.tnoah.bteterramapfabric.util.NullValidator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.netty.buffer.ByteBufInputStream;
import net.buildtheearth.terraplusplus.util.http.Http;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.http.HttpClient;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TileMapService implements CategoryMapData.ICategoryMapProperty {

    public static final int RETRY_COUNT = 3;
    public static final int DEFAULT_ZOOM = 18;
    static final int DEFAULT_MAX_THREAD = 2;
    public static BufferedImage SOMETHING_WENT_WRONG;


    private transient String source = "";

    private final String name;
    private final String urlTemplate;
    private final TileProjection tileProjection;
    private final TileURLConverter urlConverter;
    private final ExecutorService downloadExecutor;

    @JsonCreator
    private TileMapService(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "tileUrl", required = true) String urlTemplate,
            @JsonProperty(value = "projection", required = true) String projectionName,
            @JsonProperty(value = "maxThread", defaultValue = "2") @Nullable Integer maxThread,
            @JsonProperty(value = "default_zoom", defaultValue = "18") @Nullable Integer defaultZoom
    ) throws CloneNotSupportedException {
        this.name = name;
        this.urlTemplate = urlTemplate;

        TileProjection projectionResult = ProjectionJsonLoader.INSTANCE.getResult().get(projectionName);
        int _defaultZoom = NullValidator.get(defaultZoom, DEFAULT_ZOOM);

        if (projectionResult != null) {
            this.tileProjection = projectionResult.clone();
            this.tileProjection.setDefaultZoom(_defaultZoom);
        } else {
            BTETerraMapFabric.LOGGER.error(String.format("Couldn't find tile projection named \"%s\"", projectionName));
            this.tileProjection = null;
        }

        this.urlConverter = new TileURLConverter(_defaultZoom);
        this.downloadExecutor = Executors.newFixedThreadPool(NullValidator.get(maxThread, DEFAULT_MAX_THREAD));
    }

    public void renderTile(
            WorldRenderContext context,
            int relativeZoom, String tmsId,
            double y, float opacity,
            double playerX, double playerZ,
            int tileDeltaX, int tileDeltaY
    ) {
        if (this.tileProjection == null) return;

        try {
            double[] geoCoord = new double[]{playerX, playerZ};
            int[] tileCoord = this.tileProjection.geoCoordToTileCoord(geoCoord[0], geoCoord[1], relativeZoom);
            int tileX = tileCoord[0] + tileDeltaX, tileY = tileCoord[1] + tileDeltaY;
            final String tileKey = this.genTileKey(tmsId, tileX, tileY, relativeZoom);

            TileImageCache cache = TileImageCache.getInstance();
            cache.cacheAllImagesInQueue();

            if (cache.isTileInDownloadingState(tileKey)) return;

            if (!cache.textureExists(tileKey)) {
                String url = this.urlConverter.convertToUrl(this.urlTemplate, tileX, tileY, relativeZoom);
                this.downloadTile(tileKey, url);
                return;
            }

            double[] geoCoordTL = tileProjection.tileCoordToGeoCoord(tileX, tileY, relativeZoom);
            double[] gameCoordTL = new double[]{geoCoordTL[0], geoCoordTL[1]};

            double[] geoCoordBR = tileProjection.tileCoordToGeoCoord(tileX + 1, tileY + 1, relativeZoom);
            double[] gameCoordBR = new double[]{geoCoordBR[0], geoCoordBR[1]};

            cache.bindTexture(tileKey);
            TileRenderer.renderSingleTile(context, new TileRenderer.Plane(
                    gameCoordTL[0], gameCoordTL[1], Math.abs(gameCoordTL[0] - gameCoordBR[0]), Math.abs(gameCoordTL[1] - gameCoordBR[1])
            ), y, opacity, () -> {
                cache.bindTexture(tileKey);
            });

        } catch (Exception e) {
            BTETerraMapFabric.LOGGER.warn("Caught exception while rendering tile images", e);
        }
    }

    private void downloadTile(String tileKey, String url) {
        TileImageCache cache = TileImageCache.getInstance();
        cache.tileIsBeingDownloaded(tileKey);
        this.downloadExecutor.execute(new TileDownloadingTask(downloadExecutor, tileKey, url, 0));
    }


    public boolean isRelativeZoomAvailable(int relativeZoom) {
        return tileProjection != null && tileProjection.isRelativeZoomAvailable(relativeZoom);
    }


    public String genTileKey(String id, int tileX, int tileY, int zoom) {
        return "tilemap_" + id + "_" + tileX + "_" + tileY + "_" + zoom;
    }


    @Override
    public String toString() {
        return TileMapService.class.getName() + "{name=" + name + ", tile_url=" + urlTemplate + "}";
    }


    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    private static class TileDownloadingTask implements Runnable {

        private static final Timer TIMER = new Timer();

        private final ExecutorService es;
        private final String tileKey, url;
        private final int retry;

        public TileDownloadingTask(ExecutorService es, String tileKey, String url, int retry) {
            this.es = es;
            this.tileKey = tileKey;
            this.url = url;
            this.retry = retry;
        }

        @Override
        public void run() {
            TileImageCache cache = TileImageCache.getInstance();
            boolean shouldRetry = false;

            if (retry >= RETRY_COUNT+1) {
                cache.tileDownloadingComplete(tileKey, SOMETHING_WENT_WRONG);
            }
            else {
                try {
                    ByteBufInputStream stream = new ByteBufInputStream(Http.get(url).get()); // TODO: FIX HTTP
                    cache.tileDownloadingComplete(tileKey, ImageIO.read(stream));
                } catch (Exception e) {
                    BTETerraMapFabric.LOGGER.error("Caught exception while downloading a tile image (" +
                            "TileKey=" + tileKey + ", Retry #" + (retry + 1) + ")");
                    shouldRetry = true;
                }
            }

            if (shouldRetry) {
                TIMER.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        es.execute(new TileDownloadingTask(es, tileKey, url, retry + 1));
                    }
                }, 1000);
            }
        }
    }
}
