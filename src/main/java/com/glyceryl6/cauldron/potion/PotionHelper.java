package com.glyceryl6.cauldron.potion;

import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PotionHelper {

    private static final HashMap<Object, Object> potionRequirements = new HashMap<>();
    private static final HashMap<Object, Object> potionAmplifiers = new HashMap<>();
    private static final HashMap<Object, Object> potionEffects = new HashMap<>();

    private static final String[] potionPrefixes = new String[] {
            "potion.effect.mundane", "potion.effect.uninteresting", "potion.effect.bland", "potion.effect.clear", "potion.effect.milky", "potion.effect.diffuse", "potion.effect.artless", "potion.effect.thin", "potion.effect.awkward", "potion.effect.flat",
            "potion.effect.bulky", "potion.effect.bungling", "potion.effect.buttered", "potion.effect.smooth", "potion.effect.suave", "potion.effect.debonair", "potion.effect.thick", "potion.effect.elegant", "potion.effect.fancy", "potion.effect.charming",
            "potion.effect.dashing", "potion.effect.refined", "potion.effect.cordial", "potion.effect.sparkling", "potion.effect.potent", "potion.effect.foul", "potion.effect.odorless", "potion.effect.rank", "potion.effect.harsh", "potion.effect.acrid",
            "potion.effect.gross", "potion.effect.stinky" };

    private static boolean calculationOfBoolean(int liquidData, int paramInt2) {
        return ((liquidData & 1 << paramInt2 % 15) != 0);
    }

    private static boolean b(int paramInt1, int paramInt2) {
        return ((paramInt1 & 1 << paramInt2) != 0);
    }

    private static int getPotionPrefix(int paramInt1, int paramInt2) {
        return b(paramInt1, paramInt2) ? 1 : 0;
    }

    private static int d(int paramInt1, int paramInt2) {
        return b(paramInt1, paramInt2) ? 0 : 1;
    }

    public static int getPrefixNumber(int damage) {
        return getNumberInBinaryNumber(damage, 14, 9, 7, 3, 2);
    }

    public static int getPotionColor(int damage) {
        int i = (getNumberInBinaryNumber(damage, 2, 14, 11, 8, 5) ^ 0x3) << 3;
        int j = (getNumberInBinaryNumber(damage, 0, 12, 9, 6, 3) ^ 0x6) << 3;
        int k = (getNumberInBinaryNumber(damage, 13, 10, 4, 1, 7) ^ 0x8) << 3;
        return i << 16 | j << 8 | k;
    }

    public static String getPotionPrefix(int damage) {
        int prefixNumber = getPrefixNumber(damage);
        return potionPrefixes[prefixNumber];
    }

    private static int a(boolean b1, boolean b2, boolean b3, int i1, int i2, int i3, int i4) {
        int i = 0;
        if (b1) {
            i = d(i4, i2);
        } else if (i1 != -1) {
            if (i1 == 0 && h(i4) == i2) {
                i = 1;
            } else if (i1 == 1 && h(i4) > i2) {
                i = 1;
            } else if (i1 == 2 && h(i4) < i2) {
                i = 1;
            }
        } else {
            i = getPotionPrefix(i4, i2);
        }
        if (b2) {
            i *= i3;
        }
        if (b3) {
            i *= -1;
        }
        return i;
    }

    private static int h(int paramInt) {
        int i = 0;
        for (; paramInt > 0; i++) {
            paramInt &= paramInt - 1;
        }
        return i;
    }

    private static int a(String paramString, int paramInt1, int paramInt2, int paramInt3) {
        if (paramInt1 >= paramString.length() || paramInt2 < 0 || paramInt1 >= paramInt2) return 0;
        int i = paramString.indexOf('|', paramInt1);
        if (i >= 0 && i < paramInt2) {
            int i2 = a(paramString, paramInt1, i - 1, paramInt3);
            if (i2 > 0) {
                return i2;
            }
            int i3 = a(paramString, i + 1, paramInt2, paramInt3);
            return Math.max(i3, 0);
        }
        int j = paramString.indexOf('&', paramInt1);
        if (j >= 0 && j < paramInt2) {
            int i2 = a(paramString, paramInt1, j - 1, paramInt3);
            if (i2 <= 0) {
                return 0;
            }
            int i3 = a(paramString, j + 1, paramInt2, paramInt3);
            if (i3 <= 0) {
                return 0;
            }
            return Math.max(i2, i3);
        }
        boolean bool1 = false;
        boolean bool2 = false;
        boolean bool3 = false;
        boolean bool4 = false;
        boolean bool5 = false;
        byte b = -1;
        int k = 0;
        int m = 0;
        int n = 0;
        for (int i1 = paramInt1; i1 < paramInt2; i1++) {
            char c = paramString.charAt(i1);
            if (c >= '0' && c <= '9') {
                if (bool1) {
                    m = c - 48;
                    bool2 = true;
                } else {
                    k *= 10;
                    k += c - 48;
                    bool3 = true;
                }
            } else if (c == '*') {
                bool1 = true;
            } else if (c == '!') {
                if (bool3) {
                    n += a(bool4, bool2, bool5, b, k, m, paramInt3);
                    bool3 = bool2 = bool1 = bool5 = false;
                    k = m = 0;
                    b = -1;
                }
                bool4 = true;
            } else if (c == '-') {
                if (bool3) {
                    n += a(bool4, bool2, bool5, b, k, m, paramInt3);
                    bool3 = bool2 = bool1 = bool4 = false;
                    k = m = 0;
                    b = -1;
                }
                bool5 = true;
            } else if (c == '=' || c == '<' || c == '>') {
                if (bool3) {
                    n += a(bool4, bool2, bool5, b, k, m, paramInt3);
                    bool3 = bool2 = bool1 = bool5 = bool4 = false;
                    k = m = 0;
                }
                if (c == '=') {
                    b = 0;
                } else if (c == '<') {
                    b = 2;
                } else {
                    b = 1;
                }
            } else if (c == '+' && bool3) {
                n += a(bool4, bool2, bool5, b, k, m, paramInt3);
                bool3 = bool2 = bool1 = bool5 = bool4 = false;
                k = m = 0;
                b = -1;
            }
        }
        if (bool3) {
            n += a(bool4, bool2, bool5, b, k, m, paramInt3);
        }
        return n;
    }

    @SuppressWarnings("deprecation")
    public static List<?> getPotionEffects(int damage) {
        ArrayList<MobEffectInstance> localArrayList = null;
        for (PotionCauldron localPotionCauldron : PotionCauldron.potionTypes) {
            if (localPotionCauldron != null) {
                String str = (String)potionRequirements.get(localPotionCauldron.getId());
                if (str != null) {
                    int k = a(str, 0, str.length(), damage);
                    if (k > 0) {
                        int m = 0;
                        String str2 = (String)potionAmplifiers.get(localPotionCauldron.getId());
                        if (str2 != null) {
                            m = a(str2, 0, str2.length(), damage);
                            if (m < 0)
                                m = 0;
                        }
                        if (localPotionCauldron.isInstant()) {
                            k = 1;
                        } else {
                            k = 1200 * (k * 3 + (k - 1) * 2);
                            if (localPotionCauldron.isUsable())
                                k >>= 1;
                        }
                        if (localArrayList == null) {
                            localArrayList = new ArrayList<>();
                        }
                        localArrayList.add(new MobEffectInstance(Objects.requireNonNull(Registry.MOB_EFFECT.byId(localPotionCauldron.getId())), k, m));
                    }
                }
            }
        }
        return localArrayList;
    }

    public static int calculationOfNetherWarts1(int liquidData) {
        if ((liquidData & 0x1) == 0) {
            return liquidData;
        }
        byte b = 14;
        while ((liquidData & 1 << b) == 0 && b >= 0) {
            b--;
        }
        if (b < 2 || (liquidData & 1 << b - 1) != 0) {
            return liquidData;
        }
        if (b >= 0) {
            liquidData &= ~(1 << b);
        }
        liquidData <<= 1;
        if (b >= 0) {
            liquidData |= 1 << b;
            liquidData |= 1 << b - 1;
        }
        return liquidData & 0x7FFF;
    }

    public static int calculationOfNetherWarts2(int liquidData) {
        byte b = 14;
        while ((liquidData & 1 << b) == 0 && b >= 0) {
            b--;
        }
        if (b >= 0) {
            liquidData &= ~(1 << b);
        }
        int i = 0;
        int j = liquidData;
        while (j != i) {
            j = liquidData;
            i = 0;
            for (byte b1 = 0; b1 < 15; b1++) {
                boolean bool = calculationOfBoolean(liquidData, b1);
                if (bool) {
                    if (!calculationOfBoolean(liquidData, b1 + 1) && calculationOfBoolean(liquidData, b1 + 2)) {
                        bool = false;
                    } else if (!calculationOfBoolean(liquidData, b1 - 1) && calculationOfBoolean(liquidData, b1 - 2)) {
                        bool = false;
                    }
                } else {
                    bool = (calculationOfBoolean(liquidData, b1 - 1) && calculationOfBoolean(liquidData, b1 + 1));
                }
                if (bool)
                    i |= 1 << b1;
            }
            liquidData = i;
        }
        if (b >= 0)
            i |= 1 << b;
        return i & 0x7FFF;
    }

    public static int applyNetherWart(int paramInt) {
        if ((paramInt & 0x1) != 0) {
            paramInt = calculationOfNetherWarts1(paramInt);
        }
        return calculationOfNetherWarts2(paramInt);
    }

    private static int a(int i1, int i2, boolean b1, boolean b2) {
        if (b1) {
            i1 &= ~(1 << i2);
        } else if (b2) {
            if ((i1 & 1 << i2) != 0) {
                i1 &= ~(1 << i2);
            } else {
                i1 |= 1 << i2;
            }
        } else {
            i1 |= 1 << i2;
        }
        return i1;
    }

    public static int applyIngredient(int paramInt, String paramString) {
        byte b1 = 0;
        int i = paramString.length();
        boolean bool1 = false;
        boolean bool2 = false;
        boolean bool3 = false;
        int j = 0;
        for (byte b2 = b1; b2 < i; b2++) {
            char c = paramString.charAt(b2);
            if (c >= '0' && c <= '9') {
                j *= 10;
                j += c - 48;
                bool1 = true;
            } else if (c == '!') {
                if (bool1) {
                    paramInt = a(paramInt, j, bool3, bool2);
                    bool1 = bool3 = false;
                    j = 0;
                }
                bool2 = true;
            } else if (c == '-') {
                if (bool1) {
                    paramInt = a(paramInt, j, bool3, bool2);
                    bool1 = bool2 = false;
                    j = 0;
                }
                bool3 = true;
            } else if (c == '+' && bool1) {
                paramInt = a(paramInt, j, bool3, bool2);
                bool1 = bool3 = bool2 = false;
                j = 0;
            }
        }
        if (bool1) {
            paramInt = a(paramInt, j, bool3, bool2);
        }
        return paramInt & 0x7FFF;
    }

    public static int getNumberInBinaryNumber(int i1, int i2, int i3, int i4, int i5, int i6) {
        return (b(i1, i2) ? 16 : 0) | (b(i1, i3) ? 8 : 0) | (b(i1, i4) ? 4 : 0) | (b(i1, i5) ? 2 : 0) | (b(i1, i6) ? 1 : 0);
    }

    public static boolean isPotionIngredient(int itemID) {
        return potionEffects.containsKey(itemID);
    }

    public static String getPotionEffect(int itemID) {
        return (String)potionEffects.get(itemID);
    }

    static {
        potionRequirements.put(PotionCauldron.moveSpeed.getId(), "!10 & !4 & 5*2+0 & >1 | !7 & !4 & 5*2+0 & >1");
        potionRequirements.put(PotionCauldron.moveSlowdown.getId(), "10 & 7 & !4 & 7+5+1-0");
        potionRequirements.put(PotionCauldron.digSpeed.getId(), "2 & 12+2+6-1-7 & <8");
        potionRequirements.put(PotionCauldron.digSlowdown.getId(), "!2 & !1*2-9 & 14-5");
        potionRequirements.put(PotionCauldron.damageBoost.getId(), "9 & 3 & 9+4+5 & <11");
        potionRequirements.put(PotionHealthCauldron.heal.getId(), "11 & <6");
        potionRequirements.put(PotionHealthCauldron.harm.getId(), "!11 & 1 & 10 & !7");
        potionRequirements.put(PotionCauldron.jump.getId(), "8 & 2+0 & <5");
        potionRequirements.put(PotionCauldron.confusion.getId(), "8*2-!7+4-11 & !2 | 13 & 11 & 2*3-1-5");
        potionRequirements.put(PotionCauldron.regeneration.getId(), "!14 & 13*3-!0-!5-8");
        potionRequirements.put(PotionCauldron.resistance.getId(), "10 & 4 & 10+5+6 & <9");
        potionRequirements.put(PotionCauldron.fireResistance.getId(), "14 & !5 & 6-!1 & 14+13+12");
        potionRequirements.put(PotionCauldron.waterBreathing.getId(), "0+1+12 & !6 & 10 & !11 & !13");
        potionRequirements.put(PotionCauldron.invisibility.getId(), "2+5+13-0-4 & !7 & !1 & >5");
        potionRequirements.put(PotionCauldron.blindness.getId(), "9 & !1 & !5 & !3 & =3");
        potionRequirements.put(PotionCauldron.nightVision.getId(), "8*2-!7 & 5 & !0 & >3");
        potionRequirements.put(PotionCauldron.hunger.getId(), ">4>6>8-3-8+2");
        potionRequirements.put(PotionCauldron.weakness.getId(), "=1>5>7>9+3-7-2-11 & !10 & !0");
        potionRequirements.put(PotionCauldron.poison.getId(), "12+9 & !13 & !0");
        potionAmplifiers.put(PotionCauldron.moveSpeed.getId(), "7+!3-!1");
        potionAmplifiers.put(PotionCauldron.digSpeed.getId(), "1+0-!11");
        potionAmplifiers.put(PotionCauldron.damageBoost.getId(), "2+7-!12");
        potionAmplifiers.put(PotionHealthCauldron.heal.getId(), "11+!0-!1-!14");
        potionAmplifiers.put(PotionHealthCauldron.harm.getId(), "!11-!14+!0-!1");
        potionAmplifiers.put(PotionCauldron.resistance.getId(), "12-!2");
        potionAmplifiers.put(PotionCauldron.poison.getId(), "14>5");
        potionEffects.put(Item.getId(Items.GHAST_TEAR), "+11");
        potionEffects.put(Item.getId(Items.BLAZE_POWDER), "+14");
        potionEffects.put(Item.getId(Items.MAGMA_CREAM), "+14+6+1");
        potionEffects.put(Item.getId(Items.SUGAR), "+0");
        potionEffects.put(Item.getId(Items.SPIDER_EYE), "+10+7+5");
        potionEffects.put(Item.getId(Items.FERMENTED_SPIDER_EYE), "+14+9");
    }

}
