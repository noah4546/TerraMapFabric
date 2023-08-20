package ca.tnoah.bteterramapfabric;

import ca.tnoah.bteterramapfabric.gui.MapOptionsSidebar;
import ca.tnoah.bteterramapfabric.loader.TMSJsonLoader;
import ca.tnoah.bteterramapfabric.render.TileRenderer;
import ca.tnoah.bteterramapfabric.tile.TileImageCache;
import ca.tnoah.bteterramapfabric.tile.TileMapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
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

  @Getter
  public static String mapServiceCategory = "global";

  @Getter
  public static String mapServiceId = "osm";


  public static TileMapService getTileMapService() {
    return ConfigDataCache.tileMapService;
  }

  public static void setTileMapService(String categoryName, String mapId) {
    ConfigDataCache.tileMapService = TMSJsonLoader.INSTANCE.result.getItem(categoryName, mapId);
    mapServiceCategory = categoryName;
    mapServiceId = mapId;
    TileImageCache.getInstance().deleteAllRenderQueues();
  }

  private static class ConfigDataCache {
    /*
     * I couldn't put this in the main class, so I made a subclass BTRConfig.ConfigDataCache and put the
     * tms variable here.
     */
    private static TileMapService tileMapService =
            TMSJsonLoader.INSTANCE.result.getItem(mapServiceCategory, mapServiceId);
  }

}
