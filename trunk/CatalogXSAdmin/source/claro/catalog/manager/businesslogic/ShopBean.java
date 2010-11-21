package claro.catalog.manager.businesslogic;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import claro.catalog.manager.businesslogic.Shop;
import claro.catalog.manager.businesslogic.ShopBeanBase;
import claro.jpa.order.Order;
import claro.jpa.order.ProductOrder;

@Stateless
public class ShopBean extends ShopBeanBase implements Shop {
  
  
}
