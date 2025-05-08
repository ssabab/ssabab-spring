package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.dto.AccountDTO;
import ssabab.back.entity.Account;
import ssabab.back.repository.AccountRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepo;

    /* 회원가입 */
    public void save(AccountDTO dto) {
        if (accountRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        accountRepo.save(Account.fromDTO(dto));
    }

    /* 로그인 (성공 시 DTO 반환, 실패 시 null) */
    public AccountDTO login(AccountDTO dto) {
        Optional<Account> opt = accountRepo.findByEmail(dto.getEmail());
        if (opt.isPresent() && opt.get().getPassword().equals(dto.getPassword())) {
            return AccountDTO.fromEntity(opt.get());
        }
        return null;
    }

    /* 전체 목록 */
    public List<AccountDTO> findAll() {
        return accountRepo.findAll()
                .stream().map(AccountDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /* PK 조회 */
    public AccountDTO findByuserId(Long id) {
        return accountRepo.findById(id).map(AccountDTO::fromEntity).orElse(null);
    }

    /* 이메일로 조회 (마이페이지 등) */
    public AccountDTO findByEmail(String email) {
        return accountRepo.findByEmail(email).map(AccountDTO::fromEntity).orElse(null);
    }

    /* 이메일 중복 체크 */
    public boolean emailExists(String email) {
        return accountRepo.existsByEmail(email);
    }
}
