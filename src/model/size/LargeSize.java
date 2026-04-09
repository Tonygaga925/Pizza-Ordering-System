// model/size/LargeSize.java
package model.size;

public class LargeSize implements Size {
    @Override
    public String getName() {
        return "Large";
    }
    
    @Override
    public double getMultiplier() {
        return 1.6;
    }
}