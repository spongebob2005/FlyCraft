package com.sameeran.flycraft.animation;

/**
 * Easing function interface for smooth animations
 */
@FunctionalInterface
public interface EasingFunction {
    float apply(float t);

    // Common easing functions
    static class Easing {
        /**
         * Linear interpolation
         */
        public static final EasingFunction LINEAR = t -> t;

        /**
         * Ease in: slow start, fast end
         */
        public static final EasingFunction EASE_IN = t -> t * t;

        /**
         * Ease out: fast start, slow end
         */
        public static final EasingFunction EASE_OUT = t -> t * (2 - t);

        /**
         * Ease in-out: slow start and end, fast middle
         */
        public static final EasingFunction EASE_IN_OUT = t -> t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;

        /**
         * Elastic easing - bounces at the end
         */
        public static final EasingFunction ELASTIC = t -> {
            double c5 = (2 * Math.PI) / 4.5;
            return t == 0 ? 0 : t == 1 ? 1 : (float) (Math.pow(2, -10 * t) * Math.sin((t * 40 - 3) * c5) + 1);
        };

        /**
         * Bounce easing - multiple bounces
         */
        public static final EasingFunction BOUNCE = t -> {
            if (t < 0.5) {
                return 4 * t * t * t;
            } else {
                return (float) (1 - Math.pow(-2 * t + 2, 3) / 2);
            }
        };
    }
}
