package ca.tnoah.bteterramapfabric;

import ca.tnoah.bteterramapfabric.events.RenderEvent;
import ca.tnoah.bteterramapfabric.loader.ProjectionJsonLoader;
import ca.tnoah.bteterramapfabric.loader.TMSJsonLoader;
import ca.tnoah.bteterramapfabric.projection.Proj4jProjection;
import ca.tnoah.bteterramapfabric.render.RenderSettings;
import ca.tnoah.bteterramapfabric.render.TileRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.tnoah.bteterramapfabric.events.KeyHandler;

import static ca.tnoah.bteterramapfabric.BTETerraMapFabricConfig.mapServiceCategory;
import static ca.tnoah.bteterramapfabric.BTETerraMapFabricConfig.mapServiceId;

public class BTETerraMapFabric implements ModInitializer {

	public static final String MODID = "bteterramapfabric";
	public static final String NAME = "BTE Terra Map Fabric";

	public static final RenderSettings RENDER_SETTINGS = new RenderSettings();

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		try {
			String modConfigDir = FabricLoader.getInstance().getConfigDir().toString();
			LOGGER.info(modConfigDir);
			ProjectionJsonLoader.INSTANCE.refresh(modConfigDir);
			TMSJsonLoader.INSTANCE.refresh(modConfigDir);
		} catch (Exception e) {
			LOGGER.error("Error caught while parsing map json files!");
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e);
		}


		KeyHandler.initializeKeys();
		KeyHandler.initializeKeyEvents();

		//WorldRenderEvents.START.register(RenderEvent::onRenderEvent);
		WorldRenderEvents.END.register(RenderEvent::onRenderEvent);

		/*WorldRenderEvents.END.register(context -> {

			//ClientPlayerEntity player = MinecraftClient.getInstance().player;

			TileRenderer tileRenderer = new TileRenderer(context, 16, 64, 0.5f);
			tileRenderer.addTiles(
					new TileRenderer.Tile(0, 0),
					new TileRenderer.Tile(0, 1),
					new TileRenderer.Tile(0, 2)
			);
			//tileRenderer.addTilesAroundPlayer(player);
			tileRenderer.render();
		});*/
	}

	private void test(WorldRenderContext context) {
		Camera camera = context.camera();

		Vec3d targetPosition = new Vec3d(0, 100, 0);
		Vec3d transformedPosition = targetPosition.subtract(camera.getPos());

		MatrixStack matrixStack = new MatrixStack();
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
		matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);

		Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
		buffer.vertex(positionMatrix, 0, 1, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
		buffer.vertex(positionMatrix, 0, 0, 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
		buffer.vertex(positionMatrix, 1, 0, 0).color(0f, 1f, 0f, 1f).texture(1f, 1f).next();
		buffer.vertex(positionMatrix, 1, 1, 0).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();

		RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
		RenderSystem.setShaderTexture(0, new Identifier(MODID, "icon.png"));
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.disableCull();
		RenderSystem.depthFunc(GL11.GL_ALWAYS);

		tessellator.draw();

		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.enableCull();
	}

	static {

		Proj4jProjection.registerProjection();
	}
}