// model/size/MediumSize.java
package model.size;

public class MediumSize implements Size {
    @Override
    public String getName() {
        return "Medium";
    }
    
    @Override
    public double getMultiplier() {
        return 1.3;
    }
}