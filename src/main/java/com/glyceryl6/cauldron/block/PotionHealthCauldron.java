package com.glyceryl6.cauldron.block;

public class PotionHealthCauldron extends PotionCauldron {

    public PotionHealthCauldron(int paramInt) {
        super(paramInt);
    }

    public boolean isInstant() {
        return true;
    }

    public boolean isReady(int paramInt1, int paramInt2) {
        return (paramInt1 >= 1);
    }

}
