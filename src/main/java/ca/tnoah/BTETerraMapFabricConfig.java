package ca.tnoah;

import ca.tnoah.bteterramapfabric.gui.MapOptionsSidebar;
import ca.tnoah.bteterramapfabric.render.TileRenderer;
import lombok.Getter;
import lombok.Setter;

public class BTETerraMapFabricConfig {

  @Getter @Setter
  public static boolean isRender = false;

  public static void mapRenderToggle() {
    isRender = !isRender;

    /*TileRenderer.renderTile(
            TileRenderer.Settings.builder()
                    .build()
    );*/
  }

  public static void mapOptionsToggle() {
    MapOptionsSidebar.open();
  }


}
