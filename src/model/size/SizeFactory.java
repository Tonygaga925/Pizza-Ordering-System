// model/size/SizeFactory.java
package model.size;

import java.util.HashMap;
import java.util.Map;

public class SizeFactory {
    private static final Map<Integer, Size> sizeMap = new HashMap<>();
    
    static {
        sizeMap.put(1, new SmallSize());
        sizeMap.put(2, new MediumSize());
        sizeMap.put(3, new LargeSize());
    }
    
    public static Size getSize(int choice) {
        Size size = sizeMap.get(choice);
        if (size == null) {
            throw new IllegalArgumentException("Invalid size choice: " + choice);
        }
        return size;
    }
    
    public static void displaySizeOptions() {
        System.out.println("Size options:");
        System.out.println("  1. Small (x1.0)");
        System.out.println("  2. Medium (x1.3)");
        System.out.println("  3. Large (x1.6)");
    }
}