package claro.jpa.order;


public enum OrderStatus {
    InShoppingCart, 
    PendingPayment, 
    ReceivedPayment, 
    Processing, 
    Shipped, 
    OnHold, 
    Complete, 
    Closed, 
    Canceled
}