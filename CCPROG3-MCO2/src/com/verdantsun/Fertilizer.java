package com.verdantsun;

public class Fertilizer {

    private String name;
    private int price;
    private int effectDays;
    private int maxEffectDays;

    public Fertilizer(String name, int price, int effectDays) {
        this.name = name;
        this.price = price;
        this.effectDays = effectDays;
        this.maxEffectDays = effectDays;
    }

    public void decreaseEffect() {
        if (this.effectDays > 0) {
            this.effectDays--;
        }
    }

    public boolean isExpired() {
        return this.effectDays <= 0;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public int getEffectDays() {
        return this.effectDays;
    }

    public int getMaxEffectDays() {
        return this.maxEffectDays;
    }
}
