package com.hbm.nucleartech.mixin;

import com.hbm.nucleartech.entity.effects.NukeTorexEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hbm.nucleartech.entity.effects.NukeTorexEntity.SHOULD_DIE;


@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void remove(Entity.RemovalReason pReason, CallbackInfo ci) {

        Entity entity = ((Entity)(Object)this);

        if(entity instanceof NukeTorexEntity torex && !torex.getEntityData().get(SHOULD_DIE))
            ci.cancel();
    }

    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    private void kill(CallbackInfo ci) {

        Entity entity = ((Entity)(Object)this);

        if(entity instanceof NukeTorexEntity torex && !torex.getEntityData().get(SHOULD_DIE))
            ci.cancel();
    }
}
