/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
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
@Table(name = "transactions")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Transactions.findAll", query = "SELECT t FROM Transactions t"),
    @NamedQuery(name = "Transactions.findByIdTransaction", query = "SELECT t FROM Transactions t WHERE t.idTransaction = :idTransaction"),
    @NamedQuery(name = "Transactions.findByMoneyPaid", query = "SELECT t FROM Transactions t WHERE t.moneyPaid = :moneyPaid"),
    @NamedQuery(name = "Transactions.findByPaidWhen", query = "SELECT t FROM Transactions t WHERE t.paidWhen = :paidWhen")})
public class Transactions implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_transaction")
    private Integer idTransaction;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "money_paid")
    private BigDecimal moneyPaid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "paid_when")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paidWhen;
    @JoinColumn(name = "id_order", referencedColumnName = "id_order")
    @OneToOne(optional = false)
    private Orders idOrder;

    public Transactions() {
    }

    public Transactions(Integer idTransaction) {
        this.idTransaction = idTransaction;
    }

    public Transactions(Integer idTransaction, Date paidWhen) {
        this.idTransaction = idTransaction;
        this.paidWhen = paidWhen;
    }

    public Integer getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Integer idTransaction) {
        this.idTransaction = idTransaction;
    }

    public BigDecimal getMoneyPaid() {
        return moneyPaid;
    }

    public void setMoneyPaid(BigDecimal moneyPaid) {
        this.moneyPaid = moneyPaid;
    }

    public Date getPaidWhen() {
        return paidWhen;
    }

    public void setPaidWhen(Date paidWhen) {
        this.paidWhen = paidWhen;
    }

    public Orders getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Orders idOrder) {
        this.idOrder = idOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTransaction != null ? idTransaction.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transactions)) {
            return false;
        }
        Transactions other = (Transactions) object;
        if ((this.idTransaction == null && other.idTransaction != null) || (this.idTransaction != null && !this.idTransaction.equals(other.idTransaction))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Transactions[ idTransaction=" + idTransaction + " ]";
    }
    
}
