package com.glyceryl6.cauldron.block;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PotionHelperCauldron {

    private static final HashMap<Object, Object> potionRequirements = new HashMap<>();
    private static final HashMap<Object, Object> potionAmplifiers = new HashMap<>();
    private static final HashMap<Object, Object> potionEffects = new HashMap<>();

    private static final String[] c = new String[] {
            "potion.effect.mundane", "potion.effect.uninteresting", "potion.effect.bland", "potion.effect.clear", "potion.effect.milky", "potion.effect.diffuse", "potion.effect.artless", "potion.effect.thin", "potion.effect.awkward", "potion.effect.flat",
            "potion.effect.bulky", "potion.effect.bungling", "potion.effect.buttered", "potion.effect.smooth", "potion.effect.suave", "potion.effect.debonair", "potion.effect.thick", "potion.effect.elegant", "potion.effect.fancy", "potion.effect.charming",
            "potion.effect.dashing", "potion.effect.refined", "potion.effect.cordial", "potion.effect.sparkling", "potion.effect.potent", "potion.effect.foul", "potion.effect.odorless", "potion.effect.rank", "potion.effect.harsh", "potion.effect.acrid",
            "potion.effect.gross", "potion.effect.stinky" };

    private static boolean a(int paramInt1, int paramInt2) {
        return ((paramInt1 & 1 << paramInt2 % 15) != 0);
    }

    private static boolean b(int paramInt1, int paramInt2) {
        return ((paramInt1 & 1 << paramInt2) != 0);
    }

    private static int c(int paramInt1, int paramInt2) {
        return b(paramInt1, paramInt2) ? 1 : 0;
    }

    private static int d(int paramInt1, int paramInt2) {
        return b(paramInt1, paramInt2) ? 0 : 1;
    }

    public static int a(int paramInt) {
        return a(paramInt, 14, 9, 7, 3, 2);
    }

    public static int b(int paramInt) {
        int i = (a(paramInt, 2, 14, 11, 8, 5) ^ 0x3) << 3;
        int j = (a(paramInt, 0, 12, 9, 6, 3) ^ 0x6) << 3;
        int k = (a(paramInt, 13, 10, 4, 1, 7) ^ 0x8) << 3;
        return i << 16 | j << 8 | k;
    }

    public static String c(int paramInt) {
        int i = a(paramInt);
        return c[i];
    }

    private static int a(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        int i = 0;
        if (paramBoolean1) {
            i = d(paramInt4, paramInt2);
        } else if (paramInt1 != -1) {
            if (paramInt1 == 0 && h(paramInt4) == paramInt2) {
                i = 1;
            } else if (paramInt1 == 1 && h(paramInt4) > paramInt2) {
                i = 1;
            } else if (paramInt1 == 2 && h(paramInt4) < paramInt2) {
                i = 1;
            }
        } else {
            i = c(paramInt4, paramInt2);
        }
        if (paramBoolean2)
            i *= paramInt3;
        if (paramBoolean3)
            i *= -1;
        return i;
    }

    private static int h(int paramInt) {
        int i = 0;
        for (; paramInt > 0; i++)
            paramInt &= paramInt - 1;
        return i;
    }

    private static int a(String paramString, int paramInt1, int paramInt2, int paramInt3) {
        if (paramInt1 >= paramString.length() || paramInt2 < 0 || paramInt1 >= paramInt2)
            return 0;
        int i = paramString.indexOf('|', paramInt1);
        if (i >= 0 && i < paramInt2) {
            int i6 = a(paramString, paramInt1, i - 1, paramInt3);
            if (i6 > 0)
                return i6;
            int i7 = a(paramString, i + 1, paramInt2, paramInt3);
            if (i7 > 0)
                return i7;
            return 0;
        }
        int j = paramString.indexOf('&', paramInt1);
        if (j >= 0 && j < paramInt2) {
            int i6 = a(paramString, paramInt1, j - 1, paramInt3);
            if (i6 <= 0)
                return 0;
            int i7 = a(paramString, j + 1, paramInt2, paramInt3);
            if (i7 <= 0)
                return 0;
            if (i6 > i7)
                return i6;
            return i7;
        }
        int k = 0;
        boolean m = false;
        int n = 0;
        boolean bool2 = false;
        boolean bool3 = false;
        int i1 = -1;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        boolean bool1 = false;
        for (int i5 = paramInt1; i5 < paramInt2; i5++) {
            int i6 = paramString.charAt(i5);
            if (i6 >= 48 && i6 <= 57) {
                if (k != 0) {
                    i3 = i6 - 48;
                    m = true;
                } else {
                    i2 *= 10;
                    i2 += i6 - 48;
                    n = 1;
                }
            } else if (i6 == 42) {
                k = 1;
            } else if (i6 == 33) {
                if (n != 0) {
                    i4 += a(bool2, m, bool3, i1, i2, i3, paramInt3);
                    n = 0;
                    bool1 = false;
                    k = 0;
                    bool3 = false;
                    bool2 = false;
                    i2 = i3 = 0;
                    i1 = -1;
                }
                bool2 = true;
            } else if (i6 == 45) {
                if (n != 0) {
                    i4 += a(bool2, bool1, bool3, i1, i2, i3, paramInt3);
                    n = 0;
                    bool1 = false;
                    k = 0;
                    bool3 = false;
                    bool2 = false;
                    i2 = i3 = 0;
                    i1 = -1;
                }
                bool3 = true;
            } else if (i6 == 61 || i6 == 60 || i6 == 62) {
                if (n != 0) {
                    i4 += a(bool2, bool1, bool3, i1, i2, i3, paramInt3);
                    n = 0;
                    bool1 = false;
                    k = 0;
                    bool3 = false;
                    bool2 = false;
                    i2 = i3 = 0;
                    i1 = -1;
                }
                if (i6 == 61) {
                    i1 = 0;
                } else if (i6 == 60) {
                    i1 = 2;
                } else if (i6 == 62) {
                    i1 = 1;
                }
            } else if (i6 == 43 && n != 0) {
                i4 += a(bool2, bool1, bool3, i1, i2, i3, paramInt3);
                n = 0;
                bool1 = false;
                k = 0;
                bool3 = false;
                bool2 = false;
                i2 = i3 = 0;
                i1 = -1;
            }
        }
        if (n != 0)
            i4 += a(bool2, bool1, bool3, i1, i2, i3, paramInt3);
        return i4;
    }

    public static List d(int paramInt) {
        ArrayList<PotionEffect> localArrayList = null;
        for (PotionCauldron localPotionCauldron : PotionCauldron.potionTypes) {
            if (localPotionCauldron != null) {
                String str1 = (String)potionRequirements.get(localPotionCauldron.getId());
                if (str1 != null) {
                    int k = a(str1, 0, str1.length(), paramInt);
                    if (k > 0) {
                        int m = 0;
                        String str2 = (String)potionAmplifiers.get(localPotionCauldron.getId());
                        if (str2 != null) {
                            m = a(str2, 0, str2.length(), paramInt);
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
                            localArrayList = new ArrayList();
                        }
                        localArrayList.add(new PotionEffect(Objects.requireNonNull(Potion.getPotionById(localPotionCauldron.getId())), k, m));
                    }
                }
            }
        }
        return localArrayList;
    }

    public static int e(int paramInt) {
        if ((paramInt & 0x1) == 0)
            return paramInt;
        int i = 14;
        while ((paramInt & 1 << i) == 0 && i >= 0)
            i--;
        if (i < 2 || (paramInt & 1 << i - 1) != 0)
            return paramInt;
        if (i >= 0) {
            paramInt &= ~(1 << i);
        }
        paramInt <<= 1;
        if (i >= 0) {
            paramInt |= 1 << i;
            paramInt |= 1 << i - 1;
        }
        return paramInt & 0x7FFF;
    }

    public static int f(int paramInt) {
        int i = 14;
        while ((paramInt & 1 << i) == 0 && i >= 0)
            i--;
        if (i >= 0)
            paramInt &= ~(1 << i);
        int j = 0;
        int k = paramInt;
        while (k != j) {
            k = paramInt;
            j = 0;
            for (int m = 0; m < 15; m++) {
                boolean bool = a(paramInt, m);
                if (bool) {
                    if (!a(paramInt, m + 1) && a(paramInt, m + 2)) {
                        bool = false;
                    } else if (!a(paramInt, m - 1) && a(paramInt, m - 2)) {
                        bool = false;
                    }
                } else {
                    bool = (a(paramInt, m - 1) && a(paramInt, m + 1));
                }
                if (bool)
                    j |= 1 << m;
            }
            paramInt = j;
        }
        if (i >= 0)
            j |= 1 << i;
        return j & 0x7FFF;
    }

    public static int applyNetherWart(int paramInt) {
        if ((paramInt & 0x1) != 0)
            paramInt = e(paramInt);
        return f(paramInt);
    }

    private static int a(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {
        if (paramBoolean1) {
            paramInt1 &= ~(1 << paramInt2);
        } else if (paramBoolean2) {
            if ((paramInt1 & 1 << paramInt2) != 0) {
                paramInt1 &= ~(1 << paramInt2);
            } else {
                paramInt1 |= 1 << paramInt2;
            }
        } else {
            paramInt1 |= 1 << paramInt2;
        }
        return paramInt1;
    }

    public static int applyIngredient(int paramInt, String paramString) {
        int i = 0;
        int j = paramString.length();
        int k = 0;
        boolean bool1 = false;
        boolean bool2 = false;
        int m = 0;
        for (int n = i; n < j; n++) {
            int i1 = paramString.charAt(n);
            if (i1 >= 48 && i1 <= 57) {
                m *= 10;
                m += i1 - 48;
                k = 1;
            } else if (i1 == 33) {
                if (k != 0) {
                    paramInt = a(paramInt, m, bool2, bool1);
                    k = 0;
                    bool2 = bool1 = false;
                    m = 0;
                }
                bool1 = true;
            } else if (i1 == 45) {
                if (k != 0) {
                    paramInt = a(paramInt, m, bool2, bool1);
                    k = 0;
                    bool2 = bool1 = false;
                    m = 0;
                }
                bool2 = true;
            } else if (i1 == 43 && k != 0) {
                paramInt = a(paramInt, m, bool2, bool1);
                k = 0;
                bool2 = bool1 = false;
                m = 0;
            }
        }
        if (k != 0)
            paramInt = a(paramInt, m, bool2, bool1);
        return paramInt & 0x7FFF;
    }

    public static int a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        return (b(paramInt1, paramInt2) ? 16 : 0) | (b(paramInt1, paramInt3) ? 8 : 0) | (b(paramInt1, paramInt4) ? 4 : 0) | (b(paramInt1, paramInt5) ? 2 : 0) | (b(paramInt1, paramInt6) ? 1 : 0);
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
        potionRequirements.put(PotionCauldron.heal.getId(), "11 & <6");
        potionRequirements.put(PotionCauldron.harm.getId(), "!11 & 1 & 10 & !7");
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
        potionAmplifiers.put(PotionCauldron.heal.getId(), "11+!0-!1-!14");
        potionAmplifiers.put(PotionCauldron.harm.getId(), "!11-!14+!0-!1");
        potionAmplifiers.put(PotionCauldron.resistance.getId(), "12-!2");
        potionAmplifiers.put(PotionCauldron.poison.getId(), "14>5");
        potionEffects.put(Item.getIdFromItem(Items.GHAST_TEAR), "+11");
        potionEffects.put(Item.getIdFromItem(Items.BLAZE_POWDER), "+14");
        potionEffects.put(Item.getIdFromItem(Items.MAGMA_CREAM), "+14+6+1");
        potionEffects.put(Item.getIdFromItem(Items.SUGAR), "+0");
        potionEffects.put(Item.getIdFromItem(Items.SPIDER_EYE), "+10+7+5");
        potionEffects.put(Item.getIdFromItem(Items.FERMENTED_SPIDER_EYE), "+14+9");
    }

}
