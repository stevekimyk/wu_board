package com.wu.board.mapper;

import com.wu.board.dto.PsnlPhoto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PsnlPhotoMapper {
    List<PsnlPhoto> selectAll();
    List<PsnlPhoto> selectPage(Map<String, Object> param);
    int selectCount(Map<String, Object> param);
    PsnlPhoto selectByEmpno(String empno);
}
