package com.github.cr3eperall.longpowercompat;

import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class LongPowerCapabilities {
    public static final Capability<ILongFeStorage> LONG_FE_STORAGE = CapabilityManager.get(new CapabilityToken<>() {
    });
}
