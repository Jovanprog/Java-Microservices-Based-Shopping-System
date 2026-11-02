/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entites;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author WDAGUtilityAccount
 */
@Entity
@Table(name = "wishlist_item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WishlistItem.findAll", query = "SELECT w FROM WishlistItem w"),
    @NamedQuery(name = "WishlistItem.findByIdWishlist", query = "SELECT w FROM WishlistItem w WHERE w.wishlistItemPK.idWishlist = :idWishlist"),
    @NamedQuery(name = "WishlistItem.findByIdProduct", query = "SELECT w FROM WishlistItem w WHERE w.wishlistItemPK.idProduct = :idProduct"),
    @NamedQuery(name = "WishlistItem.findByAdditionTime", query = "SELECT w FROM WishlistItem w WHERE w.additionTime = :additionTime")})
public class WishlistItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WishlistItemPK wishlistItemPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "addition_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date additionTime;
    @JoinColumn(name = "id_product", referencedColumnName = "id_product", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Product product;
    @JoinColumn(name = "id_wishlist", referencedColumnName = "id_wishlist", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Wishlist wishlist;

    public WishlistItem() {
    }

    public WishlistItem(WishlistItemPK wishlistItemPK) {
        this.wishlistItemPK = wishlistItemPK;
    }

    public WishlistItem(WishlistItemPK wishlistItemPK, Date additionTime) {
        this.wishlistItemPK = wishlistItemPK;
        this.additionTime = additionTime;
    }

    public WishlistItem(int idWishlist, int idProduct) {
        this.wishlistItemPK = new WishlistItemPK(idWishlist, idProduct);
    }

    public WishlistItemPK getWishlistItemPK() {
        return wishlistItemPK;
    }

    public void setWishlistItemPK(WishlistItemPK wishlistItemPK) {
        this.wishlistItemPK = wishlistItemPK;
    }

    public Date getAdditionTime() {
        return additionTime;
    }

    public void setAdditionTime(Date additionTime) {
        this.additionTime = additionTime;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (wishlistItemPK != null ? wishlistItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WishlistItem)) {
            return false;
        }
        WishlistItem other = (WishlistItem) object;
        if ((this.wishlistItemPK == null && other.wishlistItemPK != null) || (this.wishlistItemPK != null && !this.wishlistItemPK.equals(other.wishlistItemPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entites.WishlistItem[ wishlistItemPK=" + wishlistItemPK + " ]";
    }
    
}
