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
public class WishlistItemPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_wishlist")
    private int idWishlist;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_product")
    private int idProduct;

    public WishlistItemPK() {
    }

    public WishlistItemPK(int idWishlist, int idProduct) {
        this.idWishlist = idWishlist;
        this.idProduct = idProduct;
    }

    public int getIdWishlist() {
        return idWishlist;
    }

    public void setIdWishlist(int idWishlist) {
        this.idWishlist = idWishlist;
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
        hash += (int) idWishlist;
        hash += (int) idProduct;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishlistItemPK)) {
            return false;
        }
        WishlistItemPK other = (WishlistItemPK) object;
        if (this.idWishlist != other.idWishlist) {
            return false;
        }
        if (this.idProduct != other.idProduct) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entites.WishlistItemPK[ idWishlist=" + idWishlist + ", idProduct=" + idProduct + " ]";
    }
    
}
