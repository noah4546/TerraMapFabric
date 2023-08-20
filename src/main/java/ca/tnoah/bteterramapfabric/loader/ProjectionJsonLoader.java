package ca.tnoah.bteterramapfabric.loader;

import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import ca.tnoah.bteterramapfabric.BTETerraMapFabricConfig;
import ca.tnoah.bteterramapfabric.projection.JsonTileProjection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class ProjectionJsonLoader extends JsonLoader<Map<String, JsonTileProjection>> {

    private static final ObjectMapper JACKSON_MAPPER = new ObjectMapper();

    public static final ProjectionJsonLoader INSTANCE = new ProjectionJsonLoader(
            "projections", String.format("assets/%s/default_projections.json", BTETerraMapFabric.MODID)
    );

    public ProjectionJsonLoader(String folderName, String defaultPath) {
        super(folderName, defaultPath);
    }

    @Override
    protected Map<String, JsonTileProjection> load(String fileName, Reader fileReader) throws IOException {
        return JACKSON_MAPPER.readValue(fileReader, new TypeReference<>() {});
    }

    @Override
    protected void addToResult(Map<String, JsonTileProjection> originalT, Map<String, JsonTileProjection> newT) {
        originalT.putAll(newT);
    }

    static {
        JACKSON_MAPPER.findAndRegisterModules();
    }

}
