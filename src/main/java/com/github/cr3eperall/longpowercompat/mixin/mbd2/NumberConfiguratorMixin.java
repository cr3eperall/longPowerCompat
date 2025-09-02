package com.github.cr3eperall.longpowercompat.mixin.mbd2;

import com.lowdragmc.lowdraglib.gui.editor.configurator.NumberConfigurator;
import com.lowdragmc.lowdraglib.gui.editor.configurator.ValueConfigurator;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(NumberConfigurator.class)
public abstract class NumberConfiguratorMixin extends ValueConfigurator<Number> {
    @Shadow
    protected boolean isDecimal;

    @Shadow
    protected boolean colorBackground;

    @Shadow
    protected ImageWidget image;

    @Shadow
    protected abstract IGuiTexture getCommonColor();

    public NumberConfiguratorMixin(String name, Supplier<Number> supplier, Consumer<Number> onUpdate, @NotNull Number defaultValue, boolean forceUpdate) {
        super(name, supplier, onUpdate, defaultValue, forceUpdate);
    }

    /**
     * @author cr3eperall
     * @reason fix long value parsing
     * TODO: remove when it is fixed in lowdraglib
     */
    @Overwrite(remap = false)
    private void onNumberUpdate(String s) {
        if (this.value instanceof Integer && !this.value.equals(Integer.parseInt(s))) {
            this.value = Integer.parseInt(s);
            this.updateValue();
        } else if (this.value instanceof Long && !this.value.equals(Long.parseLong(s))) {
            this.value = Long.parseLong(s);
            this.updateValue();
        } else if (this.value instanceof Float && !this.value.equals(Float.parseFloat(s))) {
            this.value = Float.parseFloat(s);
            this.updateValue();
        } else if (this.value instanceof Double && !this.value.equals(Double.parseDouble(s))) {
            this.value = Double.parseDouble(s);
            this.updateValue();
        } else if (this.value instanceof Byte && !this.value.equals(Byte.parseByte(s))) {
            this.value = Byte.parseByte(s);
            this.updateValue();
        } else if (this.value == null) {
            if (this.isDecimal) {
                this.value = Float.parseFloat(s);
            } else {
                this.value = Integer.parseInt(s);
            }

            this.updateValue();
        }

        if (this.colorBackground) {
            this.image.setImage(this.getCommonColor());
        }
    }
}
