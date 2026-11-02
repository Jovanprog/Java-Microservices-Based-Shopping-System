/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author WDAGUtilityAccount
 */
@Embeddable
public class OrderItemPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_order")
    private int idOrder;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_product")
    private int idProduct;

    public OrderItemPK() {
    }

    public OrderItemPK(int idOrder, int idProduct) {
        this.idOrder = idOrder;
        this.idProduct = idProduct;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idOrder;
        hash += (int) idProduct;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderItemPK)) {
            return false;
        }
        OrderItemPK other = (OrderItemPK) object;
        if (this.idOrder != other.idOrder) {
            return false;
        }
        if (this.idProduct != other.idProduct) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.OrderItemPK[ idOrder=" + idOrder + ", idProduct=" + idProduct + " ]";
    }
    
}
