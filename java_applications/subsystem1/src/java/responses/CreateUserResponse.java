/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package responses;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
/**
 *
 * @author Jovan
 */
@XmlRootElement(name = "createUserResponse")
public class CreateUserResponse {
    private int idUser;

    @XmlElement
    public int getIdUser() { 
        return idUser;
    }
    public void setIdUser(int idUser) { 
        this.idUser = idUser;
    }
}
