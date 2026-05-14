/**
 * @author Stevekim
 * @since 2026-04-20
 */
package com.wu.board.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PsnlPhoto {
    private String empno;
    private String empnoNm;    // b.kor_nm
    private String deptNo;     // b.dept_no
    private String deptNoNm;   // wu_cmm.codenm.dept(b.dept_no)
    private byte[] photo;      // BLOB
}

/**
 *  @Data의 역활
 *  @Getter - 모든 필드의 getter 생성
 *  @Setter - 모든 필드의 setter 생성 (final 제외)
 *  @ToString - toString() 생성
 *  @EqualsAndHashCode - equals(), hashCode() 생성
 *  @RequiredArgsConstructor - final/@NonNull 필드를 받는 생성자 생성
 */

/**
 * DTO 사실상의 표준
 *
 * @Data
 * @NoArgsConstructor
 * @AllArgsConstructor
 */

