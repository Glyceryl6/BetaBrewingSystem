package com.glyceryl6.cauldron.potion;

@SuppressWarnings("unused")
public class PotionCauldron {

    public static final PotionCauldron[] potionTypes = new PotionCauldron[32];
    public static final PotionCauldron moveSpeed = (new PotionCauldron(1)).setPotionName("potion.moveSpeed").setIconIndex(0, 0);
    public static final PotionCauldron moveSlowdown = (new PotionCauldron(2)).setPotionUsable().setPotionName("potion.moveSlowdown").setIconIndex(1, 0);
    public static final PotionCauldron digSpeed = (new PotionCauldron(3)).setPotionName("potion.digSpeed").setIconIndex(2, 0);
    public static final PotionCauldron digSlowdown = (new PotionCauldron(4)).setPotionUsable().setPotionName("potion.digSlowDown").setIconIndex(3, 0);
    public static final PotionCauldron damageBoost = (new PotionCauldron(5)).setPotionName("potion.damageBoost").setIconIndex(4, 0);
    public static final PotionCauldron jump = (new PotionCauldron(8)).setPotionName("potion.jump").setIconIndex(2, 1);
    public static final PotionCauldron confusion = (new PotionCauldron(9)).setPotionUsable().setPotionName("potion.confusion").setIconIndex(3, 1);
    public static final PotionCauldron regeneration = (new PotionCauldron(10)).setPotionName("potion.regeneration").setIconIndex(7, 0);
    public static final PotionCauldron resistance = (new PotionCauldron(11)).setPotionName("potion.resistance").setIconIndex(6, 1);
    public static final PotionCauldron fireResistance = (new PotionCauldron(12)).setPotionName("potion.fireResistance").setIconIndex(7, 1);
    public static final PotionCauldron waterBreathing = (new PotionCauldron(13)).setPotionName("potion.waterBreathing").setIconIndex(0, 2);
    public static final PotionCauldron invisibility = (new PotionCauldron(14)).setPotionName("potion.invisibility").setIconIndex(0, 1);
    public static final PotionCauldron blindness = (new PotionCauldron(15)).setPotionUsable().setPotionName("potion.blindness").setIconIndex(5, 1);
    public static final PotionCauldron nightVision = (new PotionCauldron(16)).setPotionName("potion.nightVision").setIconIndex(4, 1);
    public static final PotionCauldron hunger = (new PotionCauldron(17)).setPotionUsable().setPotionName("potion.hunger").setIconIndex(1, 1);
    public static final PotionCauldron weakness = (new PotionCauldron(18)).setPotionUsable().setPotionName("potion.weakness").setIconIndex(5, 0);
    public static final PotionCauldron poison = (new PotionCauldron(19)).setPotionUsable().setPotionName("potion.poison").setIconIndex(6, 0);

    public final int id;
    private boolean usable;
    private String name = "";
    private int statusIconIndex = -1;

    protected PotionCauldron(int paramInt) {
        this.id = paramInt;
        potionTypes[paramInt] = this;
    }

    protected PotionCauldron setIconIndex(int paramInt1, int paramInt2) {
        this.statusIconIndex = paramInt1 + paramInt2 * 8;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public boolean isInstant() {
        return false;
    }

    public PotionCauldron setPotionName(String paramString) {
        this.name = paramString;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasStatusIcon() {
        return (this.statusIconIndex >= 0);
    }

    public int getStatusIconIndex() {
        return this.statusIconIndex;
    }

    public boolean isUsable() {
        return this.usable;
    }

    protected PotionCauldron setPotionUsable() {
        this.usable = true;
        return this;
    }

}