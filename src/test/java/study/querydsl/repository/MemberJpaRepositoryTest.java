package study.querydsl.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional()
class MemberJpaRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJapRepository;


    @Test
    public void basicTest(){
        Member member = new Member("member1", 10);
        memberJapRepository.save(member);

        Member findMember = memberJapRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberJapRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJapRepository.findByUsername("member1");
        assertThat(result1).containsExactly(member);
    }

    @Test
    public void basicQuerydslTest(){
        Member member = new Member("member1", 10);
        memberJapRepository.save(member);

        Member findMember = memberJapRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberJapRepository.findAll_Querydsl();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJapRepository.findByUsername_Querydsl("member1");
        assertThat(result1).containsExactly(member);
    }

    @Test
    public void searchTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

    List<MemberTeamDto> result = memberJapRepository.searchBuilder(condition);
        assertThat(result).extracting("username").containsExactly("member4");
    }

    @Test
    @Commit
    public void searchWhereTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        //Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        //em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(10);
        condition.setAgeLoe(15); //이게 안먹히는데? member4가 좀 이상했다??
        //condition.setUsername("member4");
        condition.setTeamName("teamA");

        List<MemberTeamDto> result = memberJapRepository.search(condition);
        assertThat(result).extracting("username").containsExactly("member1");
    }


}
