package com.example.pulsevisuals.client.util;

public final class ModConfig {

    private ModConfig() {}

    public static int ACCENT_PRIMARY = 0xFF8A5CFF;
    public static int ACCENT_SECONDARY = 0xFF3CE7E0;

    public static int PANEL_BG = 0xBF0F0F14;
    public static int PANEL_BORDER = 0x33FFFFFF;

    public static int HP_COLOR = 0xFF58E86E;
    public static int HP_LOW_COLOR = 0xFFFF5C5C;
    public static int HP_LAG_COLOR = 0x80FF5C5C;

    public static int withAlpha(int rgb, int alpha0to255) {
        return (alpha0to255 << 24) | (rgb & 0x00FFFFFF);
    }

    public static int lerpColor(int colorA, int colorB, float t) {
        float a = AnimationUtil.clamp01(t);
        int aa = (int) AnimationUtil.lerp((colorA >> 24) & 0xFF, (colorB >> 24) & 0xFF, a);
        int ar = (int) AnimationUtil.lerp((colorA >> 16) & 0xFF, (colorB >> 16) & 0xFF, a);
        int ag = (int) AnimationUtil.lerp((colorA >> 8) & 0xFF, (colorB >> 8) & 0xFF, a);
        int ab = (int) AnimationUtil.lerp(colorA & 0xFF, colorB & 0xFF, a);
        return (aa << 24) | (ar << 16) | (ag << 8) | ab;
    }
}
