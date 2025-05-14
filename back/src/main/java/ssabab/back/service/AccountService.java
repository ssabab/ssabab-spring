package ssabab.back.service;

import ssabab.back.dto.AccountDTO;
import ssabab.back.entity.Account;
import ssabab.back.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 Account 생성 (회원가입 처리)
     * @throws IllegalStateException 이메일이 중복된 경우
     */
    public Account save(AccountDTO dto) {
        // 이메일 중복 체크
        if (accountRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 사용중인 이메일입니다.");
        }
        // Account 엔티티 생성 및 필드 설정
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setUsername(dto.getUsername());
        // 비밀번호 해싱 저장
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setRole("ROLE_USER");
        account.setActive(true);
        // (소셜 로그인 아닌 로컬 가입이므로 provider 정보는 null 그대로 둠)
        accountRepository.save(account);
        return account;
    }

    /**
     * 로그인 처리 (이메일/비밀번호 검증)
     * @return 인증 성공한 Account (실패 시 예외 발생)
     * @throws IllegalArgumentException 이메일 없음 또는 비밀번호 불일치
     */
    public Account login(AccountDTO dto) {
        Account account = accountRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (!account.isActive()) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }
        return account;
    }

    /**
     * Account의 Refresh Token 값 업데이트 (로그인/로그아웃 등에서 사용)
     */
    public void updateRefreshToken(Integer userId, String refreshToken) {
        accountRepository.findById(userId).ifPresent(acc -> {
            acc.setRefreshToken(refreshToken);
            accountRepository.save(acc);
        });
    }
}




// @Service
// @RequiredArgsConstructor
// public class AccountService {

//     private final AccountRepository accountRepo;

//     /* 회원가입 */
//     public void save(AccountDTO dto) {
//         if (accountRepo.existsByEmail(dto.getEmail())) {
//             throw new IllegalArgumentException("이미 등록된 이메일입니다.");
//         }
//         accountRepo.save(Account.fromDTO(dto));
//     }

//     /* 로그인 (성공 시 DTO 반환, 실패 시 null) */
//     public AccountDTO login(AccountDTO dto) {
//         Optional<Account> opt = accountRepo.findByEmail(dto.getEmail());
//         if (opt.isPresent() && opt.get().getPassword().equals(dto.getPassword())) {
//             return AccountDTO.fromEntity(opt.get());
//         }
//         return null;
//     }

//     /* 전체 목록 */
//     public List<AccountDTO> findAll() {
//         return accountRepo.findAll()
//                 .stream().map(AccountDTO::fromEntity)
//                 .collect(Collectors.toList());
//     }

//     /* PK 조회 */
//     public AccountDTO findByuserId(Integer id) {
//         return accountRepo.findById(id).map(AccountDTO::fromEntity).orElse(null);
//     }

//     /* 이메일로 조회 (마이페이지 등) */
//     public AccountDTO findByEmail(String email) {
//         return accountRepo.findByEmail(email).map(AccountDTO::fromEntity).orElse(null);
//     }

//     /* 이메일 중복 체크 */
//     public boolean emailExists(String email) {
//         return accountRepo.existsByEmail(email);
//     }
// }
