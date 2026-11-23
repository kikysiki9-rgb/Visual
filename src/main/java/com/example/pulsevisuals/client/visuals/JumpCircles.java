package com.example.pulsevisuals.client.visuals;

import com.example.pulsevisuals.client.util.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public final class JumpCircles {

    private JumpCircles() {}

    private static final int LIFETIME_TICKS = 14;
    private static final float START_RADIUS = 0.15f;
    private static final float END_RADIUS = 0.9f;
    private static final int SEGMENTS = 24;

    private static final List<Ring> rings = new ArrayList<>();

    private record Ring(Vec3d pos, int spawnTick) {}

    private static int tickCounter = 0;

    public static void onJump(Vec3d feetPos) {
        rings.add(new Ring(feetPos, tickCounter));
    }

    public static void tick() {
        tickCounter++;
        rings.removeIf(r -> tickCounter - r.spawnTick() > LIFETIME_TICKS);
    }

    public static void render(WorldRenderContext context) {
        if (rings.isEmpty()) return;

        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        Matrix4f matrix = context.matrixStack().peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        for (Ring ring : rings) {
            float progress = (tickCounter - ring.spawnTick()) / (float) LIFETIME_TICKS;
            float radius = START_RADIUS + (END_RADIUS - START_RADIUS) * progress;
            int alpha = (int) (160 * (1f - progress));
            int color = ModConfig.withAlpha(ModConfig.ACCENT_SECONDARY & 0xFFFFFF, Math.max(0, alpha));

            float lx = (float) (ring.pos().x - camPos.x);
            float ly = (float) (ring.pos().y - camPos.y) + 0.05f;
            float lz = (float) (ring.pos().z - camPos.z);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            for (int i = 0; i <= SEGMENTS; i++) {
                double angle = (Math.PI * 2 * i) / SEGMENTS;
                float px = lx + (float) (Math.cos(angle) * radius);
                float pz = lz + (float) (Math.sin(angle) * radius);
                buffer.vertex(matrix, px, ly, pz).color(color);
            }
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
