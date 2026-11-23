package com.example.pulsevisuals;

import com.example.pulsevisuals.client.hud.TargetHud;
import com.example.pulsevisuals.client.hud.Watermark;
import com.example.pulsevisuals.client.visuals.HitParticles;
import com.example.pulsevisuals.client.visuals.JumpCircles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public class PulseVisualsClient implements ClientModInitializer {

    private boolean wasOnGround = true;

    @Override
    public void onInitializeClient() {

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient && entity instanceof LivingEntity living) {
                boolean critical = player.fallDistance > 0f && !player.isOnGround() && !player.isClimbing();
                HitParticles.spawnBurst((ClientWorld) world,
                        entity.getX(), entity.getBodyY(0.5), entity.getZ(), critical);
                TargetHud.onHit(living);
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            TargetHud.tick();
            JumpCircles.tick();

            if (client.player == null) return;
            boolean onGround = client.player.isOnGround();
            if (wasOnGround && !onGround && client.player.getVelocity().y > 0.05) {
                JumpCircles.onJump(client.player.getPos());
            }
            wasOnGround = onGround;
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (MinecraftClient.getInstance().options.hudHidden) return;
            Watermark.render(drawContext);
            TargetHud.render(drawContext);
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(JumpCircles::render);
    }
}
