// service.AccountService
package ssabab.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.AccountDTO;
import ssabab.back.dto.SignupRequestDTO;
import ssabab.back.entity.Account;
import ssabab.back.repository.AccountRepository;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * 회원 계정 관련 비즈니스 로직 서비스
 */
@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    /**
     * 새로운 OAuth2 사용자 계정을 등록 (회원가입)
     */
    @Transactional
    public Account registerNewAccount(String provider, String providerId, String email,
                                      SignupRequestDTO signupData, String profileImageUrl) {
        // 중복 가입 방지: provider + providerId로 이미 존재하면 예외
        if (accountRepository.findByProviderAndProviderId(provider, providerId).isPresent()) {
            throw new IllegalStateException("이미 가입된 사용자입니다.");
        }
        // username 중복 확인
        if (signupData.getUsername() != null && accountRepository.findByUsername(signupData.getUsername()).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        // Account 엔티티 생성 및 필드 설정
        Account account = Account.builder()
                .provider(provider)
                .providerId(providerId)
                .email(email)
                .username(signupData.getUsername())
                .ssafyYear(signupData.getSsafyYear())
                .classNum(signupData.getClassNum())
                .ssafyRegion(signupData.getSsafyRegion())
                .gender(signupData.getGender())
                .birthDate(signupData.getBirthDate())
                .active(true)
                .role("USER") // 기본 역할
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .profileImageUrl(profileImageUrl)
                .build();

        // 계정 저장
        accountRepository.save(account);
        return account;
    }

    /**
     * Account 엔티티를 AccountDTO로 변환하여 반환
     */
    public AccountDTO getProfile(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setUserId(account.getUserId());
        dto.setUsername(account.getUsername());
        dto.setEmail(account.getEmail());
        dto.setSsafyYear(account.getSsafyYear());
        dto.setClassNum(account.getClassNum());
        dto.setSsafyRegion(account.getSsafyRegion());
        dto.setGender(account.getGender());
        dto.setBirthDate(account.getBirthDate());
        return dto;
    }

    /**
     * 회원 프로필 정보 업데이트 (닉네임 등 변경)
     */
    @Transactional
    public AccountDTO updateProfile(Long userId, SignupRequestDTO updateData) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        // 전달된 값들로 필드 업데이트 (null이 아닌 값만 반영)
        // 닉네임 변경 시 중복 확인
        if (updateData.getUsername() != null && !updateData.getUsername().equals(account.getUsername())) {
            if (accountRepository.findByUsername(updateData.getUsername()).isPresent()) {
                throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
            }
            account.setUsername(updateData.getUsername());
        }
        if (updateData.getSsafyYear() != null) account.setSsafyYear(updateData.getSsafyYear());
        if (updateData.getClassNum() != null) account.setClassNum(updateData.getClassNum());
        if (updateData.getSsafyRegion() != null) account.setSsafyRegion(updateData.getSsafyRegion());
        if (updateData.getGender() != null) account.setGender(updateData.getGender());
        if (updateData.getBirthDate() != null) account.setBirthDate(updateData.getBirthDate());
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        return getProfile(account);
    }

    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public Account getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        String email = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername(); // UserDetails의 username은 여기서는 email
        } else {
            throw new IllegalStateException("인증 정보에서 사용자 이메일을 찾을 수 없습니다.");
        }

        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("로그인된 사용자 정보를 찾을 수 없습니다.")); // 변경: 메시지 구체화
    }
}