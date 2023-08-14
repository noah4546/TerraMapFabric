package ca.tnoah.bteterramapfabric.gui.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

public class BooleanButton extends ClickableWidget implements ButtonWidget.PressAction {

    private final ToggleAction toggleAction;
    private final ButtonWidget button;
    private boolean on = false;

    public BooleanButton(int x, int y, int width, int height, String prefix, ToggleAction toggleAction) {
        super(x, y, width, height, Text.literal(prefix));

        button = ButtonWidget.builder(Text.literal(prefix), this)
                .dimensions(x, y, width, height)
                .build();

        this.toggleAction = toggleAction;
        applyTextToButton();
    }

    public BooleanButton(int width, int height, String prefix, ToggleAction toggleAction) {
        this(0, 0, width, height, prefix, toggleAction);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        button.setPosition(getX(), getY());
        button.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void onPress(ButtonWidget button) {
        on = !on;
        applyTextToButton();
        toggleAction.onToggle(this, on);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        button.onClick(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.button.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.button.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.button.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        button.setPosition(x, y);
    }

    private void applyTextToButton() {
        String buttonText = String.format("%s: %s", getMessage().getString(), booleanToFormattedI18n(on));
        button.setMessage(Text.literal(buttonText));
    }

    private static String booleanToFormattedI18n(boolean b) {
        return b ?
                "§a" + I18n.translate("options.on") :
                "§c" + I18n.translate("options.off");
    }

    @Environment(value = EnvType.CLIENT)
    public static interface ToggleAction {
        public void onToggle(BooleanButton button, boolean value);
    }

}
