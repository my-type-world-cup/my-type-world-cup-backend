package com.mytypeworldcup.mytypeworldcup.domain.worldcup.service;

import com.mytypeworldcup.mytypeworldcup.domain.candidate.dto.CandidateSimpleResponseDto;
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
class WorldCupServiceTest {
    @InjectMocks
    private WorldCupService worldCupService;
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
                .visibility(true)
                .password(null) // 공개이므로 비밀번호 없음
                .build();
        worldCupPostDto.setMemberId(1L);

        WorldCupResponseDto expected = WorldCupResponseDto
                .builder()
                .id(2L)
                .title(worldCupPostDto.getTitle())
                .description(worldCupPostDto.getDescription())
                .visibility(worldCupPostDto.getVisibility())
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
        assertEquals(expected.getCandidateResponseDtos(), actual.getCandidateResponseDtos());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getMemberId(), actual.getMemberId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getVisibility(), actual.getVisibility());
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
    void findWorldCup_happy() {
        // given
        Long worldCupId = 1L;

        WorldCup worldCup = new WorldCup();
        worldCup.setId(1L);
        WorldCupInfoResponseDto expected = WorldCupInfoResponseDto
                .builder()
                .id(worldCupId)
                .title("테스트 월드컵")
                .description("테스트 설명")
                .visibility(true)
                .candidatesCount(16)
                .build();

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(worldCup));
        given(worldCupMapper.worldCupToWorldCupInfoResponseDto(any(WorldCup.class))).willReturn(expected);

        // when
        WorldCupInfoResponseDto actual = worldCupService.findWorldCup(worldCupId);

        // then
        verify(worldCupRepository).findById(anyLong());
        verify(worldCupMapper).worldCupToWorldCupInfoResponseDto(any(WorldCup.class));

        assertSame(expected, actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getVisibility(), actual.getVisibility());
        assertEquals(expected.getCandidatesCount(), actual.getCandidatesCount());
    }

    @Test
    @DisplayName("월드컵 아이디로 찾기 - 실패")
    void findWorldCup_bad() {
        // given
        Long worldCupId = 1L;

        given(worldCupRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when
        // then
        BusinessLogicException thrown = assertThrows(BusinessLogicException.class, () -> worldCupService.findWorldCup(worldCupId));
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
    void verifyPassword_success() {
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
}