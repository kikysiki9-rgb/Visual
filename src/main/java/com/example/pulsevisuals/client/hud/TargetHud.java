package com.example.pulsevisuals.client.hud;

import com.example.pulsevisuals.client.render.Render2D;
import com.example.pulsevisuals.client.util.AnimationUtil;
import com.example.pulsevisuals.client.util.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.lang.ref.WeakReference;

public final class TargetHud {

    private static WeakReference<LivingEntity> target = new WeakReference<>(null);
    private static long lastHitTick = Long.MIN_VALUE;
    private static long tickCounter = 0;

    private static final int FADE_OUT_AFTER_TICKS = 60;

    private static final AnimationUtil.AnimatedFloat visibility = new AnimationUtil.AnimatedFloat(0f, 10f);
    private static final AnimationUtil.AnimatedFloat hpFront = new AnimationUtil.AnimatedFloat(1f, 14f);
    private static final AnimationUtil.AnimatedFloat hpLag = new AnimationUtil.AnimatedFloat(1f, 4f);

    private TargetHud() {}

    public static void onHit(LivingEntity entity) {
        target = new WeakReference<>(entity);
        lastHitTick = tickCounter;
        float ratio = AnimationUtil.clamp01(entity.getHealth() / entity.getMaxHealth());
        hpFront.setTarget(ratio);
    }

    public static void tick() {
        tickCounter++;
        LivingEntity t = target.get();
        boolean alive = t != null && t.isAlive();
        boolean recent = (tickCounter - lastHitTick) < FADE_OUT_AFTER_TICKS;
        visibility.setTarget(alive && recent ? 1f : 0f);

        if (alive) {
            float ratio = AnimationUtil.clamp01(t.getHealth() / t.getMaxHealth());
            hpFront.setTarget(ratio);
        }

        float dt = 1f / 20f;
        visibility.update(dt);
        hpFront.update(dt);
        hpLag.setTarget(hpFront.getTarget());
        hpLag.update(dt * 0.5f);
    }

    public static void render(DrawContext context) {
        float alpha = AnimationUtil.easeOutCubic(visibility.get());
        if (alpha <= 0.01f) return;

        LivingEntity entity = target.get();
        if (entity == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        float w = 160f;
        float h = 54f;
        float x = (context.getScaledWindowWidth() - w) / 2f;
        float y = context.getScaledWindowHeight() * 0.18f;

        float slide = (1f - alpha) * -10f;
        y += slide;

        int panelColor = ModConfig.withAlpha(ModConfig.PANEL_BG, (int) (0xBF * alpha));
        Render2D.drawGlow(context, x, y, w, h, ModConfig.withAlpha(ModConfig.ACCENT_PRIMARY, (int) (70 * alpha)), 4, 8f);
        Render2D.drawRoundedRect(context, x, y, w, h, 12f, panelColor);
        Render2D.drawRoundedOutline(context, x, y, w, h, 12f, 1f, ModConfig.withAlpha(0xFFFFFF, (int) (60 * alpha)));

        String name = entity instanceof PlayerEntity p ? p.getGameProfile().getName() : entity.getName().getString();
        context.drawText(client.textRenderer, Text.literal(name),
                (int) (x + 10), (int) (y + 6),
                (int) (0xFFFFFF | ((int) (255 * alpha) << 24)), true);

        float barX = x + 10;
        float barY = y + 20;
        float barW = w - 20;
        float barH = 8f;

        Render2D.drawRoundedRect(context, barX, barY, barW, barH, barH / 2f, ModConfig.withAlpha(0x222228, (int) (255 * alpha)));

        int lagColor = ModConfig.withAlpha(ModConfig.HP_LAG_COLOR & 0xFFFFFF, (int) (150 * alpha));
        Render2D.drawRoundedRect(context, barX, barY, barW * hpLag.get(), barH, barH / 2f, lagColor);

        float ratio = hpFront.get();
        int hpColor = ModConfig.lerpColor(ModConfig.HP_LOW_COLOR, ModConfig.HP_COLOR, ratio);
        Render2D.drawRoundedRect(context, barX, barY, barW * ratio, barH, barH / 2f, ModConfig.withAlpha(hpColor & 0xFFFFFF, (int) (255 * alpha)));

        MinecraftClient mc = MinecraftClient.getInstance();
        float distance = mc.player != null ? mc.player.distanceTo(entity) : 0f;
        int armorPieces = 0;
        for (var stack : entity.getArmorItems()) {
            if (!stack.isEmpty()) armorPieces++;
        }
        String info = String.format("%.1fm  •  броня %d/4", distance, armorPieces);
        context.drawText(client.textRenderer, Text.literal(info),
                (int) (x + 10), (int) (barY + barH + 6),
                0xAAAAAA | ((int) (200 * alpha) << 24), false);

        if (entity instanceof PlayerEntity p && !p.getMainHandStack().isEmpty()) {
            context.drawItem(p.getMainHandStack(), (int) (x + w - 26), (int) (y + 6));
        }
    }
}
