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
