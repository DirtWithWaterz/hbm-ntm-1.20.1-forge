package com.hbm.nucleartech.mixin;

import net.minecraft.util.BitStorage;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PalettedContainer.Data.class)
public interface MixinData<T> {

    @Accessor("storage")
    BitStorage getStorage();

    @Accessor("palette")
    Palette<T> getPalette();
}
