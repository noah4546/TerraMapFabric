package ca.tnoah.bteterramapfabric.loader;

import ca.tnoah.bteterramapfabric.BTETerraMapFabricConfig;
import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import ca.tnoah.bteterramapfabric.tile.TileMapService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;


public class TMSJsonLoader extends JsonLoader<CategoryMapData<TileMapService>> {

    private static final ObjectMapper JACKSON_MAPPER = new ObjectMapper();

    public static final TMSJsonLoader INSTANCE = new TMSJsonLoader(
            "maps", String.format("assets/%s/default_maps.json", BTETerraMapFabric.MODID)
    );

    public TMSJsonLoader(String folderName, String defaultPath) {
        super(folderName, defaultPath);
    }

    @Override
    protected CategoryMapData<TileMapService> load(String fileName, Reader reader) throws IOException {
        CategoryMapData<TileMapService> result = JACKSON_MAPPER.readValue(reader, new TypeReference<>() {});
        result.setSource(fileName);
        return result;
    }

    @Override
    protected void addToResult(CategoryMapData<TileMapService> originalT, CategoryMapData<TileMapService> newT) {
        originalT.append(newT);
    }

    static {
        JACKSON_MAPPER.findAndRegisterModules();
    }

}
