package responses;

import entites.CartItem;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cartItems")
public class CartItemsResponse {

    private List<CartItem> items;

    @XmlElement(name = "item")
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}

