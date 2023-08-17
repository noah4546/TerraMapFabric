package ca.tnoah.bteterramapfabric.events;

import ca.tnoah.BTETerraMapFabricConfig;
import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import ca.tnoah.bteterramapfabric.render.TileRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class RenderEvent {


    public static void onRenderEvent(WorldRenderContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (BTETerraMapFabricConfig.isRender) {
            BTETerraMapFabric.LOGGER.info("onRenderEvent() and isRender");

            /*TileRenderer.renderTile(
                    TileRenderer.Settings.builder()
                            .build()
            );*/

            /*try {
                renderTiles(
                        String.format("%s.%s", BTETerraMapFabricConfig.mapServiceCategory, BTETerraMapFabricConfig.mapServiceId),
                        BTETerraMapFabricConfig.getTileMapService(),
                        player.getX(), player.getY(), player.getZ()
                );
            } catch(IllegalArgumentException | NullPointerException e) {
                BTETerraMapFabric.LOGGER.error(e.getMessage());
            }*/
        }
    }

    public static void renderTiles() {
    }

}
