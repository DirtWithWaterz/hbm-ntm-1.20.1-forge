package com.hbm.nucleartech.modules;

import com.hbm.nucleartech.Config;
import com.hbm.nucleartech.explosion.VeryFastRaycastedExplosion;
import com.hbm.nucleartech.hazard.HazardItem;
import com.hbm.nucleartech.interfaces.IItemHazard;
import com.hbm.nucleartech.item.RegisterItems;
import com.hbm.nucleartech.lib.Library;
import com.hbm.nucleartech.util.ArmorRegistry;
import com.hbm.nucleartech.util.ArmorRegistry.HazardClass;
import com.hbm.nucleartech.util.ArmorUtil;
import com.hbm.nucleartech.util.ContaminationUtil;
import com.hbm.nucleartech.util.ContaminationUtil.HazardType;
import com.hbm.nucleartech.util.ContaminationUtil.ContaminationType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class ItemHazardModule {

    private static DecimalFormat df = new DecimalFormat("0.##");

    public double radiation; // should no longer be visible

    public double alpha;
    public double beta;
    public double gamma;
    public double neutron;
    public double xray;

    public double digamma;
    public int fire;
    public int cryogenic;
    public int toxic;
    public boolean blinding;
    public int asbestos;
    public int coal;
    public boolean hydro;
    public float explosive;

    public int uExpRadius;

    public float halfLife;

    public boolean isIsotope;
    public double gangue;
    public float originalMass;

    public float tempMod = 1f;

    public void setMod(float tempMod) {

        this.tempMod = tempMod;
    }

    public boolean isRadioactive() {

        return this.radiation > 0;
    }

    public boolean isIsotope() {

        return this.isIsotope;
    }

    public void addMass(float mass) {

        mass *= Config.molesPerIngotConstant;

        this.originalMass = mass;
    }

    public void addGangue(double gangue) {

        gangue *= Config.molesPerIngotConstant;

        this.gangue = gangue;
    }

    public void addRadiation(double radiation) {

        this.radiation = radiation;
    }

    public void addDigamma(double digamma) {

        this.digamma = digamma;
    }

    public void addFire(int fire) {

        this.fire = fire;
    }

    public void addCryogenic(int cryogenic) {

        this.cryogenic = cryogenic;
    }

    public void addToxic(int toxiclvl) {

        this.toxic = toxiclvl;
    }

    public void addCoal(int coal) {

        this.coal = coal;
    }

    public void addAsbestos(int asbestos) {

        this.asbestos = asbestos;
    }

    public void addBlinding() {

        this.blinding = true;
    }

    public void addHydroReactivity() {

        this.hydro = true;
    }

    public void addExplosive(float bang) {

        this.explosive = bang;
    }

    public boolean isInitialized(ItemStack stack) {

        return stack.getOrCreateTag().getBoolean("initialized");
    }

    public void update(@Nullable ItemStack stack, LivingEntity entity, float mod, int slot, boolean currentItem, InteractionHand hand) {

        boolean reacher = false;

        if(entity instanceof Player player /* && !GeneralConfig.enable528 */) {

//            if(player.isCreative() || player.isSpectator())
//                return;

            reacher = Library.checkForHeld(player, RegisterItems.REACHER.get());
        }

        if(stack != null)
            stack.getOrCreateTag().putLong("time_elapsed", System.currentTimeMillis() - stack.getOrCreateTag().getLong("start_time"));

        if(this.radiation * tempMod > 0) {

            double rad = this.radiation * tempMod * mod / 20f;

//            System.out.println("[Debug] reacher: " + reacher);
//            System.out.println("[Debug] og rad: " + rad);

            if(reacher)
                rad = (double) Math.min(Math.sqrt(rad), rad);

//            System.out.println("[Debug] new rad: " + rad);

//            System.err.println("calling ContaminationUtil.contaminate() for " + entity.getName().getString() + " with rad value: " + rad);
            ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, (float) rad);
        }
        if(this.fire * tempMod > 0) {

            double fire = this.fire * tempMod * mod;

            if(reacher)
                fire = 0;

            entity.setSecondsOnFire(Math.round(Math.round(fire)));
        }
        if(this.toxic * tempMod > 0) {

            boolean hasToxFilter = false;
            boolean hasHazmat = false;
            if(entity instanceof Player player) {

                if(ArmorRegistry.hasProtection(player, EquipmentSlot.HEAD, HazardClass.NERVE_AGENT)) {

                    ArmorUtil.damageGasMaskFilter(player, 1);
                    hasToxFilter = true;
                }
                hasHazmat = ArmorUtil.checkForHazmat(entity);
            }

            if(!hasToxFilter && !hasHazmat) {

                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 110, this.toxic-1, true, false));

                if(this.toxic > 2)
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 110, Math.min(4, this.toxic-1), true, false));
                if(this.toxic > 4)
                    entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 110, this.toxic, true, false));
                if(this.toxic > 6)
                    if(entity.level().random.nextInt((int)(2000/toxic)) == 0)
                        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 110, this.toxic-4, true, false));
            }
            if(!(hasHazmat && hasToxFilter)) {

                if(this.toxic > 8)
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 110, this.toxic-6, true, false));
                if(this.toxic > 16)
                    entity.addEffect(new MobEffectInstance(MobEffects.HARM, 110, this.toxic-16, true, false));
            }
        }
        if(this.coal * tempMod > 0) {

            double blacklung = this.coal * tempMod * mod;

            ContaminationUtil.contaminate(entity, HazardType.COAL, ContaminationType.CREATIVE, (float) blacklung);
        }
        if(this.asbestos * tempMod > 0) {

            double meso = this.asbestos * tempMod * mod;

            ContaminationUtil.contaminate(entity, HazardType.ASBESTOS, ContaminationType.CREATIVE, (float) meso);
        }
        if(stack != null) {

            if(isInitialized(stack)) {

                stack.getOrCreateTag().putFloat("mass", recalculateMass(this.originalMass, getTimeElapsed(stack), this.halfLife));
                if(halfLivesPassed(getTimeElapsed(stack), this.halfLife) > 10)
                    explode(stack, entity);
            }
            else
                stack.getOrCreateTag().putFloat("mass", this.originalMass);
        }
    }

    private static long getTimeElapsed(ItemStack stack) {

        return stack.getOrCreateTag().getLong("time_elapsed");
    }

    private static float recalculateMass(float originalMass, long timeElapsed, float halfLife) {

        return ((Double)(originalMass * Math.pow(0.5, halfLivesPassed(timeElapsed, halfLife)))).floatValue();
    }

    public static float halfLivesPassed(long timeElapsed, float halfLife) {

        return timeElapsed / halfLife;
    }

    private Component getDecay(ItemStack stack) {

        return Component.literal(df.format(((this.originalMass - getMass(stack)) / this.originalMass) * 100) + "%");
    }

    private static float getMass(ItemStack stack) {

        float malalassasssssSAASSSSSSFUUUUUCCKKKKKK = stack.getOrCreateTag().getFloat("mass");

        return malalassasssssSAASSSSSSFUUUUUCCKKKKKK > 0 ? malalassasssssSAASSSSSSFUUUUUCCKKKKKK : ((IItemHazard)stack.getItem()).getModule().originalMass;
    }

    private void explode(ItemStack stack, Entity source) {

        if(source instanceof Player player)
            player.getInventory().removeItem(stack);
        if(source instanceof ItemEntity item)
            item.remove(RemovalReason.DISCARDED);
        new VeryFastRaycastedExplosion(source.level(), source.getX(), source.getY(), source.getZ(), Math.round(this.uExpRadius / 3f), this.uExpRadius, this.uExpRadius, Math.round(this.uExpRadius*1.5f), (byte)0, 1, 2, 1, null, this.uExpRadius, true);
    }

    public static double getNewValue(double radiation) {
        
        if(radiation < 1000000){
            return radiation;
        } else if (radiation < 1000000000) {
            return radiation * 0.000001;
        }
        else {
            return radiation * 0.000000001;
        }
    }

    public static String getSuffix(double radiation) {

        if(radiation < 1000000){
            return "";
        } else if(radiation < 1000000000){
            return "M";
        } else{
            return "B";
        }
    }

    public static String getMassFormatted(ItemStack stack) {

        float mass = getMass(stack);

        return mass > 1000 ? df.format(mass / 1000) + "kg" : mass < 1 ? Math.round(mass * 1000) + "mg" : df.format(mass) + "g";
    }

    public void addInformation(ItemStack stack, List<Component> list, TooltipFlag flagIn) {

        stack.getOrCreateTag().putFloat("mass", getMass(stack));

        // Mass
        if(getMass(stack) > 0) {

            list.add(Component.literal("Mass: ").append(getMassFormatted(stack)).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        }
        // Rad
        if(this.radiation * tempMod > 0) {

            list.add(Component.literal("§a[Radioactive]"));
            double itemRad = radiation * tempMod;
            list.add(Component.literal("§e" + (Library.roundDouble(getNewValue(itemRad), 3) + getSuffix(itemRad) + " RAD/s")));

            if(stack.getCount() > 1) {

                double stackRad = radiation * tempMod * stack.getCount();
                list.add(Component.literal("§eStack: " + Library.roundDouble(getNewValue(stackRad), 3) + getSuffix(stackRad) + " RAD/s"));
            }
        }
        // Pyro

        // Toxic
        if(this.toxic * tempMod > 0) {

            if(this.toxic > 16)
                list.add(Component.literal("[").append(Component.translatable("adjective.extreme")).append(" ").append(Component.translatable("trait.toxic")).append("]").withStyle(ChatFormatting.GREEN));
            else if(this.toxic > 8)
                list.add(Component.literal("[").append(Component.translatable("adjective.very_high")).append(" ").append(Component.translatable("trait.toxic")).append("]").withStyle(ChatFormatting.GREEN));
            else if(this.toxic > 4)
                list.add(Component.literal("[").append(Component.translatable("adjective.high")).append(" ").append(Component.translatable("trait.toxic")).append("]").withStyle(ChatFormatting.GREEN));
            else if(this.toxic > 2)
                list.add(Component.literal("[").append(Component.translatable("adjective.medium")).append(" ").append(Component.translatable("trait.toxic")).append("]").withStyle(ChatFormatting.GREEN));
            else
                list.add(Component.literal("[").append(Component.translatable("adjective.low")).append(" ").append(Component.translatable("trait.toxic")).append("]").withStyle(ChatFormatting.GREEN));
        }
        // Blinding

        // Hydro

        // Asbestos
        if(this.asbestos * tempMod > 0) {

            list.add(Component.literal("[Asbestos]").withStyle(ChatFormatting.WHITE));
        }
        // Coal Dust
        if(this.coal * tempMod > 0) {

            list.add(Component.literal("[Coal Dust]").withStyle(ChatFormatting.DARK_GRAY));
        }
        // Unstable
        if(this.uExpRadius > 0) {

            list.add(Component.literal("[Unstable]").withStyle(ChatFormatting.DARK_RED));
            list.add(Component.literal("  -:").append(Component.translatable("trait.halflife")).append(df.format(halfLife / 1000) + "s").withStyle(ChatFormatting.RED));
            list.add(Component.literal("  -:").append(Component.translatable("trait.decay")).append(getDecay(stack)).withStyle(ChatFormatting.RED));
        }
    }

    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity item) {

        if(!item.level().isClientSide) {

            stack.getOrCreateTag().putLong("time_elapsed", System.currentTimeMillis() - stack.getOrCreateTag().getLong("start_time"));

            if(this.hydro && (item.isInWaterOrRain())) {

                item.remove(RemovalReason.KILLED);
                item.level().explode(item, item.position().x, item.position().y, item.position().z, 2f, Level.ExplosionInteraction.TNT);
                return true;
            }

            if(this.explosive > 0 && item.isOnFire()) {

                item.remove(RemovalReason.KILLED);
                item.level().explode(item, item.position().x, item.position().y, item.position().z, this.explosive, Level.ExplosionInteraction.TNT);
                return true;
            }

            if(this.isRadioactive()) {

//                System.err.println("[Debug] radiating...");
                ContaminationUtil.radiate((ServerLevel) item.level(), item.getOnPos().getX(), item.getOnPos().getY()+1, item.getOnPos().getZ(), 32, (float)(this.radiation*0.00004D-(0.00004D*20)), item.getOnPos().offset(0, 1, 0));
            }

            if(this.isInitialized(stack)) {

                stack.getOrCreateTag().putFloat("mass", recalculateMass(this.originalMass, getTimeElapsed(stack), this.halfLife));
                if(halfLivesPassed(getTimeElapsed(stack), this.halfLife) > 10)
                    explode(stack, item);
            }
            else
                stack.getOrCreateTag().putFloat("mass", this.originalMass);
        }

        return false;
    }
}
