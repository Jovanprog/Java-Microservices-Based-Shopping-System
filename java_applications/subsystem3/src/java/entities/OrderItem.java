/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author WDAGUtilityAccount
 */
@Entity
@Table(name = "order_item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OrderItem.findAll", query = "SELECT o FROM OrderItem o"),
    @NamedQuery(name = "OrderItem.findByIdOrder", query = "SELECT o FROM OrderItem o WHERE o.orderItemPK.idOrder = :idOrder"),
    @NamedQuery(name = "OrderItem.findByIdProduct", query = "SELECT o FROM OrderItem o WHERE o.orderItemPK.idProduct = :idProduct"),
    @NamedQuery(name = "OrderItem.findByQuantity", query = "SELECT o FROM OrderItem o WHERE o.quantity = :quantity"),
    @NamedQuery(name = "OrderItem.findByUnitPrice", query = "SELECT o FROM OrderItem o WHERE o.unitPrice = :unitPrice")})
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OrderItemPK orderItemPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private int quantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @JoinColumn(name = "id_order", referencedColumnName = "id_order", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Orders orders;

    public OrderItem() {
    }

    public OrderItem(OrderItemPK orderItemPK) {
        this.orderItemPK = orderItemPK;
    }

    public OrderItem(OrderItemPK orderItemPK, int quantity, BigDecimal unitPrice) {
        this.orderItemPK = orderItemPK;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderItem(int idOrder, int idProduct) {
        this.orderItemPK = new OrderItemPK(idOrder, idProduct);
    }

    public OrderItemPK getOrderItemPK() {
        return orderItemPK;
    }

    public void setOrderItemPK(OrderItemPK orderItemPK) {
        this.orderItemPK = orderItemPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderItemPK != null ? orderItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderItem)) {
            return false;
        }
        OrderItem other = (OrderItem) object;
        if ((this.orderItemPK == null && other.orderItemPK != null) || (this.orderItemPK != null && !this.orderItemPK.equals(other.orderItemPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.OrderItem[ orderItemPK=" + orderItemPK + " ]";
    }
    
}
