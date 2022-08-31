package com.glyceryl6.cauldron.util;

import java.nio.ByteOrder;

@SuppressWarnings("unused")
public class ColorUtil {

    public static int setColorOpaque_F(float r, float g, float b) {
        return setColorOpaque((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F));
    }

    public static int setColorRGBA_F(float r, float g, float b, float a) {
        return setColorRGBA((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(a * 255.0F));
    }

    public static int setColorOpaque(int r, int g, int b) {
        return setColorRGBA(r, g, b, 255);
    }

    public static int setColorRGBA(int r, int g, int b, int a) {
        int color;
        if (r > 255) {
            r = 255;
        }

        if (g > 255) {
            g = 255;
        }

        if (b > 255) {
            b = 255;
        }

        if (a > 255) {
            a = 255;
        }

        if (r < 0) {
            r = 0;
        }

        if (g < 0) {
            g = 0;
        }

        if (b < 0) {
            b = 0;
        }

        if (a < 0) {
            a = 0;
        }

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            color = a << 24 | b << 16 | g << 8 | r;
        } else {
            color = r << 24 | g << 16 | b << 8 | a;
        }
        return color;
    }

}
