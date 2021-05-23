package matthewbolan.enderdragonpaths.util;

import net.minecraft.block.BedBlock;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BedDamageSettings {

    private static boolean shouldPrintDamage = true;
    private static ConcurrentLinkedQueue<BlockPos> bedPositions = new ConcurrentLinkedQueue<>();
    private static int damageThreshold = 5;

    public static ConcurrentLinkedQueue<BlockPos> getBedPositions() {
        return bedPositions;
    }

    public static boolean shouldPrintDamage() {
        return shouldPrintDamage;
    }

    public static int getDamageThreshold() {
        return damageThreshold;
    }

    public static void addBedPosition(BlockPos pos) {
       bedPositions.add(pos);
    }

    public static void setShouldPrintDamage(boolean shouldPrintDamage) {
        BedDamageSettings.shouldPrintDamage = shouldPrintDamage;
    }

    public static void setDamageThreshold(int damageThreshold) {
        BedDamageSettings.damageThreshold = damageThreshold;
    }

    public static void resetBedPositions() {
        bedPositions = new ConcurrentLinkedQueue<>();
    }
}
