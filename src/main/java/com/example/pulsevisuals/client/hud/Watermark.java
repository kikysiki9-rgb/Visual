package com.example.pulsevisuals.client.hud;

import com.example.pulsevisuals.client.render.Render2D;
import com.example.pulsevisuals.client.util.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

public final class Watermark {

    private Watermark() {}

    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        String fps = client.getCurrentFps() + " fps";
        String coords = String.format("%.0f, %.0f, %.0f", player.getX(), player.getY(), player.getZ());
        String text = "Pulse  •  " + fps + "  •  " + coords;

        int textWidth = client.textRenderer.getWidth(text);
        float pad = 10f;
        float w = textWidth + pad * 2;
        float h = 20f;
        float x = (context.getScaledWindowWidth() - w) / 2f;
        float y = 8f;

        Render2D.drawGlow(context, x, y, w, h, ModConfig.withAlpha(ModConfig.ACCENT_PRIMARY, 60), 4, 6f);
        Render2D.drawRoundedRect(context, x, y, w, h, h / 2f, ModConfig.PANEL_BG);
        Render2D.drawRoundedOutline(context, x, y, w, h, h / 2f, 1f, ModConfig.PANEL_BORDER);

        context.drawText(client.textRenderer, text,
                (int) (x + pad), (int) (y + (h - client.textRenderer.fontHeight) / 2f),
                0xFFFFFFFF, true);
    }
}
