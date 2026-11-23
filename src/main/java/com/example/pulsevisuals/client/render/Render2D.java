package com.example.pulsevisuals.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferRenderer;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;

public final class Render2D {

    private Render2D() {}

    private static final int CORNER_SEGMENTS = 6;

    public static void drawRoundedRect(DrawContext context, float x, float y, float w, float h, float radius, int argb) {
        radius = Math.min(radius, Math.min(w, h) / 2f);
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        float cx = x + w / 2f;
        float cy = y + h / 2f;
        buffer.vertex(matrix, cx, cy, 0).color(argb);

        float[][] corners = {
                {x + w - radius, y + radius, -90, 0},
                {x + w - radius, y + h - radius, 0, 90},
                {x + radius, y + h - radius, 90, 180},
                {x + radius, y + radius, 180, 270}
        };

        for (float[] corner : corners) {
            float ccx = corner[0];
            float ccy = corner[1];
            float startDeg = corner[2];
            float endDeg = corner[3];
            for (int i = 0; i <= CORNER_SEGMENTS; i++) {
                float t = startDeg + (endDeg - startDeg) * (i / (float) CORNER_SEGMENTS);
                double rad = Math.toRadians(t);
                float px = (float) (ccx + Math.cos(rad) * radius);
                float py = (float) (ccy + Math.sin(rad) * radius);
                buffer.vertex(matrix, px, py, 0).color(argb);
            }
        }
        float firstRad = (float) Math.toRadians(-90);
        buffer.vertex(matrix,
                (float) (corners[0][0] + Math.cos(firstRad) * radius),
                (float) (corners[0][1] + Math.sin(firstRad) * radius),
                0).color(argb);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.disableBlend();
    }

    public static void drawRoundedOutline(DrawContext context, float x, float y, float w, float h, float radius, float thickness, int argb) {
        drawRoundedRect(context, x, y, w, h, radius, argb);
        drawRoundedRect(context, x + thickness, y + thickness, w - thickness * 2, h - thickness * 2,
                Math.max(0, radius - thickness), 0x00000000);
    }

    public static void drawGlow(DrawContext context, float x, float y, float w, float h, int glowColor, int layers, float maxSpread) {
        int alphaBase = (glowColor >> 24) & 0xFF;
        for (int i = layers; i >= 1; i--) {
            float spread = maxSpread * (i / (float) layers);
            float layerAlphaF = alphaBase * (1f - (i / (float) (layers + 1))) * 0.35f;
            int layerAlpha = Math.max(0, Math.min(255, (int) layerAlphaF));
            int color = (layerAlpha << 24) | (glowColor & 0x00FFFFFF);
            drawRoundedRect(context, x - spread, y - spread, w + spread * 2, h + spread * 2, (h + spread * 2) / 2.2f, color);
        }
    }
}
