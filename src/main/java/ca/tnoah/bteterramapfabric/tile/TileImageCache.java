package ca.tnoah.bteterramapfabric.tile;

import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.NonNull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static ca.tnoah.bteterramapfabric.tile.TileImageCache.*;

public class TileImageCache implements ITileImageCache {

    private static final int CACHE_AT_A_TIME = 5;


    public static final TileImageCache instance = new TileImageCache(1000 * 60 * 5, 10000);
    public static TileImageCache getInstance() { return instance; }


    private static final boolean DEBUG = false;
    private static void log(String message) {
        if(DEBUG) BTETerraMapFabric.LOGGER.debug(message);
    }


    private final int maximumSize;
    private final Map<String, TextureId> textureIdMap;
    private final Set<String> downloadingTileKeys;
    private final long expireMilliseconds;

    private Queue<Map.Entry<String, BufferedImage>> imageRenderQueue;

    private TileImageCache(long expireMilliseconds, int maximumSize) {
        this.textureIdMap = new HashMap<>();
        this.downloadingTileKeys = new HashSet<>();
        this.expireMilliseconds = expireMilliseconds;
        this.maximumSize = maximumSize;
        this.imageRenderQueue = new LinkedList<>();
    }

    @Override
    public boolean isTileInDownloadingState(String tileKey) {
        synchronized (this) {
            return downloadingTileKeys.contains(tileKey);
        }
    }

    @Override
    public void tileIsBeingDownloaded(String tileKey) {
        synchronized (this) {
            downloadingTileKeys.add(tileKey);
        }
    }

    @Override
    public boolean textureExists(String tileKey) {
        synchronized (this) {
            return textureIdMap.containsKey(tileKey);
        }
    }

    @Override
    public void cacheAllImagesInQueue() {
        synchronized (this) {
            for (int i = 0; i < CACHE_AT_A_TIME && !imageRenderQueue.isEmpty(); i++) {

                Map.Entry<String, BufferedImage> entry = imageRenderQueue.remove();
                if (entry == null) continue;

                String tileKey = entry.getKey();
                BufferedImage image = entry.getValue();

                try {
                    if (entry.getValue() != null)
                        addTexture(tileKey, image);
                } catch (Exception e) {
                    imageRenderQueue.add(new AbstractMap.SimpleEntry<>(tileKey, image));
                }
            }
        }
    }

    @Override
    public void bindTexture(String tileKey) {
        synchronized (this) {
            Identifier id = validateAndGetIdentifier(tileKey);
            RenderSystem.setShaderTexture(0, id);
        }
    }

    @Override
    public void tileDownloadingComplete(String tileKey, BufferedImage image) {
        synchronized (this) {
            downloadingTileKeys.remove(tileKey);
            imageRenderQueue.add(new AbstractMap.SimpleEntry<>(tileKey, image));
        }
    }

    @Override
    public void cleanup() {
        // TODO: IMPLEMENT
    }

    @Override
    public void deleteAllRenderQueues() {
        // TODO: IMPLEMENT
    }

    private void addTexture(String tileKey, BufferedImage image) {
        synchronized (this) {
            if(this.maximumSize != -1 && textureIdMap.size() >= this.maximumSize) {
                this.deleteOldestTexture();
            }
            this.addTexture(tileKey, initializeTile(tileKey, image));
        }
    }

    private void addTexture(String tileKey, Identifier id) {
        synchronized (this) {
            textureIdMap.put(tileKey, new TextureId(id, System.currentTimeMillis()));
            log(String.format("Added Texture: %s (Size: %s)", tileKey, textureIdMap.size()));
        }
    }

    private static Identifier initializeTile(String tileKey, BufferedImage image) {
        Identifier id = new Identifier(BTETerraMapFabric.MODID, String.format("tilemap/%s.png", tileKey));
        registerBufferedImageTexture(id, image);
        return id;
    }

    private void deleteOldestTexture() {
        String oldestKey = null; long oldest = Long.MAX_VALUE;
        for (Map.Entry<String, TextureId> entry : textureIdMap.entrySet()) {
            TextureId wrapper = entry.getValue();
            if(wrapper.lastUpdated < oldest) {
                oldestKey = entry.getKey();
                oldest = wrapper.lastUpdated;
            }
        }
        if(oldestKey != null) {
            this.deleteTexture(oldestKey);
        }
    }

    private void deleteTexture(String tileKey) {
        synchronized (this) {
            if (textureIdMap.containsKey(tileKey)) {
                Identifier id = textureIdMap.get(tileKey).identifier;
                textureIdMap.remove(tileKey);
                MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().destroyTexture(id));
                log("Deleted Texture: " + tileKey);
            }
        }
    }

    private Identifier validateAndGetIdentifier(String tileKey) {
        synchronized (this) {
            if (!textureIdMap.containsKey(tileKey)) throw new NullPointerException();
            return textureIdMap.get(tileKey).identifier;
        }
    }

    private static void registerBufferedImageTexture(@NonNull Identifier i, @NonNull BufferedImage bi) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", out);
            byte[] bytes = out.toByteArray();

            ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
            data.flip();
            NativeImageBackedTexture tex = new NativeImageBackedTexture(NativeImage.read(data));
            MinecraftClient.getInstance()
                    .execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(i, tex));
        } catch (Exception e) { // should never happen, but just in case
            log(e.getMessage());
        }
    }

    private static class TextureId {
        final Identifier identifier;
        long lastUpdated;

        public TextureId(Identifier identifier, long date) {
            this.identifier = identifier;
            this.lastUpdated = date;
        }
    }


}

