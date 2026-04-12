package model.order;

public class HandlingState implements OrderStatus {
    @Override
    public String getStatusName() {
        return "Handling";
    }
}
