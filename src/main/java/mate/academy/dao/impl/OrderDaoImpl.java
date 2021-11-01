package mate.academy.dao.impl;

import java.util.List;
import mate.academy.dao.OrderDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.Order;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class OrderDaoImpl implements OrderDao {
    @Override
    public Order add(Order order) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(order);
            transaction.commit();
            return order;
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Couldn't add new order: " + order, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Order> getByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> query = session.createQuery("FROM Order o "
                    + "LEFT JOIN FETCH o.tickets t "
                    + "LEFT JOIN FETCH t.movieSession ms "
                    + "LEFT JOIN FETCH t.user "
                    + "LEFT JOIN FETCH ms.cinemaHall ch "
                    + "LEFT JOIN FETCH ms.movie m "
                    + "WHERE t.user = :user", Order.class);
            query.setParameter("user", user);
            return query.getResultList();
        } catch (RuntimeException e) {
            throw new DataProcessingException("Couldn't find orders by user: " + user, e);
        }
    }
}