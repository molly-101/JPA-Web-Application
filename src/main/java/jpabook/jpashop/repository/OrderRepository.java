package jpabook.jpashop.repository;

import jpabook.jpashop.api.OrderSimpleApiController;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    //동적 쿼리가 들어가게됩니다. -> 원하는 로직: 검색하면 그거 맞춰서 order list 를 가져와주는 메서드
    public List<Order> findAll(OrderSearch orderSearch){
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            } else{
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like : name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);// 최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "SELECT o FROM Order o" + " JOIN FETCH o.member m" + " JOIN FETCH o.delivery d", Order.class
        ).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "SELECT o FROM Order o" +
                        " JOIN FETCH o.member m" +
                        " JOIN FETCH o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery(
                "SELECT new jpabook.jpashop.repository.SimpleOrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                        "FROM Order o" +
                        " JOIN o.member m" +
                        " JOIN o.delivery d", SimpleOrderQueryDto.class)
                        .getResultList();
    }

    public List<Order> findAllWithItem() {
        return em.createQuery(
                "SELECT DISTINCT o FROM Order o" +
                        " JOIN FETCH o.member m" +
                        " JOIN FETCH o.delivery d" +
                        " JOIN FETCH o.orderItems oi" +
                        " JOIN FETCH oi.item i", Order.class).getResultList();
    }
}
