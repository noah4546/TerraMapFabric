package ca.tnoah.bteterramapfabric.render;

import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ca.tnoah.bteterramapfabric.BTETerraMapFabric.MODID;


public class TileRenderer {

    // Render
    private final WorldRenderContext context;
    private final Tessellator tessellator;
    private final BufferBuilder buffer;


    // Config
    private final int tileSize;
    private final int yLevel;
    private final float opacity;


    // Self
    private final List<Tile> tiles;


    public TileRenderer(WorldRenderContext context, int tileSize, int yLevel, float opacity) {

        this.context = context;
        this.tessellator = Tessellator.getInstance();
        this.buffer = tessellator.getBuffer();

        this.tileSize = tileSize;
        this.yLevel = yLevel;
        this.opacity = opacity;

        this.tiles = new ArrayList<>();
    }

    public void addTile(Tile tile) {
        this.tiles.add(tile);
    }

    public void addTiles(Tile... tiles) {
        this.tiles.addAll(Arrays.asList(tiles));
    }

    public void addTilesAroundPlayer(ClientPlayerEntity player) {

    }

    public static void setupRender() {
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
    }

    public static void endRender() {
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public void render() {

       setupRender();

        for (Tile tile : this.tiles)
            renderTile(tile);

        endRender();
    }

    private void renderTile(Tile tile) {
        Coord2D coord2D = tile.getCoord2D();
        Plane plane = new Plane(
                coord2D.x * tileSize,
                coord2D.z * tileSize,
                this.tileSize
        );

        MatrixStack matrixStack = getZeroFromCamera(context.camera());
        _renderTile(matrixStack, buffer, plane, this.opacity, this.yLevel);
        RenderSystem.setShaderTexture(0, new Identifier(MODID, "icon.png"));

        this.tessellator.draw();
    }

    public static void renderSingleTile(WorldRenderContext context, Plane p, double yLevel, float opacity, RenderCallback callback) {

        MatrixStack matrixStack = getZeroFromCamera(context.camera());
        Tessellator tessellator = Tessellator.getInstance();

        _renderSingleTile(tessellator, matrixStack, p, yLevel, opacity, callback);
    }

    private static void _renderSingleTile(Tessellator tessellator, MatrixStack matrixStack, Plane p, double yLevel, float opacity, RenderCallback callback) {
        _renderTile(matrixStack, tessellator.getBuffer(), p, opacity, yLevel);

        setupRender();

        callback.onSetTexture();

        tessellator.draw();

        endRender();
    }

    private static void _renderTile(MatrixStack matrixStack, BufferBuilder buffer, Plane p, float opacity, double yLevel) {
        matrixStack.translate(p.x, yLevel, p.z);
        Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

        /*
         *  i=0 -------- i=1
         *   |            |
         *   |    TILE    |
         *   |            |
         *   |            |
         *  i=3 -------- i=2
         */

        // 0
        buffer.vertex(positionMatrix, 0, 0, 0)
                .color(1f, 1f, 1f, opacity)
                .texture(0f, 0f)
                .next();

        // 1
        buffer.vertex(positionMatrix, (float)p.width, 0, 0)
                .color(1f, 1f, 1f, opacity)
                .texture(1f, 0f)
                .next();

        // 2
        buffer.vertex(positionMatrix, (float)p.width, 0, (float)p.height)
                .color(1f, 1f, 1f, opacity)
                .texture(1f, 1f)
                .next();

        // 3
        buffer.vertex(positionMatrix, 0, 0, (float)p.height)
                .color(1f, 1f, 1f, opacity)
                .texture(0f, 1f)
                .next();
    }

    private static MatrixStack getZeroFromCamera(Camera camera) {
        Vec3d targetPosition = new Vec3d(0, 0, 0);
        Vec3d transformedPosition = targetPosition.subtract(camera.getPos());

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
        matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);

        return matrixStack;
    }

    public static class Plane {
        private final double x;
        private final double z;

        private final double width;
        private final double height;

        public Plane(double x, double z, double width, double height) {
            this.x = x;
            this.z = z;
            this.width = width;
            this.height = height;
        }

        public Plane(double x, double z, double size) {
            this(x, z, size, size);
        }

        @Override
        public String toString() {
            return "Plane{" +
                    "x=" + x +
                    ", z=" + z +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    public static class Tile {
        private final int tileX;
        private final int tileY;

        public Tile(int tileX, int tileY) {
            this.tileX = tileX;
            this.tileY = tileY;
        }

        public Coord2D getCoord2D() {
            return coordFromTile(this);
        }

        public static Coord2D coordFromTile(Tile tile) {
            return new Coord2D(tile.tileX, tile.tileY); // TODO: Convert to world coords
        }

        public static Tile tileFromCoord(Coord2D coord2D, int tileSize) {
            return new Tile(coord2D.x / tileSize, coord2D.z / tileSize); // TODO: Convert to world coords
        }
    }

    public record Coord2D(int x, int z) {}

    public interface RenderCallback {
        void onSetTexture();
    }

}
