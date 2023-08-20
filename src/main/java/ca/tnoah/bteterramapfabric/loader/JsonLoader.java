package ca.tnoah.bteterramapfabric.loader;

import ca.tnoah.bteterramapfabric.BTETerraMapFabric;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class JsonLoader<T> {

    private File mapFilesDirectory;
    private final String folderName;
    private final String defaultPath;

    public T result;

    protected JsonLoader(String folderName, String defaultPath) {
        this.folderName = folderName;
        this.defaultPath = defaultPath;
    }

    public void refresh() throws Exception {
        result = loadDefault();

        if (mapFilesDirectory == null) return;

        if (!mapFilesDirectory.exists() && !mapFilesDirectory.mkdir())
            throw new Exception("Map folder creation failed.");

        File[] mapFiles = mapFilesDirectory.listFiles((dir, name) -> name.endsWith(".json"));

        if (mapFiles != null)
            for (File mapFile : mapFiles) {
                String name = mapFile.getName();

                try (FileReader fileReader = new FileReader(mapFile)) {
                    addToResult(result, load(name, fileReader));
                } catch (Exception e) {
                    BTETerraMapFabric.LOGGER.error(e.getMessage());
                }
            }
    }

    public void refresh(String modConfigDirectory) throws Exception {
        this.mapFilesDirectory = new File(
                String.format("%s/%s/%s", modConfigDirectory, BTETerraMapFabric.MODID, folderName)
        );
        this.refresh();
    }

    private T loadDefault() throws IOException {
        return load("default", new InputStreamReader(
                Objects.requireNonNull(JsonLoader.class.getClassLoader().getResourceAsStream(defaultPath)),
                StandardCharsets.UTF_8
        ));
    }

    protected abstract T load(String fileName, Reader fileReader) throws IOException;
    protected abstract void addToResult(T originalT, T newT);

    public File getMapFilesDirectory() {
        return mapFilesDirectory;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
