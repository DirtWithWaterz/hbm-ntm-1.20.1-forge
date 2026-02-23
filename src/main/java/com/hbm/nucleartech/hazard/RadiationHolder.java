package com.hbm.nucleartech.hazard;

import com.hbm.nucleartech.util.ContaminationUtil;
import net.minecraft.nbt.CompoundTag;

public class RadiationHolder {

    public final double alpha, beta, xray, gamma, neutron;

    public final boolean nyll;

    public RadiationHolder() {

        this(0, 0, 0, 0, 0, true);
    }
    public RadiationHolder(double alpha, double beta, double xray, double gamma, double neutron) {

        this(alpha, beta, xray, gamma, neutron, false);
    }
    public RadiationHolder(double alpha, double beta, double xray, double gamma, double neutron, boolean nyll) {

        this.alpha = alpha;
        this.beta = beta;
        this.xray = xray;
        this.gamma = gamma;
        this.neutron = neutron;

        this.nyll = nyll;
    }

    public float penning() {

        return (float)(this.xray + this.gamma + this.neutron);
    }

    public RadiationHolder add(RadiationHolder other) {

        return new RadiationHolder(
                this.alpha + other.alpha,
                this.beta + other.beta,
                this.xray + other.xray,
                this.gamma + other.gamma,
                this.neutron + other.neutron
        );
    }

    public double get(ContaminationUtil.radMeV MeV) {

        return switch(MeV) {
            case ALPHA -> this.alpha;
            case BETA -> this.beta;
            case XRAY -> this.xray;
            case GAMMA -> this.gamma;
            case NEUTRON -> this.neutron;
            case PENNING -> this.gamma+this.xray+this.neutron;
        };
    }

    public double sum() {

        return this.alpha +
                this.beta +
                this.xray +
                this.gamma +
                this.neutron;
    }

    public CompoundTag serializeNBT() {

        CompoundTag tag = new CompoundTag();

        tag.putDouble("alpha", alpha);
        tag.putDouble("beta", alpha);
        tag.putDouble("xray", alpha);
        tag.putDouble("gamma", alpha);
        tag.putDouble("neutron", alpha);
        tag.putBoolean("nyll", nyll);

        return tag;
    }

    public static RadiationHolder deserializeNBT(CompoundTag tag) {

        return new RadiationHolder(
                tag.getDouble("alpha"),
                tag.getDouble("beta"),
                tag.getDouble("xray"),
                tag.getDouble("gamma"),
                tag.getDouble("neutron"),
                tag.getBoolean("nyll")
        );
    }
}
