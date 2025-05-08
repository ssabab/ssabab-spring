package ssabab.back.service;

import ssabab.back.dto.AccountDTO;
import ssabab.back.entity.Account;
import ssabab.back.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    public void save(AccountDTO accountDTO) {
        // 1. dto -> entity 변환
        // 2. repository의 save 메서드 호출
        Account account = Account.toAccountEntity(accountDTO);
        accountRepository.save(account);
        // repository의 save메서드 호출 (조건. entity객체를 넘겨줘야 함)
    }

    public AccountDTO login(AccountDTO accountDTO) {
        /*
            1. 회원이 입력한 이메일로 DB에서 조회를 함
            2. DB에서 조회한 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 판단
         */
        Optional<Account> byAccountEmail = accountRepository.findByAccountEmail(accountDTO.getAccountEmail());
        if (byAccountEmail.isPresent()) {
            // 조회 결과가 있다(해당 이메일을 가진 회원 정보가 있다)
            Account account = byAccountEmail.get();
            if (account.getAccountPassword().equals(accountDTO.getAccountPassword())) {
                // 비밀번호 일치
                // entity -> dto 변환 후 리턴
                AccountDTO dto = AccountDTO.toAccountDTO(account);
                return dto;
            } else {
                // 비밀번호 불일치(로그인실패)
                return null;
            }
        } else {
            // 조회 결과가 없다(해당 이메일을 가진 회원이 없다)
            return null;
        }
    }

    public List<AccountDTO> findAll() {
        List<Account> accountList = accountRepository.findAll();
        List<AccountDTO> accountDTOList = new ArrayList<>();
        for (Account account: accountList) {
            accountDTOList.add(AccountDTO.toAccountDTO(account));
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            memberDTOList.add(memberDTO);
        }
        return accountDTOList;
    }

    public AccountDTO findByuserId(Long userId) {
        Optional<Account> optionalAccount = accountRepository.findByuserId(userId);
        if (optionalAccount.isPresent()) {
//            MemberEntity memberEntity = optionalMemberEntity.get();
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            return memberDTO;
            return AccountDTO.toAccountDTO(optionalAccount.get());
        } else {
            return null;
        }

    }

    public AccountDTO updateForm(String myEmail) {
        Optional<Account> optionalAccount = accountRepository.findByAccountEmail(myEmail);
        if (optionalAccount.isPresent()) {
            return AccountDTO.toAccountDTO(optionalAccount.get());
        } else {
            return null;
        }
    }

    public void update(AccountDTO accountDTO) {
        accountRepository.save(Account.toUpdateAccount(accountDTO));
    }


    public String emailCheck(String accountEmail) {
        Optional<Account> byAccountEmail = accountRepository.findByAccountEmail(accountEmail);
        if (byAccountEmail.isPresent()) {
            // 조회결과가 있다 -> 사용할 수 없다.
            return null;
        } else {
            // 조회결과가 없다 -> 사용할 수 있다.
            return "ok";
        }
    }
}













