package com.wu.board.service;

import com.wu.board.dto.PsnlPhoto;
import com.wu.board.mapper.PsnlPhotoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PsnlPhotoServiceImpl implements PsnlPhotoService {

    private static final int PAGE_SIZE = 14;

    private final PsnlPhotoMapper psnlPhotoMapper;

    @Override
    public PsnlPhoto findByEmpno(String empno) {
        return psnlPhotoMapper.selectByEmpno(empno);
    }

    @Override
    public int countAll(String searchType, String keyword) {
        Map<String, Object> param = new HashMap<>();
        param.put("searchType", searchType);
        param.put("keyword",    keyword);
        return psnlPhotoMapper.selectCount(param);
    }

    @Override
    public int calcTotalPages(int totalCount) {
        return (int) Math.ceil((double) totalCount / PAGE_SIZE);
    }

    @Override
    public int normalizePage(int page, int totalPages) {
        if (page < 1) return 1;
        if (page > totalPages && totalPages > 0) return totalPages;
        return page;
    }

    @Override
    public List<PsnlPhoto> findPage(String searchType, String keyword, int page) {
        Map<String, Object> param = new HashMap<>();
        param.put("searchType", searchType);
        param.put("keyword",    keyword);
        param.put("startRow",   (page - 1) * PAGE_SIZE + 1);
        param.put("endRow",     page * PAGE_SIZE);
        return psnlPhotoMapper.selectPage(param);
    }
}
