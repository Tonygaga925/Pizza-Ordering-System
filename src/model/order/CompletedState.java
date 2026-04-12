package model.order;

public class CompletedState implements OrderStatus {
    @Override
    public String getStatusName() {
        return "Completed";
    }
}
