package me.zeroX150.cornos.etc.config;

import net.minecraft.util.math.MathHelper;

public class MConfNum extends MConf.ConfigKey {
    public double max;
    public double min;

    public MConfNum(String k, double v, double max, double min) {
        super(k, v + "", "No description");
        this.max = max;
        this.min = min;
    }

    public MConfNum(String k, double v, double max, double min, String description) {
        this(k, v, max, min);
        this.description = description;
    }

    public double getValue() {
        return Double.parseDouble(this.value);
    }

    @Override
    public void setValue(String newV) {
        try {
            double d = Double.parseDouble(newV);
            d = MathHelper.clamp(d, min, max);
            super.setValue(d + "");
        } catch (Exception ignored) {
        }
    }
}
