package ssabab.back.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.entity.Account;
import ssabab.back.entity.Menu;
import ssabab.back.entity.PreVote;
import ssabab.back.repository.AccountRepository;
import ssabab.back.repository.MenuRepository;
import ssabab.back.repository.PreVoteRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreVoteService {

    private final AccountRepository accountRepository;
    private final MenuRepository menuRepository;
    private final PreVoteRepository preVoteRepository;

    /**
     * 사전 투표를 등록하거나 수정합니다.
     * 사용자가 다른 메뉴에 이미 투표한 경우, 해당 투표의 메뉴 ID를 변경합니다.
     * @param menuId 메뉴 ID
     * @param userId 사용자 ID
     * @return 성공 여부
     */
    @Transactional
    public boolean createOrUpdatePreVote(Integer menuId, Integer userId) {
        try {
            // 메뉴와 사용자 조회
            Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다: " + menuId));
            
            Account account = accountRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));
            
            // 현재 메뉴에 대한 투표 확인
            Optional<PreVote> existingVote = preVoteRepository.findByMenuAndAccount(menu, account);
            
            if (existingVote.isPresent()) {
                // 이미 이 메뉴에 투표한 경우, 수정 (사실상 동일한 데이터로 갱신)
                PreVote vote = existingVote.get();
                vote.setMenu(menu);
                vote.setAccount(account);
                preVoteRepository.save(vote);
            } else {
                // 사용자의 모든 투표 확인
                List<PreVote> userVotes = preVoteRepository.findByAccount(account);
                
                if (!userVotes.isEmpty()) {
                    // 사용자가 다른 메뉴에 투표한 경우, 첫 번째 투표를 수정
                    PreVote existingUserVote = userVotes.get(0);
                    existingUserVote.setMenu(menu);
                    preVoteRepository.save(existingUserVote);
                } else {
                    // 새로운 투표 생성
                    PreVote newVote = PreVote.builder()
                            .menu(menu)
                            .account(account)
                            .build();
                    preVoteRepository.save(newVote);
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 특정 메뉴의 투표 수를 조회합니다.
     * @param menuId 메뉴 ID
     * @return 투표 수
     */
    public int getVoteCountByMenuId(Integer menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다: " + menuId));
        
        return preVoteRepository.countVotesByMenu(menu);
    }
    
    /**
     * 사용자가 특정 메뉴에 투표했는지 확인합니다.
     * @param menuId 메뉴 ID
     * @param userId 사용자 ID
     * @return 투표 여부
     */
    public boolean hasVoted(Integer menuId, Integer userId) {
        try {
            Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다: " + menuId));
            
            Account account = accountRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));
            
            return preVoteRepository.findByMenuAndAccount(menu, account).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
} 