package com.hbm.nucleartech.mixin;

import com.hbm.nucleartech.entity.effects.NukeTorexEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static com.hbm.nucleartech.entity.effects.NukeTorexEntity.SHOULD_DIE;

@Mixin(Level.class)
public abstract class MixinLevel {

    @Inject(method = "guardEntityTick(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void forceTickNukeTorex(Consumer<Entity> tickConsumer, Entity entity, CallbackInfo ci) {
//        System.out.println("[Mixin] guardEntityTick mixin.");
        if (entity instanceof NukeTorexEntity torex && !torex.getEntityData().get(SHOULD_DIE))
            if(!((Level)(Object)this).isClientSide) // Server-side only
                tickConsumer.accept(entity); // Ensure it's ticked by calling the consumer
    }
}