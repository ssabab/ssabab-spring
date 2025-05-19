package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.dto.AccountDTO;
import ssabab.back.dto.LoginDTO;
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
        try {
            if (accountRepo.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("이미 등록된 이메일입니다.");
            }
            
            // 필수 필드 검증
            if (dto.getOrdNum() == null) {
                throw new IllegalArgumentException("SSAFY 기수는 필수 입력 항목입니다.");
            }
            if (dto.getClassNum() == null) {
                throw new IllegalArgumentException("반 번호는 필수 입력 항목입니다.");
            }
            if (dto.getSsafyRegion() == null || dto.getSsafyRegion().isEmpty()) {
                throw new IllegalArgumentException("SSAFY 지역은 필수 입력 항목입니다.");
            }
            if (dto.getGender() == null) {
                throw new IllegalArgumentException("성별은 필수 입력 항목입니다.");
            }
            if (dto.getBirthYear() == null) {
                throw new IllegalArgumentException("출생년도는 필수 입력 항목입니다.");
            }
            
            // role은 항상 "0"으로 설정
            dto.setRole("0");
            
            Account account = Account.fromDTO(dto);
            accountRepo.save(account);
        } catch (Exception e) {
            System.out.println("회원가입 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /* 로그인 (성공 시 DTO 반환, 실패 시 null) */
    public AccountDTO login(LoginDTO dto) {
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
    public AccountDTO findById(Integer id) {
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
