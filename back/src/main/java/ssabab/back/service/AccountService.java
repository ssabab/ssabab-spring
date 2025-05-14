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

    // Sign up a new account with hashed password
    public Account save(AccountDTO accountDTO) {
        // Map DTO to Entity and hash the password
        Account account = Account.builder()
                .email(accountDTO.getEmail())
                .username(accountDTO.getUsername())
                .password(passwordEncoder.encode(accountDTO.getPassword()))
                .role("USER")
                .provider("LOCAL")
                .providerId(null)
                .profileImgUrl(null)
                .active(true)
                .build();
        return accountRepository.save(account);
    }

    // Local login: validate credentials and return Account if successful
    public Account login(AccountDTO accountDTO) {
        Optional<Account> accountOpt = accountRepository.findByEmail(accountDTO.getEmail());
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            // Check password hash match (only for LOCAL accounts)
            if (account.getPassword() != null 
                    && passwordEncoder.matches(accountDTO.getPassword(), account.getPassword())) {
                return account;
            }
        }
        return null;
    }

    // Retrieve all accounts (for admin or debugging purposes)
    public List<AccountDTO> findAll() {
        List<Account> accounts = accountRepository.findAll();
        List<AccountDTO> dtoList = new ArrayList<>();
        for (Account account : accounts) {
            dtoList.add(AccountDTO.toAccountDTO(account));
        }
        return dtoList;
    }

    // Find account by userId
    public AccountDTO findByUserId(INTGER userId) {
        Optional<Account> accountOpt = accountRepository.findByUserId(userId);
        return accountOpt.map(AccountDTO::toAccountDTO).orElse(null);
    }

    // Update account profile (username or password)
    public void update(AccountDTO accountDTO) {
        Optional<Account> accountOpt = accountRepository.findById(accountDTO.getUserId());
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (accountDTO.getUsername() != null) {
                account.setUsername(accountDTO.getUsername());
            }
            if (accountDTO.getPassword() != null && !accountDTO.getPassword().isEmpty()) {
                account.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
            }
            accountRepository.save(account);
        }
    }

    // Check if an email is already in use (returns "ok" if available, otherwise null)
    public String emailCheck(String email) {
        boolean exists = accountRepository.findByEmail(email).isPresent();
        return exists ? null : "ok";
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
