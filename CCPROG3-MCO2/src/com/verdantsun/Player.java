package com.verdantsun;

public class Player {

    private String name;
    private int savings;

    public Player(String name) {
        this.name = name;
        this.savings = 1000;
    }

    public void addSavings(int amount) {

        this.savings += amount;
    }

    public void deductSavings(int amount) {
        if (this.savings >= amount) {
            this.savings -=amount;
        }
    }

    public boolean canAfford(int amount) {

        return this.savings >= amount;
    }

    public int getSavings() {
        return this.savings;
    }

    public String getName() {

        return this.name;
    }
}
