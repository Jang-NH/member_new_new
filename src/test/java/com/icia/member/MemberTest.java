package com.icia.member;

import com.icia.member.dto.MemberDetailDTO;
import com.icia.member.dto.MemberMapperDTO;
import com.icia.member.dto.MemberSaveDTO;
import com.icia.member.repository.MemberMapperRepository;
import com.icia.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

@SpringBootTest
public class MemberTest {

    @Autowired
    private MemberService ms;

    @Autowired
    private MemberMapperRepository mmr;

    @Test
    @DisplayName("회원데이터생성")
    public void newMembers() {
        IntStream.rangeClosed(1, 15).forEach(i -> {
            ms.save(new MemberSaveDTO("email" + i,"pw" + i, "name" + i));
        });
    }

    @Test
    @DisplayName("회원데이터삭제")
    public void deleteMember() {
        // 회원 생성
        MemberSaveDTO memberSaveDTO = new MemberSaveDTO("삭제용회원이메일", "삭제용회원비밀번호", "삭제용회원이름");
        Long memberId = ms.save(memberSaveDTO);
        // 회원 조회
        System.out.println(ms.findById(memberId));
        // 회원 삭제
        ms.deleteById(memberId);
        // 삭제한 회원 조회되는지 (NoSuchElementException 예외 발생)
        System.out.println(ms.findById(memberId));

        // 삭제한 회원의 id로 조회를 시도했을 때 null이어야 테스트 통과
        // NoSuchElementException은 무시하고 테스트 수행
        assertThrows(NoSuchElementException.class, () -> {
            assertThat(ms.findById(memberId)).isNull(); // 삭제회원 id 조회결과가 null이면 테스트 통과
        });
    }

    @Test
    @Transactional // test 후 DB에 test 데이터가 남지 않음
    @Rollback      // test 후 DB에 test 데이터가 남지 않음
    @DisplayName("회원데이터수정")
    public void updateMember() {
        // 변수 선언
        String memberEmail = "수정회원이메일1";
        String memberPassword = "수정회원비밀번호1";
        String memberName = "수정회원이름1";
        // 변수를 바탕으로 회원 생성
        MemberSaveDTO memberSaveDTO = new MemberSaveDTO(memberEmail, memberPassword, memberName);
        Long saveMemberId = ms.save(memberSaveDTO);
        // 생성 회원 이름 조회
        String savedMemberName = ms.findById(saveMemberId).getMemberName();
        // 회원 이름 수정 (이름만 수정가능하도록 설정해두었음)
        String updateName = "수정완료이름";
        MemberDetailDTO updateMember = new MemberDetailDTO(saveMemberId, memberEmail, memberPassword, updateName);
        ms.update(updateMember);
        // 수정 후 DB에서 이름 조회
        String updateMemberName = ms.findById(saveMemberId).getMemberName();
        // 처음 생성한 이름(수전 전 이름)이 DB에 없어야함. (신규등록시 사용한 이름과 DB에 저장된 이름이 일치하는지 판단, 일치하지 않아야 테스트 통과)
        assertThat(savedMemberName).isNotEqualTo(updateMemberName);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("mybatis 목록 출력 테스트")
    public void memberListTest() {
        List<MemberMapperDTO> memberList = mmr.memberList();
        for(MemberMapperDTO m: memberList) {
            System.out.println("m.toString() = " + m.toString());
        }
    }

    @Test
    @DisplayName("mybatis 회원가입 테스트")
    public void memberSaveTest() {
        MemberMapperDTO memberMapperDTO = new MemberMapperDTO("회원이메일1", "회원비밀번호1", "회원이름1");
        mmr.save(memberMapperDTO);
    }

}
