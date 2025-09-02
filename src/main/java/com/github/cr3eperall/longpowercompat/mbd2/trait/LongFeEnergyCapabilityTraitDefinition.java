package com.github.cr3eperall.longpowercompat.mbd2.trait;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.lowdragmc.lowdraglib.gui.editor.annotation.ConfigSetter;
import com.lowdragmc.lowdraglib.gui.editor.annotation.Configurable;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.annotation.NumberRange;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.ProgressWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextTextureWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.lowdragmc.mbd2.api.machine.IMachine;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.trait.ITrait;
import com.lowdragmc.mbd2.common.trait.SimpleCapabilityTrait;
import com.lowdragmc.mbd2.common.trait.SimpleCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.trait.ToggleAutoIO;
import com.lowdragmc.mbd2.utils.EnergyFormattingUtil;
import com.lowdragmc.mbd2.utils.WidgetUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;

import static com.github.cr3eperall.longpowercompat.mbd2.LongFeRecipeCapability.ENERGY_BAR;
import static com.github.cr3eperall.longpowercompat.mbd2.LongFeRecipeCapability.ENERGY_BASE;

@LDLRegister(name="longpower_energy", group="trait", modID="longpowercompat")
public class LongFeEnergyCapabilityTraitDefinition extends SimpleCapabilityTraitDefinition {
    @Getter
    @Setter
    @Configurable(name = "config.definition.trait.longpower_energy.capacity")
    @NumberRange(range = {1, Long.MAX_VALUE})
    private long capacity = 5000;
    @Getter
    @Setter
    @Configurable(name = "config.definition.trait.longpower_energy.max_receive", tips = "config.definition.trait.longpower_energy.max_receive.tooltip")
    @NumberRange(range = {0, Long.MAX_VALUE})
    private long maxReceive = 5000;
    @Getter
    @Setter
    @Configurable(name = "config.definition.trait.longpower_energy.max_extract", tips = "config.definition.trait.longpower_energy.max_extract.tooltip")
    @NumberRange(range = {0, Long.MAX_VALUE})
    private long maxExtract = 5000;
    @Getter
    @Configurable(name = "config.definition.trait.auto_io", subConfigurable = true, tips = "config.definition.trait.longpower_energy.auto_io.tooltip")
    private final ToggleAutoIO autoIO = new ToggleAutoIO();
    @Configurable(name = "config.definition.trait.longpower_energy.fancy_renderer", subConfigurable = true,
            tips = "config.definition.trait.longpower_energy.fancy_renderer.tooltip")
    private final LongFeEnergyFancyRendererSettings fancyRendererSettings = new LongFeEnergyFancyRendererSettings(this);

    @Override
    public SimpleCapabilityTrait createTrait(MBDMachine mbdMachine) {
        return new LongFeEnergyCapabilityTrait(mbdMachine, this);
    }

    @Override
    public IGuiTexture getIcon() {
        return new ResourceTexture("mbd2:textures/gui/forge_energy.png");
    }

    @Override
    public IRenderer getBESRenderer(IMachine machine) {
        return fancyRendererSettings.getFancyRenderer(machine);
    }

    @Override
    public void createTraitUITemplate(WidgetGroup ui) {
        var prefix = uiPrefixName();
        var energyBar = new ProgressWidget(ProgressWidget.JEIProgress, 0, 0, 100, 14, new ProgressTexture(
                IGuiTexture.EMPTY, ENERGY_BAR
        ));
        energyBar.setBackground(ENERGY_BASE);
        energyBar.setId(prefix);
        var energyBarText = new TextTextureWidget(5, 2, 90, 10)
                .setText("0/0 FE")
                .textureStyle(textTexture -> textTexture.setDropShadow(true));
        energyBarText.setId(prefix + "_text");
        ui.addWidget(energyBar);
        ui.addWidget(energyBarText);
    }

    @Override
    public void initTraitUI(ITrait trait, WidgetGroup group) {
        if (trait instanceof LongFeEnergyCapabilityTrait lFeEnergyTrait) {
            var prefix = uiPrefixName();
            WidgetUtils.widgetByIdForEach(group, "^%s$".formatted(prefix), ProgressWidget.class, energyBar -> {
                energyBar.setProgressSupplier(() -> lFeEnergyTrait.storage.getEnergyStoredL() * 1d / lFeEnergyTrait.storage.getMaxEnergyStoredL());
                energyBar.setDynamicHoverTips(value -> {
                    var stored = EnergyFormattingUtil.formatExtended(lFeEnergyTrait.storage.getEnergyStoredL());
                    var maxStored = EnergyFormattingUtil.formatExtended(lFeEnergyTrait.storage.getMaxEnergyStoredL());
                    return LocalizationUtils.format("config.definition.trait.longpower_energy.ui_container_hover", stored, maxStored);
                });
            });
            WidgetUtils.widgetByIdForEach(group, "^%s_text$".formatted(prefix), TextTextureWidget.class, energyBarText -> {
                energyBarText.setText(() -> {
                    var stored = EnergyFormattingUtil.formatCompact(lFeEnergyTrait.storage.getEnergyStoredL()) + "FE";
                    var maxStored = EnergyFormattingUtil.formatCompact(lFeEnergyTrait.storage.getMaxEnergyStoredL()) + "FE";
                    return Component.literal(stored + "/" + maxStored);
                });
            });
        }
    }
}
