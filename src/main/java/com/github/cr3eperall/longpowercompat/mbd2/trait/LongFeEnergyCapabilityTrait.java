package com.github.cr3eperall.longpowercompat.mbd2.trait;

import com.github.cr3eperall.longpowercompat.LongPowerCapabilities;
import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import com.github.cr3eperall.longpowercompat.mbd2.LongFeRecipeCapability;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.mbd2.api.capability.recipe.IO;
import com.lowdragmc.mbd2.api.capability.recipe.IRecipeHandlerTrait;
import com.lowdragmc.mbd2.api.recipe.MBDRecipe;
import com.lowdragmc.mbd2.common.machine.MBDMachine;
import com.lowdragmc.mbd2.common.trait.*;
import com.lowdragmc.mbd2.common.trait.forgeenergy.EnergyStorageList;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@Getter
public class LongFeEnergyCapabilityTrait extends SimpleCapabilityTrait implements IAutoIOTrait {
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(LongFeEnergyCapabilityTrait.class);
    @Override
    public ManagedFieldHolder getFieldHolder() { return MANAGED_FIELD_HOLDER; }

    @Persisted
    @DescSynced
    public final CopiableLFeEnergyStorage storage;
    private final LongFeRecipeHandler recipeHandler=new LongFeRecipeHandler();
    private final LongFeStorageCap longEnergyStorageCap = new LongFeStorageCap();
    private final FeStorageCap feEnergyStorageCap = new FeStorageCap();

    public LongFeEnergyCapabilityTrait(MBDMachine machine, LongFeEnergyCapabilityTraitDefinition definition) {
        super(machine, definition);
        storage = createStorages();
        storage.setOnContentsChanged(this::notifyListeners);
    }

    @Override
    public LongFeEnergyCapabilityTraitDefinition getDefinition() {
        return (LongFeEnergyCapabilityTraitDefinition) super.getDefinition();
    }

    @Override
    public void onLoadingTraitInPreview() {
        storage.receiveEnergyL(getDefinition().getCapacity()/2, false);
    }

    protected CopiableLFeEnergyStorage createStorages(){
        return new CopiableLFeEnergyStorage(getDefinition().getCapacity());
    }

    @Override
    public List<IRecipeHandlerTrait<?>> getRecipeHandlerTraits() {
        return List.of(recipeHandler);
    }

    @Override
    public List<ICapabilityProviderTrait<?>> getCapabilityProviderTraits() {
        return List.of(longEnergyStorageCap, feEnergyStorageCap);
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
                    Optional<ILongFeStorage> cap;
                    if ((cap = be.getCapability(LongPowerCapabilities.LONG_FE_STORAGE, side.getOpposite()).resolve()).isPresent()) {
                        return cap;
                    }else {
                        return be.getCapability(ForgeCapabilities.ENERGY, side.getOpposite()).resolve();
                    }
                })
                .ifPresent(source -> {
                    if (source instanceof ILongFeStorage lFeSource) {
                        lFeSource.extractEnergyL(
                                storage.receiveEnergyL(lFeSource.extractEnergyL(getDefinition().getMaxReceive(), true),
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
                    Optional<ILongFeStorage> cap;
                    if ((cap = be.getCapability(LongPowerCapabilities.LONG_FE_STORAGE, side.getOpposite()).resolve()).isPresent()) {
                        return cap;
                    }else {
                        return be.getCapability(ForgeCapabilities.ENERGY, side.getOpposite()).resolve();
                    }
                })
                .ifPresent( target -> {
                    if (target instanceof ILongFeStorage lFeTarget) {
                        lFeTarget.receiveEnergyL(
                                storage.extractEnergyL(lFeTarget.receiveEnergyL(getDefinition().getMaxExtract(), true),
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

    public class LongFeRecipeHandler extends RecipeHandlerTrait<Long> {
        protected LongFeRecipeHandler() {
            super(LongFeEnergyCapabilityTrait.this, LongFeRecipeCapability.CAP);
        }

        //TODO: check if overflow is possible
        @Override
        public List<Long> handleRecipeInner(IO io, MBDRecipe recipe, List<Long> left, @Nullable String slotName, boolean simulate) {
            if (!compatibleWith(io)) return left;
            long required = left.stream().reduce(0L, Long::sum);
            CopiableLFeEnergyStorage capability = simulate ? storage.copy() : storage;
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
    public class LongFeStorageCap implements ICapabilityProviderTrait<ILongFeStorage> {

        @Override
        public IO getCapabilityIO(@Nullable Direction side) {
            return LongFeEnergyCapabilityTrait.this.getCapabilityIO(side);
        }

        @Override
        public Capability<ILongFeStorage> getCapability() {
            return LongPowerCapabilities.LONG_FE_STORAGE;
        }

        @Override
        public ILongFeStorage getCapContent(IO io) {
            return new LFeEnergyStorageWrapper(storage, io, getDefinition().getMaxReceive(), getDefinition().getMaxExtract());
        }

        @Override
        public ILongFeStorage mergeContents(List<ILongFeStorage> contents) {
            return new LFeEnergyStorageList(contents.toArray(new ILongFeStorage[0]));
        }
    }

    public class FeStorageCap implements ICapabilityProviderTrait<IEnergyStorage> {

        @Override
        public IO getCapabilityIO(@Nullable Direction side) {
            return LongFeEnergyCapabilityTrait.this.getCapabilityIO(side);
        }

        @Override
        public Capability<IEnergyStorage> getCapability() {
            return ForgeCapabilities.ENERGY;
        }

        @Override
        public IEnergyStorage getCapContent(IO io) {
            return new LFeEnergyStorageWrapper(storage, io, getDefinition().getMaxReceive(), getDefinition().getMaxExtract());
        }

        @Override
        public IEnergyStorage mergeContents(List<IEnergyStorage> contents) {
            if(contents.stream().allMatch((s)->s instanceof ILongFeStorage)) {
                List<ILongFeStorage> longContents = contents.stream().map(s->(ILongFeStorage)s).toList();
                return new LFeEnergyStorageList(longContents.toArray(new ILongFeStorage[0]));
            }else{
                return new EnergyStorageList(contents.toArray(new IEnergyStorage[0]));
            }
        }
    }
}
