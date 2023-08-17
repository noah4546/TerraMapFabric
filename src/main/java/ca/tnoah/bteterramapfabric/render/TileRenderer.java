package ca.tnoah.bteterramapfabric.render;

import ca.tnoah.bteterramapfabric.BTETerraMapFabric;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import static ca.tnoah.bteterramapfabric.BTETerraMapFabric.MODID;


public class TileRenderer {

    public static void renderSingleTile(WorldRenderContext context, Plane p, int yLevel, float opacity, String tmsId) {

        MatrixStack matrixStack = getZeroFromCamera(context.camera());
        Tessellator tessellator = Tessellator.getInstance();

        _renderSingleTile(tessellator, matrixStack, p, yLevel, opacity, tmsId);
    }

    private static void _renderSingleTile(Tessellator tessellator, MatrixStack matrixStack, Plane p, int yLevel, float opacity, String tmsId) {
        _renderTile(matrixStack, tessellator.getBuffer(), p, opacity, yLevel);

        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);

        if (tmsId == null)
            RenderSystem.setShaderTexture(0, new Identifier(MODID, "icon.png"));

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);

        tessellator.draw();

        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void _renderTile(MatrixStack matrixStack, BufferBuilder buffer, Plane p, float opacity, int yLevel) {
        matrixStack.translate(p.x, yLevel, p.z);
        Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();

        /*
         *  i=0 -------- i=1
         *   |            |
         *   |    TILE    |
         *   |            |
         *   |            |
         *  i=3 -------- i=2
         */
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

        // 0
        buffer.vertex(positionMatrix, 0, 0, 0)
                .color(1f, 1f, 1f, opacity)
                .texture(0f, 0f)
                .next();

        // 1
        buffer.vertex(positionMatrix, p.width, 0, 0)
                .color(1f, 1f, 1f, opacity)
                .texture(1f, 0f)
                .next();

        // 2
        buffer.vertex(positionMatrix, p.width, 0, p.height)
                .color(1f, 1f, 1f, opacity)
                .texture(1f, 1f)
                .next();

        // 3
        buffer.vertex(positionMatrix, 0, 0, p.height)
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
        private final int x;
        private final int z;

        private final int width;
        private final int height;

        public Plane(int x, int z, int width, int height) {
            this.x = x;
            this.z = z;
            this.width = width;
            this.height = height;
        }

        public Plane(int x, int z, int size) {
            this(x, z, size, size);
        }
    }

    public static class XZ {
        private final int x;
        private final int z;

        public XZ(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }


    public static class Settings {

        // Location
        private final int x;
        private final int y;
        private final int z;

        // Size
        private final int xSize;
        private final int zSize;

        // Other
        private final float opacity;

        public Settings(int x, int y, int z, int xSize, int zSize, float opacity) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.xSize = xSize;
            this.zSize = zSize;
            this.opacity = opacity;
        }

        public static Builder builder() {
            return new Builder();
        }

        @Override
        public String toString() {
            return "Settings{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    ", xSize=" + xSize +
                    ", zSize=" + zSize +
                    ", opacity=" + opacity +
                    '}';
        }

        public static class Builder {
            // Location
            private int x = 0;
            private int y = 64;
            private int z = 0;

            // Size
            private int xSize = 16;
            private int zSize = 16;

            // Other
            private float opacity = 1f;

            public Builder() {

            }

            /**
             * Set the origin position of the tile
             *
             * @param x x coord of origin [ East(+), West(-) ]
             * @param z z coord of origin [ South(+), North(-) ]
             * @return {@link Builder}
             */
            public Builder position(int x, int z) {
                this.x = x;
                this.z = z;
                return this;
            }

            /**
             * Set the y position of the tile (height)
             *
             * @param y y position (height)
             * @return {@link Builder}
             */
            public Builder yPosition(int y) {
                this.y = y;
                return this;
            }

            /**
             * Set the size of the tile
             *
             * @param size size in x and z direction (from origin south/east)
             * @return {@link Builder}
             */
            public Builder size(int size) {
                this.xSize = size;
                this.zSize = size;
                return this;
            }

            /**
             * Set the size of the tile
             *
             * @param xSize x size (from origin east)
             * @param zSize z size (from origin south)
             * @return {@link Builder}
             */
            public Builder size(int xSize, int zSize) {
                this.xSize = xSize;
                this.zSize = zSize;
                return this;
            }

            /**
             * Set the opacity of the tile.
             *
             * @param opacity opacity (percentage 0 to 1)
             * @return {@link Builder}
             */
            public Builder opacity(float opacity) {
                if (opacity > 1 || opacity < 1) return this;

                this.opacity = opacity;
                return this;
            }

            /**
             * Build the Settings
             *
             * @return {@link Settings}
             */
            public Settings build() {
                return new Settings(
                        x, y, z,
                        xSize, zSize,
                        opacity
                );
            }
        }
    }

}
