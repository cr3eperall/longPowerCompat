package com.github.cr3eperall.longpowercompat.mbd2;

import com.lowdragmc.lowdraglib.gui.editor.configurator.ConfiguratorGroup;
import com.lowdragmc.lowdraglib.gui.editor.configurator.NumberConfigurator;
import com.lowdragmc.lowdraglib.gui.texture.*;
import com.lowdragmc.lowdraglib.gui.widget.ProgressWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.mbd2.api.capability.recipe.RecipeCapability;
import com.lowdragmc.mbd2.api.recipe.content.Content;
import com.lowdragmc.mbd2.api.recipe.content.SerializerLong;
import com.lowdragmc.mbd2.common.gui.recipe.CornerNumberWidget;
import com.lowdragmc.mbd2.utils.EnergyFormattingUtil;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluxNetworksRecipeCapability extends RecipeCapability <Long>{
    public static final FluxNetworksRecipeCapability CAP = new FluxNetworksRecipeCapability();
    //TODO: change textures
    public final static ResourceTexture ENERGY_BAR = new ResourceTexture("mbd2:textures/gui/energy_bar_base.png");
    public final static ResourceBorderTexture ENERGY_BASE = new ResourceBorderTexture("mbd2:textures/gui/energy_bar_background.png", 42, 14, 1, 1);

    protected FluxNetworksRecipeCapability(){
        super("fluxnetworks_energy", SerializerLong.INSTANCE);
    }

    @Override
    public Long createDefaultContent() {
        return 512L;
    }

    @Override
    public Widget createPreviewWidget(Long content) {
        var previewGroup = new WidgetGroup(0,0,18,18);
        previewGroup.setBackground(new ResourceTexture("mbd2:textures/gui/forge_energy.png"));
        previewGroup.addWidget(new CornerNumberWidget(0, 0, 18, 18).setValue(content));
        return previewGroup;
    }

    @Override
    public Widget createXEITemplate() {
        var energyBar = new ProgressWidget(ProgressWidget.JEIProgress, 0, 0, 50, 14, new ProgressTexture(
                IGuiTexture.EMPTY, ENERGY_BAR
        ));
        energyBar.setBackground(ENERGY_BASE);
        energyBar.setOverlay(new TextTexture("0 FE"));
        return energyBar;
    }

    @Override
    public void bindXEIWidget(Widget widget, Content content, IngredientIO ingredientIO) {
        if (widget instanceof ProgressWidget energyBar) {
            //TODO: use suffixes for large numbers
            var energy = EnergyFormattingUtil.formatExtended(of(content.content));
            if (energyBar.getOverlay() instanceof TextTexture textTexture) {
                if (content.perTick) {
                    textTexture.updateText(energy + "FE/t");
                } else {
                    textTexture.updateText(energy + "FE");
                }
            }
        }
    }

    @Override
    public void createContentConfigurator(ConfiguratorGroup father, Supplier<Long> supplier, Consumer<Long> onUpdate) {
        father.addConfigurators(new NumberConfigurator("recipe.capability.fluxnetworks_energy.energy", supplier::get,
                number -> onUpdate.accept(number.longValue()), 1, true).setRange(1, Long.MAX_VALUE));
    }

    @Override
    public Component getLeftErrorInfo(List<Long> left) {
        return Component.literal(left.stream().mapToLong(Long::longValue).sum() + " FE");
    }
}
