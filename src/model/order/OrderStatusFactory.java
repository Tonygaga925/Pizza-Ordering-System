package model.order;

public class OrderStatusFactory {
    public static OrderStatus createStatus(String statusName) {
        if (statusName == null) {
            return new ProcessingState();
        }
        
        switch (statusName.toLowerCase()) {
            case "handling":
                return new HandlingState();
            case "completed":
                return new CompletedState();
            case "cancelled":
                return new CancelledState();
            case "processing":
            default:
                return new ProcessingState();
        }
    }
}
