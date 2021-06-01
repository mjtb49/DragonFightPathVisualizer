package matthewbolan.enderdragonpaths.util;

public class DragonTracker {
    private static boolean shouldPrintYDelta = false;
    private static boolean shouldPrintClockwise = true;
    private static boolean shouldPrintDamageDone = true;
    private static boolean shouldLetDragonSee = true;

    public static void setShouldPrintYDelta(boolean shouldPrintYDelta) {
        DragonTracker.shouldPrintYDelta = shouldPrintYDelta;
    }

    public static void setShouldPrintClockwise(boolean shouldPrintClockwise) {
        DragonTracker.shouldPrintClockwise = shouldPrintClockwise;
    }

    public static void setShouldPrintDamageDone(boolean shouldPrintDamageDone) {
        DragonTracker.shouldPrintDamageDone = shouldPrintDamageDone;
    }

    public static void setShouldLetDragonSee(boolean shouldLetDragonSee) {
        DragonTracker.shouldLetDragonSee = shouldLetDragonSee;
    }

    public static boolean shouldPrintYDelta() {
        return shouldPrintYDelta;
    }

    public static boolean shouldPrintClockwise() {
        return shouldPrintClockwise;
    }

    public static boolean shouldPrintDamageDone() {
        return shouldPrintDamageDone;
    }

    public static boolean shouldLetDragonSee() {
        return shouldLetDragonSee;
    }
}
