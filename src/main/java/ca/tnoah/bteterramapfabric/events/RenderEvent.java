package ca.tnoah.bteterramapfabric.events;

import ca.tnoah.BTETerraMapFabricConfig;
import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;

import java.util.function.BiConsumer;

public class RenderEvent {


    public static void onRenderEvent(WorldRenderContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (BTETerraMapFabricConfig.isRender) {
            BTETerraMapFabric.LOGGER.info("onRenderEvent() and isRender");
            try {
                /*renderTiles(
                        String.format("%s.%s", BTETerraMapFabricConfig.mapServiceCategory, BTETerraMapFabricConfig.mapServiceId),
                        BTETerraMapFabricConfig.getTileMapService(),
                        player.getX(), player.getY(), player.getZ()
                );*/
            } catch(IllegalArgumentException | NullPointerException e) {
                BTETerraMapFabric.LOGGER.error(e.getMessage());
            }
        }
    }

    public static void renderTiles() {
    }

}
