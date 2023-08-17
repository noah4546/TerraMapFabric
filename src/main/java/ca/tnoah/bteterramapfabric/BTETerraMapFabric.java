package ca.tnoah.bteterramapfabric;

import ca.tnoah.bteterramapfabric.events.RenderEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.tnoah.bteterramapfabric.events.KeyHandler;

public class BTETerraMapFabric implements ModInitializer {

	public static final String MODID = "bteterramapfabric";
	public static final String NAME = "BTE Terra Map Fabric";

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
			//ProjectionYamlLoader.INSTANCE.refresh(modConfigDir);
			//TMSYamlLoader.INSTANCE.refresh(modConfigDir);
		} catch (Exception e) {
			LOGGER.error("Error caught while parsing map yaml files!");
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e);
		}


		KeyHandler.initializeKeys();
		KeyHandler.initializeKeyEvents();

		WorldRenderEvents.START.register(RenderEvent::onRenderEvent);
	}

	static {
		//Proj4jProjection.registerProjection();
	}
}