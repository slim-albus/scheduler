package app.scheduler.utils;

public class BitmaskUtils {
    private static final int DAYS = 6;
    private static final int PERIODS_PER_DAY = 5;
    
    public static long createMask(int[] days, int[] periods) {
        long mask = 0;
        for (int day : days) {
            for (int period : periods) {
                int slotIndex = day * PERIODS_PER_DAY + period;
                mask |= (1L << slotIndex);
            }
        }
        return mask;
    }
    
    public static boolean isAvailable(long mask, int day, int period) {
        int slotIndex = day * PERIODS_PER_DAY + period;
        return (mask & (1L << slotIndex)) != 0;
    }
}
