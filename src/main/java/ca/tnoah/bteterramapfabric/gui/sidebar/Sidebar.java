package ca.tnoah.bteterramapfabric.gui.sidebar;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class Sidebar extends Screen {

  protected final List<ClickableWidget> elements;

  private SidebarSide side;

  private final int paddingSide;
  private final int paddingTopBottom;
  private final int elementDistance;
  private int verticalSlider;

  public final int elementWidth;

  private int totalHeight;

  protected Sidebar(SidebarSide side, int paddingSide, int paddingTopBottom, int elementDistance, int elementWidth) {
    super(Text.literal("Sidebar"));
    this.elements = new ArrayList<>();
    this.side = side;
    this.paddingSide = paddingSide;
    this.paddingTopBottom = paddingTopBottom;
    this.elementDistance = elementDistance;
    this.verticalSlider = 0;

    this.elementWidth = elementWidth;
  }

  public void setSide(SidebarSide side) {
    this.side = side;
  }

  public void addElement(ClickableWidget drawable) {
    this.elements.add(drawable);
  }

  public void addElements(ClickableWidget... elements) {
    for (ClickableWidget element : elements)
      addElement(element);
  }

  protected void drawScreen() {
    this.clearChildren();

    int translateX = getTranslateX();
    int currentHeight = this.paddingTopBottom - verticalSlider;

    for (ClickableWidget element : elements) {
      if (element == null) continue;

      element.setPosition(translateX, currentHeight);
      currentHeight += element.getHeight() + this.elementDistance;
      this.totalHeight += element.getHeight() + this.elementDistance;

      addDrawable(element);
    }
  }

  private int getTranslateX() {
    if(this.side == SidebarSide.LEFT) {
      return this.paddingSide;
    }
    else {
      return width - elementWidth - this.paddingSide;
    }
  }

  @Override
  protected void init() {
    super.init();
  }

  @Override
  public void tick() {
    super.tick();
  }

  @Override
  public void close() {
    super.close();
  }

}
