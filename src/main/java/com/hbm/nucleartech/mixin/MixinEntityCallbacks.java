package com.hbm.nucleartech.mixin;

import com.hbm.nucleartech.entity.effects.NukeTorexEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hbm.nucleartech.entity.effects.NukeTorexEntity.SHOULD_DIE;

@Mixin(targets = "net.minecraft.server.level.ServerLevel$EntityCallbacks")
public abstract class MixinEntityCallbacks {

    @Inject(method = "onTickingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void onTickingEnd(Entity entity, CallbackInfo ci) {

        if(entity instanceof NukeTorexEntity torex && !torex.getEntityData().get(SHOULD_DIE))
            ci.cancel();
    }
}
