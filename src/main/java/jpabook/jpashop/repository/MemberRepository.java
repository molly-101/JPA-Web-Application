package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // 원래는 @PersistenceContext 어노테이션이 있어야 하지만 SpringBoot 는 생성자 주입으로도 해준다
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    // 특정 멤버 하나만 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // 모든 멤버 조회
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    // 이름으로 멤버 조회
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
