/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entites;

import java.io.Serializable;
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
@Table(name = "cart_item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CartItem.findAll", query = "SELECT c FROM CartItem c"),
    @NamedQuery(name = "CartItem.findByIdCart", query = "SELECT c FROM CartItem c WHERE c.cartItemPK.idCart = :idCart"),
    @NamedQuery(name = "CartItem.findByIdProduct", query = "SELECT c FROM CartItem c WHERE c.cartItemPK.idProduct = :idProduct"),
    @NamedQuery(name = "CartItem.findByQuantity", query = "SELECT c FROM CartItem c WHERE c.quantity = :quantity")})
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CartItemPK cartItemPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private int quantity;
    @JoinColumn(name = "id_cart", referencedColumnName = "id_cart", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Cart cart;
    @JoinColumn(name = "id_product", referencedColumnName = "id_product", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Product product;

    public CartItem() {
    }

    public CartItem(CartItemPK cartItemPK) {
        this.cartItemPK = cartItemPK;
    }

    public CartItem(CartItemPK cartItemPK, int quantity) {
        this.cartItemPK = cartItemPK;
        this.quantity = quantity;
    }

    public CartItem(int idCart, int idProduct) {
        this.cartItemPK = new CartItemPK(idCart, idProduct);
    }

    public CartItemPK getCartItemPK() {
        return cartItemPK;
    }

    public void setCartItemPK(CartItemPK cartItemPK) {
        this.cartItemPK = cartItemPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cartItemPK != null ? cartItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CartItem)) {
            return false;
        }
        CartItem other = (CartItem) object;
        if ((this.cartItemPK == null && other.cartItemPK != null) || (this.cartItemPK != null && !this.cartItemPK.equals(other.cartItemPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entites.CartItem[ cartItemPK=" + cartItemPK + " ]";
    }
    
}
