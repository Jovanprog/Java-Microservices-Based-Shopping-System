/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem2;

import entites.Cart;
import entites.CartItem;
import entites.CartItemPK;
import entites.Category;
import entites.Product;
import entites.Wishlist;
import entites.WishlistItem;
import entites.WishlistItemPK;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPAExecutor {

    private static final EntityManagerFactory emfactory;

    static {
    try {

        emfactory = Persistence.createEntityManagerFactory("subsystem2PU");

    } catch (Exception e) {
        throw new RuntimeException("Failed to initialize EntityManagerFactory for subsystem2PU", e);
    }
}

    @FunctionalInterface
    private interface JpaOperation {
        String execute(EntityManager em) throws Exception;
    }

    private static String executeTransaction(JpaOperation operation) {
        EntityManager em = emfactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            String res = operation.execute(em);
            tx.commit();
            return res;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }

    static String createCategory(String name, Integer parentId) {
        return executeTransaction(em -> {
            Category c = new Category();
            c.setName(name);

            if (parentId != null) {
                Category parent = em.find(Category.class, parentId);
                if (parent == null) {
                    return "<error>INVALID PARENT CATEGORY</error>";
                }
                c.setIdParentCategory(parent);
            }

            em.persist(c);
            em.flush();

            StringBuilder xml = new StringBuilder();
            xml.append("<createCategoryResponse>");
            xml.append("<idCategory>").append(c.getIdCategory()).append("</idCategory>");
            xml.append("</createCategoryResponse>");
            return xml.toString();
        });
    }

    static String createProduct(String name, String description, double price, int discount, int sellerId, int categoryId) {
        if (price < 0.0)
            return "<error>NEGATIVE PRICE</error>";
        if (discount < 0)
            return "<error>NEGATIVE DISCOUNT</error>";
        
        return executeTransaction(em -> {
            Category category = em.find(Category.class, categoryId);
            if (category == null) {
                return "<error>INVALID CATEGORY</error>";
            }

            Product p = new Product();
            p.setName(name);
            p.setDescription(description);
            p.setPrice(BigDecimal.valueOf(price));
            p.setDiscount(0);
            p.setIdUser(sellerId);

            em.persist(p);
            em.flush();
            p.setNewCategory(category);

            StringBuilder xml = new StringBuilder();
            xml.append("<createProductResponse>");
            xml.append("<idProduct>").append(p.getIdProduct()).append("</idProduct>");
            xml.append("</createProductResponse>");
            return xml.toString();
        });
    }

    static String updateProductPrice(int idProduct, double price, int sellerId) {
        if (price < 0.0)
            return "<error>NEGATIVE PRICE</error>";
        
        return executeTransaction(em -> {
            Product product = em.find(Product.class, idProduct);
            if (product == null) {
                return "<error>INVALID PRODUCT</error>";
            }
            if (product.getIdUser() != sellerId) {
                return "<error>ONLY THE OWNER CAN CHANGE THE PRICE.</error>";
            }

            product.setPrice(BigDecimal.valueOf(price));

            StringBuilder xml = new StringBuilder();
            xml.append("<updateProductPriceResponse>");
            xml.append("<newPrice>").append(price).append("</newPrice>");
            xml.append("</updateProductPriceResponse>");
            return xml.toString();
        });
    }

    static String setDiscount(int idProduct, int discount, int sellerId) {
        if (discount < 0)
            return "<error>NEGATIVE DISCOUNT</error>";
        
        return executeTransaction(em -> {
            Product product = em.find(Product.class, idProduct);
            if (product == null) {
                return "<error>INVALID PRODUCT</error>";
            }
            if (product.getIdUser() != sellerId) {
                return "<error>ONLY THE OWNER CAN SET THE DISCOUNT.</error>";
            }

            product.setDiscount(discount);

            StringBuilder xml = new StringBuilder();
            xml.append("<setDiscountResponse>");
            xml.append("<newDiscount>").append(discount).append("</newDiscount>");
            xml.append("</setDiscountResponse>");
            return xml.toString();
        });
    }

    private static Cart getOrCreateCart(EntityManager em, int userId) {
        List<Cart> carts = em.createQuery(
                "SELECT c FROM Cart c WHERE c.idUser = :u", Cart.class)
                .setParameter("u", userId)
                .getResultList();
        if (carts.isEmpty()) {
            Cart cart = new Cart();
            cart.setIdUser(userId);
            cart.setTotalPrice(BigDecimal.ZERO);
            em.persist(cart);
            em.flush();
            return cart;
        }
        return carts.get(0);
    }

    private static Wishlist getOrCreateWishlist(EntityManager em, int userId) {
        List<Wishlist> list = em.createQuery(
                "SELECT w FROM Wishlist w WHERE w.idUser = :u", Wishlist.class)
                .setParameter("u", userId)
                .getResultList();
        if (list.isEmpty()) {
            Wishlist w = new Wishlist();
            w.setIdUser(userId);
            w.setCreationTime(new Date());
            em.persist(w);
            em.flush();
            return w;
        }
        return list.get(0);
    }

    static String addToCart(int userId, int productId, int quantity) {
        if (quantity <= 0) {
            return "<error>INVALID QUANTITY</error>";
        }

        return executeTransaction(em -> {
            Product product = em.find(Product.class, productId);
            if (product == null) {
                return "<error>PRODUCT NOT FOUND</error>";
            }

            Cart cart = getOrCreateCart(em, userId);

            CartItemPK pk = new CartItemPK(cart.getIdCart(), productId);
            CartItem item = em.find(CartItem.class, pk);
            if (item == null) {
                item = new CartItem(pk, quantity);
                item.setCart(cart);
                item.setProduct(product);
                em.persist(item);
            } else {
                item.setQuantity(item.getQuantity() + quantity);
            }

            StringBuilder xml = new StringBuilder();
            xml.append("<addToCartResponse>");
            xml.append("<productId>").append(productId).append("</productId>");
            xml.append("<quantity>").append(quantity).append("</quantity>");
            xml.append("</addToCartResponse>");
            return xml.toString();
        });
    }

    static String removeFromCart(int userId, int productId, int quantity) {
        if (quantity <= 0) {
            return "<error>INVALID QUANTITY</error>";
        }

        return executeTransaction(em -> {
            List<Cart> carts = em.createQuery(
                    "SELECT c FROM Cart c WHERE c.idUser = :u", Cart.class)
                    .setParameter("u", userId)
                    .getResultList();
            if (carts.isEmpty()) {
                return "<error>CART NOT FOUND</error>";
            }
            Cart cart = carts.get(0);

            CartItemPK pk = new CartItemPK(cart.getIdCart(), productId);
            CartItem item = em.find(CartItem.class, pk);
            if (item == null) {
                return "<error>ITEM NOT FOUND</error>";
            }
            
            int newQuantity = item.getQuantity();
            if (item.getQuantity() <= quantity) {
                em.remove(item);
                newQuantity = 0;
            } else {
                item.setQuantity(item.getQuantity() - quantity);
                newQuantity = item.getQuantity();
            }

            StringBuilder xml = new StringBuilder();
            xml.append("<removeFromCartResponse>");
            xml.append("<productId>").append(item.getProduct().getIdProduct()).append("</productId>");
            xml.append("<quantity>").append(newQuantity).append("</quantity>");
            xml.append("</removeFromCartResponse>");
            return xml.toString();
        });
    }
    
    static String clearCart(int userId){
        return executeTransaction(em -> {
            Cart cart = em.createQuery(
                    "SELECT c FROM Cart c WHERE c.idUser = :u", Cart.class)
                    .setParameter("u", userId)
                    .getSingleResult();
            if(cart == null){
                return "<clearCartResponse><status>ALREADY EMPTY</status></clearCartResponse>";
            }
            
            List<CartItem> items = em.createQuery(
                    "SELECT ci FROM CartItem ci WHERE ci.cart.idCart = :cid", CartItem.class)
                    .setParameter("cid", cart.getIdCart())
                    .getResultList();
            for(CartItem ci : items){
                em.remove(ci);
            }
            
            return "<clearCartResponse><status>SUCCESS</status></clearCartResponse>";
        });
    }

    static String addToWishlist(int userId, int productId) {
        return executeTransaction(em -> {
            Product product = em.find(Product.class, productId);
            if (product == null) {
                return "<error>PRODUCT NOT FOUND</error>";
            }

            Wishlist wishlist = getOrCreateWishlist(em, userId);

            WishlistItemPK pk = new WishlistItemPK(wishlist.getIdWishlist(), productId);
            WishlistItem item = em.find(WishlistItem.class, pk);
            if (item == null) {
                item = new WishlistItem(pk, new Date());
                item.setWishlist(wishlist);
                item.setProduct(product);
                em.persist(item);
                em.flush();
            }

            StringBuilder xml = new StringBuilder();
            xml.append("<addToWishlistResponse>");
            xml.append("<id>").append(item.getProduct().getIdProduct()).append("</id>");
            xml.append("</addToWishlistResponse>");
            return xml.toString();
        });
    }

    // 13. Brisanje artikla iz liste želja
    static String removeFromWishlist(int userId, int productId) {
        return executeTransaction(em -> {
            List<Wishlist> list = em.createQuery(
                    "SELECT w FROM Wishlist w WHERE w.idUser = :u", Wishlist.class)
                    .setParameter("u", userId)
                    .getResultList();
            if (list.isEmpty()) {
                return "<error>WISHLIST NOT FOUND</error>";
            }
            Wishlist wishlist = list.get(0);

            WishlistItemPK pk = new WishlistItemPK(wishlist.getIdWishlist(), productId);
            WishlistItem item = em.find(WishlistItem.class, pk);
            if (item == null) {
                return "<error>ITEM NOT FOUND</error>";
            }

            em.remove(item);

            StringBuilder xml = new StringBuilder();
            xml.append("<removeFromWishlistResponse>");
            xml.append("<id>").append(item.getProduct().getIdProduct()).append("</id>");
            xml.append("</removeFromWishlistResponse>");
            return xml.toString();
        });
    }

    // 17. Dohvatanje svih kategorija
    static String getAllCategories() {
        EntityManager em = emfactory.createEntityManager();
        try {
            List<Category> list = em.createQuery("SELECT c FROM Category c", Category.class)
                    .getResultList();

            StringBuilder xml = new StringBuilder();
            xml.append("<categories>");
            for (Category c : list) {
                xml.append("<category>");
                xml.append("<id>").append(c.getIdCategory()).append("</id>");
                xml.append("<name>").append(c.getName()).append("</name>");
                if (c.getIdParentCategory() != null) {
                    xml.append("<parentId>").append(c.getIdParentCategory().getIdCategory()).append("</parentId>");
                }
                xml.append("</category>");
            }
            xml.append("</categories>");
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }

    // 18. Dohvatanje artikala koje prodaje korisnik
    static String getProductsBySeller(int sellerId) {
        EntityManager em = emfactory.createEntityManager();
        try {
            List<Product> list = em.createQuery(
                    "SELECT p FROM Product p WHERE p.idUser = :u", Product.class)
                    .setParameter("u", sellerId)
                    .getResultList();

            StringBuilder xml = new StringBuilder();
            xml.append("<products>");
            for (Product p : list) {
                xml.append("<product>");
                xml.append("<id>").append(p.getIdProduct()).append("</id>");
                xml.append("<name>").append(p.getName()).append("</name>");
                if (p.getDescription() != null) {
                    xml.append("<description>").append(p.getDescription()).append("</description>");
                }
                xml.append("<price>").append(p.getPrice()).append("</price>");
                if (p.getDiscount() != null) {
                    xml.append("<discount>").append(p.getDiscount()).append("</discount>");
                }
                
                xml.append("<sellerId>").append(p.getIdUser()).append("</sellerId>");
                
                xml.append("<categoryName>");
                boolean not_first = false;
                for(Category c : p.getCategoryList()){
                    if(not_first){
                        xml.append(",");
                    }
                    xml.append(c.getName());
                    not_first = true;
                }
                xml.append("</categoryName>");
                
                xml.append("</product>");
            }
            xml.append("</products>");
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }

    // 19. Dohvatanje sadržaja korpe
    static String getCart(int userId) {
        EntityManager em = emfactory.createEntityManager();
        try {
            List<CartItem> items = em.createQuery(
                    "SELECT ci FROM CartItem ci WHERE ci.cart.idUser = :u", CartItem.class)
                    .setParameter("u", userId)
                    .getResultList();

            StringBuilder xml = new StringBuilder();
            xml.append("<cartItems>");
            for (CartItem ci : items) {
                xml.append("<item>");
                xml.append("<productId>").append(ci.getProduct().getIdProduct()).append("</productId>");
                xml.append("<productName>").append(ci.getProduct().getName()).append("</productName>");
                xml.append("<price>").append(ci.getProduct().getPrice()).append("</price>");
                xml.append("<discount>").append(ci.getProduct().getDiscount()).append("</discount>");
                xml.append("<quantity>").append(ci.getQuantity()).append("</quantity>");
                xml.append("</item>");
            }
            xml.append("</cartItems>");
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }

    // 20. Dohvatanje sadržaja liste želja
    static String getWishlist(int userId) {
        EntityManager em = emfactory.createEntityManager();
        try {
            List<WishlistItem> items = em.createQuery(
                    "SELECT wi FROM WishlistItem wi WHERE wi.wishlist.idUser = :u", WishlistItem.class)
                    .setParameter("u", userId)
                    .getResultList();

            StringBuilder xml = new StringBuilder();
            xml.append("<wishlistItems>");
            for (WishlistItem wi : items) {
                xml.append("<item>");
                xml.append("<productId>").append(wi.getProduct().getIdProduct()).append("</productId>");
                xml.append("<productName>").append(wi.getProduct().getName()).append("</productName>");
                xml.append("<addedWhen>").append(wi.getAdditionTime()).append("</addedWhen>");
                xml.append("<price>").append(wi.getProduct().getPrice()).append("</price>");
                xml.append("</item>");
            }
            xml.append("</wishlistItems>");
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }
}
