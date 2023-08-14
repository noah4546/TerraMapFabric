package ca.tnoah.bteterramapfabric.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import ca.tnoah.BTETerraMapFabricConfig;

@Environment(EnvType.CLIENT)
public class KeyHandler {

	private static KeyBinding mapOptionsKey, mapToggleKey;

	public static void initializeKeys() {
		KeyBindingHelper.registerKeyBinding(mapOptionsKey = new KeyBinding(
				"key.bteterramapfabric.options_ui",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_GRAVE_ACCENT,
				"key.bteterramapfabric.category"));

		KeyBindingHelper.registerKeyBinding(mapToggleKey = new KeyBinding(
				"key.bteterramapfabric.toggle",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"key.bteterramapfabric.category"));
	}

	public static void initializeKeyEvents() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			if (mapOptionsKey.wasPressed())
				BTETerraMapFabricConfig.mapOptionsToggle();

			if (mapToggleKey.wasPressed())
				BTETerraMapFabricConfig.mapRenderToggle();

		});
  }

}
