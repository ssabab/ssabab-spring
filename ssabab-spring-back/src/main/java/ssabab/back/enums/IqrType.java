// ssabab/back/enums/IqrType.java (새로 추가)
package ssabab.back.enums;

public enum IqrType {
    total,   // 전체 통계
    gender,  // 성별 통계
    class_num // 반별 통계 (class는 예약어이므로 class_num으로 사용)
    // 'ord'도 ERD에 있지만, 정확한 의미가 불명확하여 일단 제외. 필요시 추가
}