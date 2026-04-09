// model/size/SmallSize.java
package model.size;

public class SmallSize implements Size {
    @Override
    public String getName() {
        return "Small";
    }
    
    @Override
    public double getMultiplier() {
        return 1.0;
    }
}