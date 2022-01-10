package com.icia.member;

import com.icia.member.dto.MemberDetailDTO;
import com.icia.member.dto.MemberSaveDTO;
import com.icia.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.stream.IntStream;

@SpringBootTest
public class MemberTest {

    @Autowired
    private MemberService ms;

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

}
