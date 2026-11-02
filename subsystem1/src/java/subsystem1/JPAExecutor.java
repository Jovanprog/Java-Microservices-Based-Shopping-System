package subsystem1;

import entities.City;
import entities.Roles;
import entities.Users;
import responses.LoginResponse;
import responses.CreateCityResponse;
import responses.CreateUserResponse;
import responses.IncreaseBalanceResponse;
import responses.UpdateUserResponse;
import responses.CitiesResponse;
import responses.UsersResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPAExecutor {
    
    private static final EntityManagerFactory emfactory;
    
    static {
    try {
        emfactory = Persistence.createEntityManagerFactory("subsystem1PU");
    } catch (Exception e) {
        throw new RuntimeException("Failed to initialize JPA EntityManagerFactory for subsystem1PU", e);
    }
}
    
    @FunctionalInterface
    private interface JpaOperation {
        String execute(EntityManager em) throws Exception;
    }
    
    private static String executeTransaction(JpaOperation operation) {
        EntityManager em = emfactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            String res = operation.execute(em);
            transaction.commit();
            return res;
        } catch (Exception e) {
            if (transaction.isActive())
                transaction.rollback();
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }
    
    // ---------------------------UPITI--------------------------
    
    static String login(String username, String password) {
        EntityManager em = emfactory.createEntityManager();
        try {
            Users user = em.createQuery(
                    "SELECT u FROM Users u WHERE u.username=:username AND u.password=:password",
                    Users.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();

            LoginResponse resp = new LoginResponse();
            resp.setIdUser(user.getIdUser());
            resp.setUsername(user.getUsername());

            String roleName = "normal_user";
            if (user.getRolesList() != null && !user.getRolesList().isEmpty()) {
                roleName = user.getRolesList().get(0).getName();
            }

            StringBuilder xml = new StringBuilder();
            xml.append("<loginResponse>");
            xml.append("<idUser>").append(resp.getIdUser()).append("</idUser>");
            xml.append("<username>").append(resp.getUsername()).append("</username>");
            xml.append("<role>").append(roleName).append("</role>");
            xml.append("</loginResponse>");
            return xml.toString();

        } catch (Exception e) {
            return "<error>INVALID LOGIN</error>";
        } finally {
            em.close();
        }
    }

    static String createCity(String name) {
        return executeTransaction(em -> {
            City c = new City();
            c.setName(name);
            System.out.println(c);
            em.persist(c);
            em.flush();
            
            CreateCityResponse resp = new CreateCityResponse();
            resp.setIdCity(c.getIdCity());
            
            StringBuilder xml = new StringBuilder();
            xml.append("<createCityResponse>");
            xml.append("<idCity>").append(resp.getIdCity()).append("</idCity>");
            xml.append("</createCityResponse>");
            return xml.toString();
        });
    }
    
    
    static String createUser(String username, String password, String firstName, String lastName, String role, double balance, String address, int cityId) {
        if (balance < 0.0)
            return "<error>NEGATIVE BALANCE</error>";
        
        return executeTransaction(em -> {
            City city = em.find(City.class, cityId);
            if (city == null)
                return "<error>CITY NOT FOUND</error>";
            
            
            List<Roles> roles = em.createQuery(
                    "SELECT r FROM Roles r WHERE r.name=:name",
                    Roles.class)
                    .setParameter("name", role)
                    .getResultList();
            
            
            if(roles.isEmpty()){
                return "<error>ROLE NOT FOUND</error>";
            }
            Users user = new Users();
            user.setUsername(username);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRolesList(new ArrayList<>());
            user.addRole(roles.get(0));
            user.setAddress(address);
            user.setBalance(BigDecimal.valueOf(balance));
            user.setIdCity(city);

            em.persist(user);
            em.flush();
            
            CreateUserResponse resp = new CreateUserResponse();
            resp.setIdUser(user.getIdUser());
            
            StringBuilder xml = new StringBuilder();
            xml.append("<createUserResponse>");
            xml.append("<idUser>").append(resp.getIdUser()).append("</idUser>");
            xml.append("</createUserResponse>");
            return xml.toString();
        });
    }
    
    static String increaseBalance(int idUser, double amount) {
        if (amount < 0.0)
            return "<error>NEGATIVE AMOUNT</error>";

        return executeTransaction(em -> {
            Users user = em.createQuery(
                    "SELECT u FROM Users u WHERE u.idUser=:idUser",
                    Users.class)
                    .setParameter("idUser", idUser)
                    .getSingleResult();
            if(user == null){
                    return "<error>USER NOT FOUND</error>";
            }
            if (user.getBalance() == null)
                user.setBalance(BigDecimal.ZERO);
            
            BigDecimal newBalance = user.getBalance().add(BigDecimal.valueOf(amount));
            user.setBalance(newBalance);
            
            IncreaseBalanceResponse resp = new IncreaseBalanceResponse();
            resp.setNewBalance(newBalance);
            
            StringBuilder xml = new StringBuilder();
            xml.append("<increaseBalanceResponse>");
            xml.append("<idUser>").append(idUser).append("</idUser>");
            xml.append("<newBalance>").append(resp.getNewBalance().toString()).append("</newBalance>");
            xml.append("</increaseBalanceResponse>");
            return xml.toString();
        });
    }
    
    static String decreaseBalance(int userId, double amount) {
        if (amount < 0.0)
            return "<error>NEGATIVE AMOUNT</error>";

        return executeTransaction(em -> {
            Users user = em.find(Users.class, userId);
            if(user == null){
                    return "<error>USER NOT FOUND</error>";
            }

            if (user.getBalance() == null)
                user.setBalance(BigDecimal.ZERO);
            
            BigDecimal amountBD = BigDecimal.valueOf(amount);
            if(user.getBalance().compareTo(amountBD) < 0){
                return "<error>INSUFFICIENT BALANCE</error>";
            }
            
            BigDecimal newBalance = user.getBalance().subtract(amountBD);
            user.setBalance(newBalance);
            
            StringBuilder xml = new StringBuilder();
            xml.append("<decreaseBalanceResponse>");
            xml.append("<newBalance>").append(newBalance.toString()).append("</newBalance>");
            xml.append("</decreaseBalanceResponse>");
            return xml.toString();
        });
    }
    
    static String getBalance(int userId){
        EntityManager em = emfactory.createEntityManager();
        try {
            Users user = em.find(Users.class, userId);
            if(user == null){
                    return "<error>USER NOT FOUND</error>";
            }
            
            StringBuilder xml = new StringBuilder();
            xml.append("<balanceResponse>");
            xml.append("<balance>").append(user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO).append("</balance>");
            xml.append("</balanceResponse>");
            return xml.toString();
        } catch (Exception e){
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }
    
    static String updateAddressAndCity(int idUser, String address, int cityId) {
        return executeTransaction(em -> {
            City city = em.find(City.class, cityId);
            if (city == null) {
                return "<error>INVALID CITY</error>";
            }

            int updated = em.createQuery(
                    "UPDATE Users u SET u.address = :address, u.idCity = :city WHERE u.idUser = :idUser")
                    .setParameter("address", address)
                    .setParameter("city", city)
                    .setParameter("idUser", idUser)
                    .executeUpdate();

            if (updated == 0) {
                return "<error>USER NOT FOUND</error>";
            }

            UpdateUserResponse resp = new UpdateUserResponse();
            resp.setStatus("SUCCESS");
            
            StringBuilder xml = new StringBuilder();
            xml.append("<updateUserResponse>");
            xml.append("<idUser>").append(idUser).append("</idUser>");
            xml.append("<newAddress>").append(address).append("</newAddress>");
            xml.append("<newCity>").append(city.getName()).append("</newCity>");
            xml.append("</updateUserResponse>");
            return xml.toString();
        });
    }
    
    static String getAllCities() {
        EntityManager em = emfactory.createEntityManager();
        try {
            List<City> list = em.createQuery("SELECT c FROM City c", City.class).getResultList();
            
            CitiesResponse resp = new CitiesResponse();
            resp.setCities(list);
            
            StringBuilder xml = new StringBuilder();
            //xml.append("<cities>");
            for (City c : resp.getCities()) {
                xml.append("<city>");
                xml.append("<id>").append(c.getIdCity()).append("</id>");
                xml.append("<name>").append(c.getName()).append("</name>");
                xml.append("</city>");
            }
            //xml.append("</cities>");
            
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }

    static String getAllUsers() {
        EntityManager em = emfactory.createEntityManager();
        try {
            List<Users> list = em.createQuery("SELECT u FROM Users u", Users.class).getResultList();
            
            UsersResponse resp = new UsersResponse();
            resp.setUsers(list);
            
            StringBuilder xml = new StringBuilder();
            xml.append("<users>");
            for (Users u : resp.getUsers()) {
                xml.append("<user>");
                xml.append("<idUser>").append(u.getIdUser()).append("</idUser>");
                xml.append("<username>").append(u.getUsername()).append("</username>");
                
                if (u.getAddress() != null && !u.getAddress().isEmpty())
                    xml.append("<firstName>").append(u.getFirstName()).append("</firstName>");
                
                if (u.getAddress() != null && !u.getAddress().isEmpty())
                    xml.append("<lastName>").append(u.getLastName()).append("</lastName>");
                
                xml.append("<roles>");
                boolean not_first = false;
                for(Roles r : u.getRolesList()){
                    if(not_first){
                        xml.append(",");
                    }
                    xml.append(r.getName());
                    not_first = true;
                }
                xml.append("</roles>");
                
                if (u.getAddress() != null && !u.getAddress().isEmpty())
                    xml.append("<address>").append(u.getAddress()).append("</address>");
                
                if (u.getIdCity() != null)
                    xml.append("<city>").append(u.getIdCity().getName()).append("</city>");
                
                if (u.getBalance() != null)
                    xml.append("<balance>").append(u.getBalance()).append("</balance>");
                
                xml.append("</user>");
            }
            xml.append("</users>");
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }
    
    static String cityExists(int cityId) {
        EntityManager em = emfactory.createEntityManager();
        try {
            City city = em.find(City.class, cityId);
            if (city == null)
                return "<error>CITY NOT FOUND</error>";
            StringBuilder xml = new StringBuilder();
            xml.append("<cityResponse>");
            xml.append("<id>").append(city.getIdCity()).append("</id>");
            xml.append("<name>").append(city.getName()).append("</name>");
            xml.append("</cityResponse>");
            return xml.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        } finally {
            em.close();
        }
    }
}
