/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package responses;

import entities.Users;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
/**
 *
 * @author Jovan
 */
@XmlRootElement(name = "users")
public class UsersResponse {
    private List<Users> users;

    @XmlElement(name = "user")
    public List<Users> getUsers() { 
        return users;
    }
    public void setUsers(List<Users> users) { 
        this.users = users;
    }
}
