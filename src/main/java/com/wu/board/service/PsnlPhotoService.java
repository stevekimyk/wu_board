package com.wu.board.service;

import com.wu.board.dto.PsnlPhoto;

import java.util.List;

public interface PsnlPhotoService {
    PsnlPhoto findByEmpno(String empno);
    int countAll(String searchType, String keyword);
    int calcTotalPages(int totalCount);
    int normalizePage(int page, int totalPages);
    List<PsnlPhoto> findPage(String searchType, String keyword, int page);
}
