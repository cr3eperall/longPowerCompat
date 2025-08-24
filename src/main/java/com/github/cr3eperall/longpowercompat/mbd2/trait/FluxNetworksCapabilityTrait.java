package com.github.cr3eperall.longpowercompat.mbd2.trait;

import com.github.cr3eperall.longpowercompat.LongUtils;
import com.github.cr3eperall.longpowercompat.mbd2.FluxNetworksRecipeCapability;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.mbd2.api.capability.recipe.IO;
import com.lowdragmc.mbd2.api.capability.recipe.IRecipeHandlerTrait;
import com.lowdragmc.mbd2.api.recipe.MBDRecipe;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.trait.*;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

import java.util.List;
import java.util.Optional;

@Getter
public class FluxNetworksCapabilityTrait extends SimpleCapabilityTrait implements IAutoIOTrait {
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(FluxNetworksCapabilityTrait.class);
    @Override
    public ManagedFieldHolder getFieldHolder() { return MANAGED_FIELD_HOLDER; }

    @Persisted
    @DescSynced
    public final CopiableFNEnergyStorage storage;
    private final FluxNetworksRecipeHandler recipeHandler=new FluxNetworksRecipeHandler();
    private final FluxNetworksStorageCap energyStorageCap = new FluxNetworksStorageCap();

    public FluxNetworksCapabilityTrait(MBDMachine machine, FluxNetworksCapabilityTraitDefinition definition) {
        super(machine, definition);
        storage = createStorages();
        storage.setOnContentsChanged(this::notifyListeners);
    }

    @Override
    public FluxNetworksCapabilityTraitDefinition getDefinition() {
        return (FluxNetworksCapabilityTraitDefinition) super.getDefinition();
    }

    @Override
    public void onLoadingTraitInPreview() {
        storage.receiveEnergyL(getDefinition().getCapacity()/2, false);
    }

    protected CopiableFNEnergyStorage createStorages(){
        return new CopiableFNEnergyStorage(getDefinition().getCapacity());
    }

    @Override
    public List<IRecipeHandlerTrait<?>> getRecipeHandlerTraits() {
        return List.of(recipeHandler);
    }

    @Override
    public List<ICapabilityProviderTrait<?>> getCapabilityProviderTraits() {
        return List.of(energyStorageCap);
    }

    @Override
    public @Nullable AutoIO getAutoIO() {
        return getDefinition().getAutoIO().isEnable() ? getDefinition().getAutoIO() : null;
    }

    @Override
    public void handleAutoIO(BlockPos port, Direction side, IO io) {
        if (io.support(IO.IN)) {
            Optional.ofNullable(getMachine().getLevel().getBlockEntity(port.relative(side)))
                .flatMap(be -> {
                    Optional<IFNEnergyStorage> cap;
                    if ((cap = be.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side.getOpposite()).resolve()).isPresent()) {
                        return cap;
                    }else {
                        return be.getCapability(ForgeCapabilities.ENERGY, side.getOpposite()).resolve();
                    }
                })
                .ifPresent(source -> {
                    if (source instanceof IFNEnergyStorage fnSource) {
                        fnSource.extractEnergyL(
                                storage.receiveEnergyL(fnSource.extractEnergyL(getDefinition().getMaxReceive(), true),
                                        false),
                                false);
                    } else if (source instanceof net.minecraftforge.energy.IEnergyStorage feSource) {
                        feSource.extractEnergy(
                                storage.receiveEnergy(Math.min((int)Math.min(getDefinition().getMaxReceive(), Integer.MAX_VALUE), feSource.extractEnergy((int)Math.min(getDefinition().getMaxReceive(), Integer.MAX_VALUE), true)), false),
                                false);
                    }

                }
            );
        }
        if (io.support(IO.OUT)){
            Optional.ofNullable(getMachine().getLevel().getBlockEntity(port.relative(side)))
                .flatMap(be -> {
                    Optional<IFNEnergyStorage> cap;
                    if ((cap = be.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side.getOpposite()).resolve()).isPresent()) {
                        return cap;
                    }else {
                        return be.getCapability(ForgeCapabilities.ENERGY, side.getOpposite()).resolve();
                    }
                })
                .ifPresent( target -> {
                    if (target instanceof IFNEnergyStorage fnTarget) {
                        fnTarget.receiveEnergyL(
                                storage.extractEnergyL(fnTarget.receiveEnergyL(getDefinition().getMaxExtract(), true),
                                        false),
                                false);
                    } else if (target instanceof net.minecraftforge.energy.IEnergyStorage feTarget) {
                        feTarget.receiveEnergy(
                                storage.extractEnergy(Math.min((int)Math.min(getDefinition().getMaxExtract(), Integer.MAX_VALUE), feTarget.receiveEnergy((int)Math.min(getDefinition().getMaxExtract(), Integer.MAX_VALUE), true)), false),
                                false
                        );
                    }
                }
            );
        }
    }

    public class FluxNetworksRecipeHandler extends RecipeHandlerTrait<Long> {
        protected FluxNetworksRecipeHandler() {
            super(FluxNetworksCapabilityTrait.this, FluxNetworksRecipeCapability.CAP);
        }

        //TODO: check if overflow is possible
        @Override
        public List<Long> handleRecipeInner(IO io, MBDRecipe recipe, List<Long> left, @Nullable String slotName, boolean simulate) {
            if (!compatibleWith(io)) return left;
            long required = left.stream().reduce(0L, Long::sum);
            CopiableFNEnergyStorage capability = simulate ? storage.copy() : storage;
            if (io == IO.IN) {
                long extracted = capability.extractEnergyL(required, simulate);
                required-=extracted;
            } else {
                long received = capability.receiveEnergyL(required, simulate);
                required-=received;
            }
            return required>0 ? List.of(required) : null;
        }
    }

    //TODO: check if Forge energy also works with this
    public class FluxNetworksStorageCap implements ICapabilityProviderTrait<IFNEnergyStorage> {

        @Override
        public IO getCapabilityIO(@Nullable Direction side) {
            return FluxNetworksCapabilityTrait.this.getCapabilityIO(side);
        }

        @Override
        public Capability<IFNEnergyStorage> getCapability() {
            return FluxCapabilities.FN_ENERGY_STORAGE;
        }

        @Override
        public IFNEnergyStorage getCapContent(IO io) {
            return new FNEnergyStorageWrapper(storage, io, getDefinition().getMaxReceive(), getDefinition().getMaxExtract());
        }

        @Override
        public IFNEnergyStorage mergeContents(List<IFNEnergyStorage> contents) {
            return new FNEnergyStorageList(contents.toArray(new IFNEnergyStorage[0]));
        }
    }
}
