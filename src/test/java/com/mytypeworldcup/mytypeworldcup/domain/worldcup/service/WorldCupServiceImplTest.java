package com.mytypeworldcup.mytypeworldcup.domain.worldcup.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
import com.mytypeworldcup.mytypeworldcup.domain.member.entity.Member;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.dto.*;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.entity.WorldCup;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.exception.WorldCupExceptionCode;
import com.mytypeworldcup.mytypeworldcup.domain.worldcup.repository.WorldCupRepository;
import com.mytypeworldcup.mytypeworldcup.global.error.BusinessLogicException;
import com.mytypeworldcup.mytypeworldcup.global.error.CommonExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;


@ExtendWith(MockitoExtension.class)
class WorldCupServiceImplTest {
    @InjectMocks
    private WorldCupServiceImpl worldCupService;
    @Mock
    private WorldCupMapper worldCupMapper;
    @Mock
    private WorldCupRepository worldCupRepository;

    @Test
    @DisplayName("월드컵 생성 테스트")
    void createWorldCup() {
        // given
        WorldCupPostDto worldCupPostDto = WorldCupPostDto
                .builder()
                .title("테스트 월드컵")
                .description("테스트 설명")
                .password(null) // 공개이므로 비밀번호 없음
                .build();
        worldCupPostDto.setMemberId(1L);

        WorldCupResponseDto expected = WorldCupResponseDto
                .builder()
                .id(2L)
                .title(worldCupPostDto.getTitle())
                .description(worldCupPostDto.getDescription())
                .password(worldCupPostDto.getPassword())
                .memberId(worldCupPostDto.getMemberId())
                .build();

        given(worldCupMapper.worldCupPostDtoToWorldCup(worldCupPostDto)).willReturn(new WorldCup());
        given(worldCupRepository.save(any(WorldCup.class))).willReturn(new WorldCup());
        given(worldCupMapper.worldCupToWorldCupResponseDto(any(WorldCup.class))).willReturn(expected);

        // when
        WorldCupResponseDto actual = worldCupService.createWorldCup(worldCupPostDto);

        // then
        verify(worldCupMapper, times(1)).worldCupPostDtoToWorldCup(any(WorldCupPostDto.class));
        verify(worldCupRepository, times(1)).save(any(WorldCup.class));
        verify(worldCupMapper, times(1)).worldCupToWorldCupResponseDto(any(WorldCup.class));

        assertSame(expected, actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getMemberId(), actual.getMemberId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPassword(), actual.getPassword());
    }

    @Test
    @DisplayName("월드컵 업데이트")
    void updateWorldCup() {
        // given
        long worldCupId = 1L;
        WorldCup worldCup = WorldCup.builder()
                .title("테스트 타이틀")
                .description("테스트 설명")
                .password(null)
                .build();
        worldCup.setId(worldCupId);

        WorldCupPatchDto worldCupPatchDto = WorldCupPatchDto.builder()
                .title("업데이트 테스트")
                .description("업데이트 테스트 입니다")
                .password("1435")
                .build();

        WorldCupResponseDto worldCupResponseDto = WorldCupResponseDto.builder()
                .id(worldCupId)
                .title(worldCupPatchDto.getTitle())
                .description(worldCupPatchDto.getDescription())
                .password(worldCupPatchDto.getPassword())
                .build();

        given(worldCupRepository.findById(worldCupId)).willReturn(Optional.of(worldCup));
        given(worldCupMapper.worldCupToWorldCupResponseDto(any(WorldCup.class))).willReturn(worldCupResponseDto);

        // when
        WorldCupResponseDto result = worldCupService.updateWorldCup(worldCupId, worldCupPatchDto);

        // then
        verify(worldCupRepository, times(1)).findById(worldCupId);
        verify(worldCupMapper, times(1)).worldCupToWorldCupResponseDto(any(WorldCup.class));
        assertEquals(worldCupPatchDto.getTitle(), worldCup.getTitle());
        assertEquals(worldCupPatchDto.getDescription(), worldCup.getDescription());
        assertEquals(worldCupPatchDto.getPassword(), worldCup.getPassword());
        assertSame(worldCupResponseDto, result);
    }

    @Test
    @DisplayName("월드컵 업데이트 - 비밀번호 \"null\"인 경우")
    void updateWorldCup_passwordNull() {
        // given
        long worldCupId = 1L;
        WorldCup worldCup = WorldCup.builder()
                .title("테스트 타이틀")
                .description("테스트 설명")
                .password("1434")
                .build();
        worldCup.setId(worldCupId);

        WorldCupPatchDto worldCupPatchDto = WorldCupPatchDto.builder()
                .title("업데이트 테스트")
                .description("업데이트 테스트 입니다")
                .password("null")
                .build();

        WorldCupResponseDto worldCupResponseDto = WorldCupResponseDto.builder()
                .id(worldCupId)
                .title(worldCupPatchDto.getTitle())
                .description(worldCupPatchDto.getDescription())
                .password(worldCupPatchDto.getPassword())
                .build();

        given(worldCupRepository.findById(worldCupId)).willReturn(Optional.of(worldCup));
        given(worldCupMapper.worldCupToWorldCupResponseDto(any(WorldCup.class))).willReturn(worldCupResponseDto);

        // when
        WorldCupResponseDto actual = worldCupService.updateWorldCup(worldCupId, worldCupPatchDto);

        // then
        verify(worldCupRepository, times(1)).findById(worldCupId);
        verify(worldCupMapper, times(1)).worldCupToWorldCupResponseDto(any(WorldCup.class));
        assertEquals(worldCupPatchDto.getTitle(), worldCup.getTitle());
        assertEquals(worldCupPatchDto.getDescription(), worldCup.getDescription());
        assertNull(worldCup.getPassword());
        assertSame(worldCupResponseDto, actual);
    }

    @Test
    @DisplayName("월드컵 검색")
    void searchWorldCups() {
        // given
        Long memberId = 1L;
        String keyword = "월드컵";
        Pageable pageable = PageRequest.of(0, 5, ASC, "createdAt");

        List<WorldCupSimpleResponseDto> data = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            WorldCupSimpleResponseDto dummy = new WorldCupSimpleResponseDto(i, "월드컵 " + i, "설명입니다.");
            dummy.setCandidateSimpleResponseDtos(List.of(
                    new CandidateSimpleResponseDto(i, "테스트 이미지 " + i, "테스트 이미지 url", "테스트 썸네일 url"),
                    new CandidateSimpleResponseDto(i + 3, "테스트 이미지 " + i + 3, "테스트 이미지 url", "테스트 썸네일 url")));
            data.add(dummy);
        }

        Page<WorldCupSimpleResponseDto> expected = new PageImpl<>(data);

        given(worldCupService.searchWorldCups(anyLong(), anyString(), any(Pageable.class))).willReturn(expected);

        // when
        Page<WorldCupSimpleResponseDto> actual = worldCupService.searchWorldCups(memberId, keyword, pageable);

        // then
        verify(worldCupRepository).getWorldCupsWithCandidates(anyLong(), anyString(), any(Pageable.class));
        assertSame(expected, actual);
    }

    @Test
    @DisplayName("월드컵 아이디로 찾기 - 성공")
    void findWorldCupPreview() {
        // given
        Long worldCupId = 1L;

        WorldCup worldCup = new WorldCup();
        worldCup.setId(1L);
        WorldCupPreview expected = WorldCupPreview
                .builder()
                .id(worldCupId)
                .title("테스트 월드컵")
                .description("테스트 설명")
                .visibility(true)
                .candidatesCount(16)
                .build();

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(worldCup));
        given(worldCupMapper.worldCupToWorldCupPreview(any(WorldCup.class))).willReturn(expected);

        // when
        WorldCupPreview actual = worldCupService.findWorldCupPreview(worldCupId);

        // then
        verify(worldCupRepository).findById(anyLong());
        verify(worldCupMapper).worldCupToWorldCupPreview(any(WorldCup.class));

        assertSame(expected, actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getVisibility(), actual.getVisibility());
        assertEquals(expected.getCandidatesCount(), actual.getCandidatesCount());
    }

    @Test
    @DisplayName("월드컵 아이디로 찾기 - 실패")
    void findWorldCupPreview_bad() {
        // given
        Long worldCupId = 1L;

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when
        // then
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> worldCupService.findWorldCupPreview(worldCupId));
        assertEquals(WorldCupExceptionCode.WORLD_CUP_NOT_FOUND, thrown.getExceptionCode());
    }

    @Test
    @DisplayName("비밀번호 검증 - 비밀번호가 없을 경우")
    void verifyPassword_noPassword() {
        // given
        Long worldCupId = 1L;
        String password = null;

        WorldCup expected = WorldCup
                .builder()
                .password(null)
                .build();

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(expected));

        // when
        // then
        assertDoesNotThrow(() -> worldCupService.verifyPassword(worldCupId, password));
    }

    @Test
    @DisplayName("비밀번호 검증 - 성공")
    void verifyPassword() {
        // given
        Long worldCupId = 1L;
        String password = String.valueOf(1234);

        WorldCup expected = WorldCup
                .builder()
                .password(String.valueOf(1234))
                .build();

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(expected));

        // when
        // then
        assertDoesNotThrow(() -> worldCupService.verifyPassword(worldCupId, password));
    }

    @Test
    @DisplayName("비밀번호 검증 - 유효하지않는 비밀번호")
    void verifyPassword_invalidPassword() {
        // given
        Long worldCupId = 1L;
        String password = "1234";

        WorldCup expected = WorldCup
                .builder()
                .password("실패")
                .build();

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(expected));

        // when
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> worldCupService.verifyPassword(worldCupId, password));

        // then
        assertEquals(CommonExceptionCode.INVALID_PASSWORD, thrown.getExceptionCode());
    }

    @Test
    @DisplayName("월드컵 상세보기")
    void findWorldCupDetails() {
        // given
        long worldCupId = 1L;
        WorldCupResponseDto worldCupResponseDto = WorldCupResponseDto.builder()
                .id(worldCupId)
                .title("test")
                .description("test")
                .password(null)
                .memberId(2L)
                .build();

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(new WorldCup()));
        given(worldCupMapper.worldCupToWorldCupResponseDto(any(WorldCup.class))).willReturn(worldCupResponseDto);

        // when
        WorldCupResponseDto actual = worldCupService.findWorldCupDetails(worldCupId);

        // then
        assertEquals(worldCupResponseDto.getId(), actual.getId());
        assertEquals(worldCupResponseDto.getTitle(), actual.getTitle());
        assertEquals(worldCupResponseDto.getDescription(), actual.getDescription());
        assertEquals(worldCupResponseDto.getMemberId(), actual.getMemberId());
        assertNull(actual.getPassword());
    }

    @Test
    @DisplayName("월드컵 접근 권한 확인")
    void verifyWorldCupAccess() {
        // given
        String email = "test@test.com";

        Member member = Member.builder()
                .email("test@test.com")
                .build();

        WorldCup worldCup = WorldCup.builder()
                .member(member)
                .build();

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(worldCup));

        // when
        // then
        assertDoesNotThrow(() -> worldCupService.verifyWorldCupAccess(email, 1L));
    }

    @Test
    @DisplayName("월드컵 접근 권한 확인 - 실패")
    void verifyWorldCupAccess_bad() {
        // given
        String email = "test@test.com";

        Member member = Member.builder()
                .email("forbidden@test.com")
                .build();

        WorldCup worldCup = WorldCup.builder()
                .member(member)
                .build();

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(worldCup));

        // when
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> worldCupService.verifyWorldCupAccess(email, 1L));

        // then
        assertEquals(CommonExceptionCode.FORBIDDEN, thrown.getExceptionCode());
    }

    @Test
    @DisplayName("유효한 월드컵 찾기")
    void findVerifiedWorldCup() {
        // given
        long id = 1L;
        WorldCup worldCup = new WorldCup();
        worldCup.setId(id);

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(worldCup));

        // when
        WorldCup verifiedWorldCup = worldCupService.findVerifiedWorldCup(id);

        // then
        assertEquals(id, verifiedWorldCup.getId());
    }

    @Test
    @DisplayName("유효한 월드컵 찾기 - 실패")
    void findVerifiedWorldCup_bad() {
        // given
        long id = 1L;

        // when
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> worldCupService.findVerifiedWorldCup(id));

        // then
        assertEquals(WorldCupExceptionCode.WORLD_CUP_NOT_FOUND, thrown.getExceptionCode());
    }

    @Test
    @DisplayName("월드컵 삭제")
    void deleteWorldCup() {
        // given
        long worldCupId = 1L;
        WorldCup worldCup = new WorldCup();
        worldCup.setId(worldCupId);

        given(worldCupRepository.findById(worldCupId)).willReturn(Optional.ofNullable(worldCup));
        doNothing().when(worldCupRepository).delete(worldCup);

        // when
        worldCupService.deleteWorldCup(worldCupId);

        // then
        verify(worldCupRepository, times(1)).delete(worldCup);
    }
}