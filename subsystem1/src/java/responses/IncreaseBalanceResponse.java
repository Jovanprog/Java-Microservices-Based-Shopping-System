/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package responses;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;
/**
 *
 * @author Jovan
 */
@XmlRootElement(name = "increaseBalanceResponse")
public class IncreaseBalanceResponse {
    private BigDecimal newBalance;

    @XmlElement
    public BigDecimal getNewBalance() { 
        return newBalance;
    }
    public void setNewBalance(BigDecimal newBalance) { 
        this.newBalance = newBalance;
    }
}
