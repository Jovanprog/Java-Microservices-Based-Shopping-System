package responses;

import entites.WishlistItem;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "wishlistItems")
public class WishlistItemsResponse {

    private List<WishlistItem> items;

    @XmlElement(name = "item")
    public List<WishlistItem> getItems() {
        return items;
    }

    public void setItems(List<WishlistItem> items) {
        this.items = items;
    }
}

