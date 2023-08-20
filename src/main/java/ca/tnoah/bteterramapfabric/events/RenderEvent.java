package ca.tnoah.bteterramapfabric.events;

import ca.tnoah.bteterramapfabric.BTETerraMapFabricConfig;
import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import ca.tnoah.bteterramapfabric.render.RenderSettings;
import ca.tnoah.bteterramapfabric.tile.TileImageCache;
import ca.tnoah.bteterramapfabric.tile.TileMapService;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.function.BiConsumer;

public class RenderEvent {


    public static void onRenderEvent(WorldRenderContext context) {
        if (BTETerraMapFabricConfig.isRender) {
            try {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;

                if (player == null) {
                    BTETerraMapFabric.LOGGER.error("Player not found!");
                    return;
                }

                double px = player.getX();
                double pz = player.getZ();

                renderTiles(
                        context,
                        BTETerraMapFabricConfig.mapServiceCategory + "." + BTETerraMapFabricConfig.mapServiceId,
                        BTETerraMapFabricConfig.getTileMapService(),
                        px, pz
                );
            } catch (NullPointerException ex) {
                BTETerraMapFabric.LOGGER.error(ex.getMessage());
            }
        }
    }

    public static void renderTiles(WorldRenderContext context, String tmsId, TileMapService tms, double px, double pz) {

        if (tms == null) {
            BTETerraMapFabric.LOGGER.error("TMS is null in renderTiles()");
            return;
        }
        /*if (Projections.getServerProjection() == null) {
            BTETerraMapFabric.LOGGER.error("Server Projection is null in renderTiles()");
            return;
        }*/

        RenderSettings settings = BTETerraMapFabric.RENDER_SETTINGS;

        int size = settings.radius -1;

        BiConsumer<Integer, Integer> drawTile = (dx, dy) -> {
            if (Math.abs(dx) > size || Math.abs(dy) > size) return;

            tms.renderTile(
                    context,
                    settings.zoom, tmsId,
                    settings.yLevel + 0.1, settings.opacity,
                    px + settings.xAlign, pz + settings.zAlign,
                    dx, dy
            );
        };

        for (int i = 0; i < 2 * size + 1; ++i) {
            if (i == 0)
                drawTile.accept(0, 0);

            for (int j = 0; j < i; ++j) {
                drawTile.accept(-j, j - i);
                drawTile.accept(j - i, j);
                drawTile.accept(j, i - j);
                drawTile.accept(i - j, -j);
            }
        }

        TileImageCache.getInstance().cleanup();

    }

}
