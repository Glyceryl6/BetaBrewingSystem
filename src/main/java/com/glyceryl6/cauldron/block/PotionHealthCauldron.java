package com.glyceryl6.cauldron.block;

public class PotionHealthCauldron extends PotionCauldron {

    public static final PotionCauldron heal = (new PotionHealthCauldron(6)).setPotionName("potion.heal");
    public static final PotionCauldron harm = (new PotionHealthCauldron(7)).setPotionUsable().setPotionName("potion.harm");

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
