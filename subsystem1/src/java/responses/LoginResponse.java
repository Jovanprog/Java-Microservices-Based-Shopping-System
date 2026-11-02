/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package responses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Jovan
 */
@XmlRootElement(name = "loginResponse")
public class LoginResponse {
    private int idUser;
    private String username;

    @XmlElement
    public int getIdUser() { 
        return idUser;
    }
    public void setIdUser(int idUser) { 
        this.idUser = idUser;
    }

    @XmlElement
    public String getUsername() { 
        return username;
    }
    public void setUsername(String username) { 
        this.username = username; 
    }
}
