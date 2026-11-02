/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem3;

import entities.OrderItem;
import entities.OrderItemPK;
import entities.Orders;
import entities.Transactions;
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

        emfactory = Persistence.createEntityManagerFactory("subsystem3PU");

    } catch (Exception e) {
        throw new RuntimeException("Failed to initialize EntityManagerFactory for subsystem3PU", e);
    }
}

    @FunctionalInterface
    private interface JpaOperation {
        String execute(EntityManager em) throws Exception;
    }

    private static String executeTransaction(JpaOperation op) {
        EntityManager em = emfactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            String res = op.execute(em);
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

    //Placanje
    static String pay(int userId, String address, int cityId, List<PayItem> items) {
        if (items == null || items.isEmpty()) {
            return "<error>EMPTY ORDER ITEMS</error>";
        }

        BigDecimal total = BigDecimal.ZERO;
        for (PayItem it : items) {
            BigDecimal price = it.getUnitPrice();
            total = total.add(price.multiply(BigDecimal.valueOf(it.getQuantity())));
        }

        BigDecimal finalTotal = total;

        return executeTransaction(em -> {
            Orders order = new Orders();
            order.setIdUser(userId);
            order.setAddress(address);
            order.setIdCity(cityId);
            order.setCreatedWhen(new Date());
            order.setTotalPrice(finalTotal);
            em.persist(order);
            em.flush();

            for (PayItem it : items) {
                OrderItemPK pk = new OrderItemPK(order.getIdOrder(), it.getProductId());
                OrderItem oi = new OrderItem(pk, it.getQuantity(), it.getUnitPrice());
                oi.setOrders(order);
                em.persist(oi);
            }

            Transactions t = new Transactions();
            t.setIdOrder(order);
            t.setMoneyPaid(finalTotal);
            t.setPaidWhen(new Date());
            em.persist(t);
            em.flush();

            StringBuilder xml = new StringBuilder();
            xml.append("<payResponse>");
            xml.append("<orderId>").append(order.getIdOrder()).append("</orderId>");
            xml.append("<transactionId>").append(t.getIdTransaction()).append("</transactionId>");
            xml.append("<total>").append(finalTotal).append("</total>");
            xml.append("</payResponse>");
            return xml.toString();
        });
    }

    static String getOrdersByUser(int userId) {
        EntityManager em = emfactory.createEntityManager();
        try {
            List<Orders> list = em.createQuery(
                    "SELECT o FROM Orders o WHERE o.idUser = :u", Orders.class)
                    .setParameter("u", userId)
                    .getResultList();

            StringBuilder xml = new StringBuilder();
            xml.append("<orders>");
            for (Orders o : list) {
                xml.append("<order>");
                xml.append("<id>").append(o.getIdOrder()).append("</id>");
                xml.append("<userId>").append(o.getIdUser()).append("</userId>");
                xml.append("<createdWhen>").append(o.getCreatedWhen()).append("</createdWhen>");
                if (o.getTotalPrice() != null) {
                    xml.append("<totalPrice>").append(o.getTotalPrice()).append("</totalPrice>");
                }
                xml.append("<address>").append(o.getAddress()).append("</address>");
                xml.append("<cityId>").append(o.getIdCity()).append("</cityId>");
                xml.append("</order>");
            }
            xml.append("</orders>");
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }

    static String getAllOrders() {
        EntityManager em = emfactory.createEntityManager();
        try {
            List<Orders> list = em.createQuery("SELECT o FROM Orders o", Orders.class)
                    .getResultList();

            StringBuilder xml = new StringBuilder();
            xml.append("<orders>");
            for (Orders o : list) {
                xml.append("<order>");
                xml.append("<id>").append(o.getIdOrder()).append("</id>");
                xml.append("<userId>").append(o.getIdUser()).append("</userId>");
                xml.append("<createdWhen>").append(o.getCreatedWhen()).append("</createdWhen>");
                if (o.getTotalPrice() != null) {
                    xml.append("<totalPrice>").append(o.getTotalPrice()).append("</totalPrice>");
                }
                xml.append("<address>").append(o.getAddress()).append("</address>");
                xml.append("<cityId>").append(o.getIdCity()).append("</cityId>");
                xml.append("</order>");
            }
            xml.append("</orders>");
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }

    //Sve transakcije
    static String getAllTransactions() {
        EntityManager em = emfactory.createEntityManager();
        try {
            List<Transactions> list = em.createQuery(
                    "SELECT t FROM Transactions t", Transactions.class)
                    .getResultList();

            StringBuilder xml = new StringBuilder();
            xml.append("<transactions>");
            for (Transactions t : list) {
                xml.append("<transaction>");
                xml.append("<idTransaction>").append(t.getIdTransaction()).append("</idTransaction>");
                if (t.getMoneyPaid() != null) {
                    xml.append("<moneyPaid>").append(t.getMoneyPaid()).append("</moneyPaid>");
                }
                xml.append("<userId>").append(t.getIdOrder().getIdUser()).append("</userId>");
                xml.append("<paidWhen>").append(t.getPaidWhen()).append("</paidWhen>");
                xml.append("<orderId>").append(t.getIdOrder().getIdOrder()).append("</orderId>");
                xml.append("<createdWhen>").append(t.getPaidWhen()).append("</createdWhen>");
                xml.append("</transaction>");
            }
            xml.append("</transactions>");
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }

    //Pomocna klasa za placanje
    public static class PayItem {
        private final int productId;
        private final int quantity;
        private final BigDecimal unitPrice;

        public PayItem(int productId, int quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public int getProductId() {
            return productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
    }
}
