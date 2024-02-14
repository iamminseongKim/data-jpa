package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;
    @Test
    @DisplayName("")
    void testMember() throws Exception {
        //given
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");

        //when
        Member savedMember = memberRepository.save(member);
        Member findMember= memberRepository.findById(savedMember.getId()).get();
        //then

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);   // findMember==member
    }

    @Test
    @DisplayName("CRUD test")
    void basicCRUD() throws Exception {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        //then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    @DisplayName("")
    void findByUsernameAndAgeGreaterThanTest() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        //then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    @DisplayName("")
    void testQuery() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        //when
        List<Member> result = memberRepository.findUser("AAA", 10);
        //then
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    @DisplayName("")
    void findUsernameList() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        //when
        List<String> userNameList = memberRepository.findUserNameList();
        //then
        for (String s : userNameList) {
            System.out.println("s = " + s);
        }
    }
    
    @Test
    @DisplayName("")
    void findMemberDto() throws Exception {
        //given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        //when
        List<MemberDto> memberDto = memberRepository.findMemberDto();
        //then
        for (MemberDto dto : memberDto) {
            System.out.println(dto);
        }
    }


    @Test
    @DisplayName("")
    void findByNamesTest() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        //when
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        //then
        for (Member member : result) {
            System.out.println("Member = " + member );
        }

    }
    
    @Test
    public void returnTypeTest() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        
        //when
        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member aaa1 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA");
        //then

    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));

        // 페이징 해주는 객체
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        int age = 10;

        //when
        //long totalCount = memberRepository.totalCount(age); 이것도 필요 없다. totalCount 도 같이 날린다.
        Page<Member> pages = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> toMap = pages.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> content = pages.getContent();

        assertThat(content.size()).isEqualTo(3);    // 가져온 데이터 수
        assertThat(pages.getTotalElements()).isEqualTo(6);        // 총 데이터 수
        assertThat(pages.getNumber()).isEqualTo(0);     // 시작 번호가 0 인가
        assertThat(pages.getTotalPages()).isEqualTo(2); // 전체 페이지가 2 페이지인가
        assertThat(pages.isFirst()).isTrue();    // 이게 첫 번째 페이지 인가.
        assertThat(pages.hasNext()).isTrue();   // 다음 페이지가 있는가?
    }


    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 12));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 25));
        memberRepository.save(new Member("member5", 30));
        memberRepository.save(new Member("member6", 50));
        //when
        int result = memberRepository.bulkAgePlus(20);

        Member findMember = memberRepository.findById(6L).get();
        System.out.println("findMember = " + findMember);

        //then
        assertThat(result).isEqualTo(4);
    }

    @Test
    public void findMemberLazy() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        em.flush();
        em.clear();
        //when
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

}