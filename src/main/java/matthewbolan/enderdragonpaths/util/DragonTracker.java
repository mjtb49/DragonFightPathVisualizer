package matthewbolan.enderdragonpaths.util;

public class DragonTracker {
    private static boolean shouldPrintYDelta;

    public static void setShouldPrintYDelta(boolean shouldPrintYDelta) {
        DragonTracker.shouldPrintYDelta = shouldPrintYDelta;
    }

    public static boolean shouldPrintYDelta() {
        return shouldPrintYDelta;
    }

}
