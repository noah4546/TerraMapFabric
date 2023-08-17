package ca.tnoah;

import ca.tnoah.bteterramapfabric.gui.MapOptionsSidebar;
import lombok.Getter;
import lombok.Setter;

public class BTETerraMapFabricConfig {

  @Getter @Setter
  public static boolean isRender = false;

  public static void mapRenderToggle() {
    isRender = !isRender;
  }

  public static void mapOptionsToggle() {
    MapOptionsSidebar.open();
  }


}
