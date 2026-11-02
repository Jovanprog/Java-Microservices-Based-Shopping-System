/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entites;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author WDAGUtilityAccount
 */
@Entity
@Table(name = "wishlist")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Wishlist.findAll", query = "SELECT w FROM Wishlist w"),
    @NamedQuery(name = "Wishlist.findByIdWishlist", query = "SELECT w FROM Wishlist w WHERE w.idWishlist = :idWishlist"),
    @NamedQuery(name = "Wishlist.findByIdUser", query = "SELECT w FROM Wishlist w WHERE w.idUser = :idUser"),
    @NamedQuery(name = "Wishlist.findByCreationTime", query = "SELECT w FROM Wishlist w WHERE w.creationTime = :creationTime")})
public class Wishlist implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_wishlist")
    private Integer idWishlist;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_user")
    private int idUser;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wishlist")
    private List<WishlistItem> wishlistItemList;

    public Wishlist() {
    }

    public Wishlist(Integer idWishlist) {
        this.idWishlist = idWishlist;
    }

    public Wishlist(Integer idWishlist, int idUser, Date creationTime) {
        this.idWishlist = idWishlist;
        this.idUser = idUser;
        this.creationTime = creationTime;
    }

    public Integer getIdWishlist() {
        return idWishlist;
    }

    public void setIdWishlist(Integer idWishlist) {
        this.idWishlist = idWishlist;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    @XmlTransient
    public List<WishlistItem> getWishlistItemList() {
        return wishlistItemList;
    }

    public void setWishlistItemList(List<WishlistItem> wishlistItemList) {
        this.wishlistItemList = wishlistItemList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idWishlist != null ? idWishlist.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Wishlist)) {
            return false;
        }
        Wishlist other = (Wishlist) object;
        if ((this.idWishlist == null && other.idWishlist != null) || (this.idWishlist != null && !this.idWishlist.equals(other.idWishlist))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entites.Wishlist[ idWishlist=" + idWishlist + " ]";
    }
    
}
