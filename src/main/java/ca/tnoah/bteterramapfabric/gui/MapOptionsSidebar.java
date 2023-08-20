package ca.tnoah.bteterramapfabric.gui;

import ca.tnoah.bteterramapfabric.BTETerraMapFabricConfig;
import ca.tnoah.bteterramapfabric.gui.custom.BooleanButton;
import ca.tnoah.bteterramapfabric.gui.custom.Slider;
import ca.tnoah.bteterramapfabric.gui.custom.TextFieldWithLabel;
import ca.tnoah.bteterramapfabric.gui.sidebar.Sidebar;
import ca.tnoah.bteterramapfabric.gui.sidebar.SidebarSide;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MapOptionsSidebar extends Sidebar {

  private static MapOptionsSidebar instance;

  private interface Default {
    int height = 20;
    int width = 200;
  }

  // General
  BooleanButton renderToggle;
  TextFieldWithLabel mapLevel;
  Slider mapOpacity;

  // Map Source


  protected MapOptionsSidebar() {
    super(SidebarSide.LEFT, 20, 20, 7, Default.width);

    this.textRenderer = MinecraftClient.getInstance().textRenderer;
    initGeneral();
    initMapSource();
    drawScreen();
  }

  @Override
  protected void init() {
    super.init();
  }

  private void initGeneral() {
    renderToggle = new BooleanButton(Default.width, Default.height, "Render", (button, value) -> {
      BTETerraMapFabricConfig.mapRenderToggle();
    });

    TextWidget mapLevelLabel = new TextWidget(
            Default.width, Default.height, Text.literal("Map Y Level:"), textRenderer
    );

    TextFieldWidget mapLevelValue = new TextFieldWidget(
            textRenderer, 0, 0, Default.width, Default.height, Text.literal("Map Level")
    );

    mapLevel = new TextFieldWithLabel(Default.width, Default.height, Text.literal("Map Y Level"));
    mapLevel.setLabel(mapLevelLabel);
    mapLevel.setValue(mapLevelValue);

    mapOpacity = new Slider(Default.width, Default.height, "Opacity", 0.5);

    addElements(renderToggle, mapLevel, mapOpacity);
  }

  private void initMapSource() {

  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    renderToggle.mouseClicked(mouseX, mouseY, button);

    if (mapLevel.isMouseOver(mouseX, mouseY))
      setFocused(mapLevel.getValue());

    if (mapOpacity.isMouseOver(mouseX, mouseY))
      setFocused(mapOpacity);

    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

    if (mapOpacity.isMouseOver(mouseX, mouseY) || mapOpacity.isFocused())
      mapOpacity.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

    return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {

    mapOpacity.setFocused(false);

    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  public static void open() {
    if (instance == null) instance = new MapOptionsSidebar();
    MinecraftClient.getInstance().setScreen(instance);
  }

  @Override
  public void close() {
    super.close();
    instance = null;
  }
}
