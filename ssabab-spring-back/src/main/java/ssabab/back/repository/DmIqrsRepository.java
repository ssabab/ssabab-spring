// ssabab/back/repository/DmIqrsRepository.java (새로 추가)
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmIqrs;
import ssabab.back.enums.IqrType;

import java.util.List;
import java.util.Optional;

public interface DmIqrsRepository extends JpaRepository<DmIqrs, Long> {
    Optional<DmIqrs> findByIqrType(IqrType iqrType);
    List<DmIqrs> findByIqrTypeIn(List<IqrType> iqrTypes); // 여러 타입 조회용
}