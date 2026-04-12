package model.size;

import java.util.*;
import java.lang.reflect.Constructor;

public class SizeFactory {
    private static final List<Class<? extends Size>> sizeClasses = new ArrayList<>();
    private static final List<String> sizeNames = new ArrayList<>();
    private static final List<Double> sizeMultipliers = new ArrayList<>();
    private static final Map<Integer, Size> sizeCache = new HashMap<>();
    
    static {
        // Register all size classes
        registerSize(SmallSize.class);
        registerSize(MediumSize.class);
        registerSize(LargeSize.class);
    }
    
    private static void registerSize(Class<? extends Size> sizeClass) {
        try {
            Constructor<? extends Size> constructor = sizeClass.getConstructor();
            Size instance = constructor.newInstance();
            sizeClasses.add(sizeClass);
            sizeNames.add(instance.getName());
            sizeMultipliers.add(instance.getMultiplier());
        } catch (Exception e) {
            System.err.println("Failed to register size: " + sizeClass.getSimpleName());
        }
    }
    
    public static Size getSize(int choice) {
        if (choice < 1 || choice > sizeClasses.size()) {
            throw new IllegalArgumentException("Invalid size choice: " + choice);
        }
        
        // Return cached instance
        if (sizeCache.containsKey(choice)) {
            return sizeCache.get(choice);
        }
        
        try {
            Size instance = sizeClasses.get(choice - 1).getConstructor().newInstance();
            sizeCache.put(choice, instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create size", e);
        }
    }
    
    public static Size getSizeByName(String name) {
        for (int i = 0; i < sizeNames.size(); i++) {
            if (sizeNames.get(i).equalsIgnoreCase(name)) {
                return getSize(i + 1);
            }
        }
        throw new IllegalArgumentException("Invalid size name: " + name);
    }
    

    
    public static List<String> getSizeNames() {
        return new ArrayList<>(sizeNames);
    }
    
    public static List<Double> getSizeMultipliers() {
        return new ArrayList<>(sizeMultipliers);
    }
    
    public static int getSizeCount() {
        return sizeClasses.size();
    }
    
    public static double getMultiplierByName(String name) {
        for (int i = 0; i < sizeNames.size(); i++) {
            if (sizeNames.get(i).equalsIgnoreCase(name)) {
                return sizeMultipliers.get(i);
            }
        }
        return 1.0; // default
    }
}