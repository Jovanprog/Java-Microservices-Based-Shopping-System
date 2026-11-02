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
@XmlRootElement(name = "createCityResponse")
public class CreateCityResponse {
    private int idCity;

    @XmlElement
    public int getIdCity() { 
        return idCity;
    }
    public void setIdCity(int idCity) { 
        this.idCity = idCity;
    }
}
