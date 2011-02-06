package claro.jpa.order;

import java.io.Serializable;

import claro.jpa.catalog.Product;
import claro.jpa.shop.Promotion;

@SuppressWarnings("serial")
public class ProductOrder implements Serializable {
    private Long id;
    private Product product;
    private Promotion promotion;
    private Integer volume = 0;
    private Double price = 0.0;
    private String priceCurrency = "";
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product value) {
        this.product = value;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion value) {
        this.promotion = value;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer value) {
        if (value == null) {
            value = 0;
        }
        this.volume = value;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double value) {
        if (value == null) {
            value = 0.0;
        }
        this.price = value;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String value) {
        if (value == null) {
            value = "";
        }
        this.priceCurrency = value;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order value) {
        this.order = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ProductOrder) {
            ProductOrder otherProductOrder = (ProductOrder) other;
            if (this.id == null) {
                if (otherProductOrder.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherProductOrder.id)) {
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (id  == null? 0 : id .hashCode());
        return result;
    }

}