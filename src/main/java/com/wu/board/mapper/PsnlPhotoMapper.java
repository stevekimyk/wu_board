package com.wu.board.mapper;

import com.wu.board.dto.PsnlPhoto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PsnlPhotoMapper {

    /* xml Mapper에 들어가서 반환타입이 List<PsnlPhoto>, id가 selectPage 찾아 실행시킨다. 파라미터는 Map 타입의 param  */
    List<PsnlPhoto> selectPage(Map <String, Object> param);

    /* xml Mapper에 들어가서 id가 selectCount를 찾아 실행시킨다. 파라미터는 Map형식의 param이다  */
    int selectCount(Map<String, Object> param);

    /* xml Mapper에 들어가서 반환타입이 PsnlPhoto, id가 selectByEmpno 찾아 실행시킨다. 파라미터는 String 사원번호 empno이다  */
    PsnlPhoto selectByEmpno(String empno);
}
