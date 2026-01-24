package com.hbm.nucleartech.mixin;

import com.hbm.nucleartech.entity.effects.NukeTorexEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

import static com.hbm.nucleartech.entity.effects.NukeTorexEntity.SHOULD_DIE;

@Mixin(ServerLevel.class)
public class MixinServerLevel {

    @Shadow
    @Final
    EntityTickList entityTickList;

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At("HEAD"))
    private void forceTickNukeTorex(BooleanSupplier booleanSupplier, CallbackInfo ci) {

//        System.err.println("[Mixin] tick mixin called.");

        ServerLevel level = ((ServerLevel)(Object)this);

        this.entityTickList.forEach((p_184065_) -> {

//            System.out.println("[Mixin] entity: " + p_184065_.getName().getString());

            if(p_184065_ instanceof NukeTorexEntity torex && !torex.getEntityData().get(SHOULD_DIE)) {

//                System.err.println("[Mixin] torex found! ticking...");
                level.guardEntityTick(level::tickNonPassenger, p_184065_);
            }
        });
    }

    @Inject(method = "tickNonPassenger(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void forceTickNukeTorex(Entity entity, CallbackInfo ci) {
        if (entity instanceof NukeTorexEntity torex && !torex.getEntityData().get(SHOULD_DIE)) {
            // Force tick even if outside simulation distance
            entity.tick();
            torex.tick();
            ci.cancel(); // Skip normal tick to avoid duplicates/conflicts; adjust if needed
        }
    }
}