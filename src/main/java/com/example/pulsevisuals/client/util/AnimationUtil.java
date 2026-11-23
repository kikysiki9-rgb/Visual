package com.example.pulsevisuals.client.util;

public final class AnimationUtil {

    private AnimationUtil() {}

    public static float easeOutCubic(float t) {
        float f = t - 1f;
        return f * f * f + 1f;
    }

    public static float smoothStep(float edge0, float edge1, float x) {
        float t = clamp01((x - edge0) / (edge1 - edge0));
        return t * t * (3f - 2f * t);
    }

    public static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp01(t);
    }

    public static class AnimatedFloat {
        private float current;
        private float target;
        private final float speed;

        public AnimatedFloat(float initial, float speed) {
            this.current = initial;
            this.target = initial;
            this.speed = speed;
        }

        public void setTarget(float target) {
            this.target = target;
        }

        public void snapTo(float value) {
            this.current = value;
            this.target = value;
        }

        public void update(float deltaSeconds) {
            float t = 1f - (float) Math.exp(-speed * deltaSeconds);
            current = lerp(current, target, t);
        }

        public float get() {
            return current;
        }

        public float getTarget() {
            return target;
        }

        public boolean isSettled(float epsilon) {
            return Math.abs(target - current) < epsilon;
        }
    }
}
