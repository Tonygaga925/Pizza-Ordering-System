package model.order;

public class ProcessingState implements OrderStatus {
    @Override
    public String getStatusName() {
        return "Processing";
    }
}
