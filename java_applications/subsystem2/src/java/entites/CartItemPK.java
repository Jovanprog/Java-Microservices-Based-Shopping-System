/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entites;

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
public class CartItemPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_cart")
    private int idCart;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_product")
    private int idProduct;

    public CartItemPK() {
    }

    public CartItemPK(int idCart, int idProduct) {
        this.idCart = idCart;
        this.idProduct = idProduct;
    }

    public int getIdCart() {
        return idCart;
    }

    public void setIdCart(int idCart) {
        this.idCart = idCart;
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
        hash += (int) idCart;
        hash += (int) idProduct;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CartItemPK)) {
            return false;
        }
        CartItemPK other = (CartItemPK) object;
        if (this.idCart != other.idCart) {
            return false;
        }
        if (this.idProduct != other.idProduct) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entites.CartItemPK[ idCart=" + idCart + ", idProduct=" + idProduct + " ]";
    }
    
}
