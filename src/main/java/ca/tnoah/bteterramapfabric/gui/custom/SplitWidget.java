package ca.tnoah.bteterramapfabric.gui.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class SplitWidget<Left extends ClickableWidget, Right extends ClickableWidget> extends ClickableWidget {

    private Left left;
    private Right right;

    private float split = 0.5f;

    public SplitWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    public SplitWidget(int width, int height, Text message) {
        this(0, 0, width, height, message);
    }

    public SplitWidget(int width, int height, Text message, Left left, Right right) {
        this(width, height, message);

        this.left = left;
        this.right = right;
    }

    public void setWidgetLeft(Left widget) {
        this.left = widget;
    }

    public void setWidgetRight(Right widget) {
        this.right = widget;
    }

    public Left getWidgetLeft() {
        return this.left;
    }

    public Right getWidgetRight() {
        return this.right;
    }

    public boolean setSplit(float split) {
        if (split <= 0 || split >= 1)
            return false;

        this.split = split;
        return true;
    }

    private void drawItems() {
        if (left == null && right == null)
            return;

        if (left == null || right == null) {

            ClickableWidget drawThis = left == null ? right : left;

            drawThis.setPosition(getX(), getY());
            drawThis.setWidth(this.getWidth());

            return;
        }

        int leftWidth = (int)(getWidth() * split);
        int rightWidth = getWidth() - leftWidth;

        left.setWidth(leftWidth);
        right.setWidth(rightWidth);

        left.setPosition(getX(), getY());
        right.setPosition(getX() + leftWidth, getY());
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        drawItems();
        left.render(context, mouseX, mouseY, delta);
        right.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
