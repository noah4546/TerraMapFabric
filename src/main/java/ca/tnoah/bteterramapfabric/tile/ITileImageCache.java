package ca.tnoah.bteterramapfabric.tile;

import java.awt.image.BufferedImage;

public interface ITileImageCache {
    boolean isTileInDownloadingState(String tileKey);
    void tileIsBeingDownloaded(String tileKey);
    boolean textureExists(String tileKey);
    void cacheAllImagesInQueue();
    void bindTexture(String tileKey);
    void tileDownloadingComplete(String tileKey, BufferedImage image);
    void cleanup();
    void deleteAllRenderQueues();
}
