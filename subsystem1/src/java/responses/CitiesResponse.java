/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package responses;

import entities.City;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
/**
 *
 * @author Jovan
 */
@XmlRootElement(name = "cities")
public class CitiesResponse {
    private List<City> cities;

    @XmlElement(name = "city")
    public List<City> getCities() { 
        return cities;
    }
    public void setCities(List<City> cities) { 
        this.cities = cities;
    }
}
