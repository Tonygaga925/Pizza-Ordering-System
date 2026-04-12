package model.order;

public class CancelledState implements OrderStatus {
    @Override
    public String getStatusName() {
        return "Cancelled";
    }
}
