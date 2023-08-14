package ca.tnoah.bteterramapfabric.gui.sidebar;

import net.minecraft.client.font.TextRenderer;

public abstract class SidebarElement {

  public Sidebar parent;
  protected TextRenderer textRenderer;
  public boolean hide;

  public SidebarElement() {
    this.hide = false;
  }

  public final void initGui(Sidebar parent, TextRenderer textRenderer) {
    this.parent = parent;
    this.textRenderer = textRenderer;
    this.init();
  }

  public abstract int getHeight();

  protected abstract void init();

  public abstract void onWidthChange(int newWidth);

  public abstract void updateScreen();
  public abstract void drawScreen(int mouseX, int mouseY, float partialTicks);

  public abstract boolean mouseClicked(int mouseX, int mouseY, int mouseButton);
  public abstract void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick);
  public abstract void mouseReleased(int mouseX, int mouseY, int state);

  public abstract boolean keyTyped(char key, int keyCode);
  
}
