package ca.tnoah.bteterramapfabric.gui.custom;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class Slider extends SliderWidget {

    private final String text;

    public Slider(int x, int y, int width, int height, String text, double value) {
        super(x, y, width, height, Text.literal(text), value);

        this.text = text;
        updateMessage();
    }

    public Slider(int width, int height, String text, double value) {
        this(0, 0, width, height, text, value);
    }

    @Override
    protected void updateMessage() {
        setMessage(Text.literal(text + ": " + String.format("%.2f", value)));
    }

    @Override
    protected void applyValue() {

    }
}
