package ca.tnoah.bteterramapfabric.gui.custom;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class TextFieldWithLabel extends SplitWidget<TextWidget, TextFieldWidget> {

    public TextFieldWithLabel(int width, int height, Text message) {
        super(width, height, message);
    }

    public TextFieldWithLabel(int width, int height, Text message, TextWidget label, TextFieldWidget value) {
        super(width, height, message, label, value);
    }

    public void setLabel(TextWidget label) {
        setWidgetLeft(label);
    }

    public void setValue(TextFieldWidget value) {
        setWidgetRight(value);
    }

    public TextWidget getLabel() {
        return getWidgetLeft();
    }

    public TextFieldWidget getValue() {
        return getWidgetRight();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);

        if (focused)
            getValue().setFocused(true);
    }
}
