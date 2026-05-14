package com.wu.board.controller;

import com.wu.board.dto.Post;
import com.wu.board.dto.PsnlPhoto;
import com.wu.board.mapper.PsnlPhotoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private PsnlPhotoMapper psnlPhotoMapper;

    /**
     * 게시판 종류별 한글 라벨
     */
    private String label(String type) {
        if (type == null) return "전체 게시판";
        switch (type) {
            case "notice":  return "공지사항";
            case "free":    return "자유게시판";
            case "qna":     return "질문/답변";
            case "gallery": return "갤러리";
            default:        return "전체 게시판";
        }
    }

    /**
     * 더미 게시글 리스트 (실무에서는 MyBatis Mapper 호출로 대체)
     */
    private List<Post> dummyList(String type) {
        List<Post> list = new ArrayList<>();
        list.add(new Post(101L, type, "오늘 점심 뭐 먹었나요?",          "홍길동", "", "2026-05-11", 124, 12, true));
        list.add(new Post(100L, type, "주말에 갈만한 카페 추천해주세요", "이순신", "", "2026-05-10",  87,  8, false));
        list.add(new Post( 99L, type, "Spring Boot 공부 팁 공유합니다",   "김철수", "", "2026-05-09", 213,  5, false));
        list.add(new Post( 98L, type, "JPA와 MyBatis 중 뭐가 나을까요?",  "박개발", "", "2026-05-08", 156,  3, false));
        list.add(new Post( 97L, type, "Oracle 연결 에러 해결 방법",       "최테크", "", "2026-05-07", 342,  7, false));
        list.add(new Post( 96L, type, "Thymeleaf 변수 바인딩 질문",       "정스프링","", "2026-05-06",  78,  2, false));
        return list;
    }

    private static final int PAGE_SIZE = 10;

    // 사원 상세 (GET /board/list?id=사원번호)
    @GetMapping(value = "/list", params = "id")
    public String empDetail(@RequestParam("id") String empno, Model model) {
        PsnlPhoto p = psnlPhotoMapper.selectByEmpno(empno);
        model.addAttribute("emp", p);
        model.addAttribute("empno", empno);
        return "board/emp-detail";
    }

    // 게시글 목록
    @GetMapping("/list")
    public String list(@RequestParam(value = "type",       required = false, defaultValue = "free") String type,
                       @RequestParam(value = "searchType", required = false, defaultValue = "empno") String searchType,
                       @RequestParam(value = "q",          required = false) String q,
                       @RequestParam(value = "page",       required = false, defaultValue = "1") int page,
                       Model model) {
        model.addAttribute("type",       type);
        model.addAttribute("typeLabel",  label(type));
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword",    q);

        if ("free".equals(type)) {
            Map<String, Object> param = new HashMap<>();
            param.put("searchType", searchType);
            param.put("keyword",    q);

            int totalCount = psnlPhotoMapper.selectCount(param);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            param.put("startRow", (page - 1) * PAGE_SIZE + 1);
            param.put("endRow",   page * PAGE_SIZE);

            model.addAttribute("photoList",  psnlPhotoMapper.selectPage(param));
            model.addAttribute("postList",   new ArrayList<>());
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("curPage",    page);
        } else {
            model.addAttribute("postList", dummyList(type));
        }
        return "board/list";
    }

    // 게시글 상세
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Post post = new Post(id, "free",
                "Spring Boot + MyBatis + Oracle 연동 후기",
                "홍길동",
                "안녕하세요. 이번에 Spring Boot 2.7.7 + MyBatis + Oracle 19c 환경에서 게시판을 만들어봤습니다.\n\n" +
                "막혔던 부분과 해결 방법을 공유드려요.\n\n" +
                "1. ojdbc8 의존성 추가\n" +
                "2. application.properties 에 datasource 설정\n" +
                "3. MyBatis Mapper 인터페이스 + XML 매핑\n\n" +
                "도움이 되었으면 좋겠네요!",
                "2026-05-11", 124, 3, true);

        // 더미 댓글
        List<String[]> comments = Arrays.asList(
                new String[]{"이순신", "2026-05-11 10:23", "정리 잘 되어 있네요. 감사합니다!"},
                new String[]{"김철수", "2026-05-11 11:05", "ojdbc8 버전은 어떤거 쓰셨나요?"},
                new String[]{"홍길동", "2026-05-11 11:30", "Spring Boot 2.7.7 기본 버전 그대로 썼습니다."}
        );

        model.addAttribute("post",     post);
        model.addAttribute("comments", comments);
        return "board/detail";
    }

    // 게시글 작성 폼
    @GetMapping("/write")
    public String writeForm(@RequestParam(value = "type", required = false, defaultValue = "free") String type,
                            Model model) {
        model.addAttribute("type",      type);
        model.addAttribute("typeLabel", label(type));
        return "board/write";
    }

    // 게시글 작성 처리 (DB 연동 전: 일단 목록으로 리다이렉트)
    @PostMapping("/write")
    public String write(@ModelAttribute Post post) {
        // TODO: MyBatis insert 호출
        return "redirect:/board/list?type=" + (post.getBoardType() == null ? "free" : post.getBoardType());
    }

    // 사원 사진 BLOB → 이미지 응답
    @GetMapping("/photo/{empno}")
    @ResponseBody
    public ResponseEntity<byte[]> photo(@PathVariable String empno) {
        PsnlPhoto p = psnlPhotoMapper.selectByEmpno(empno);
        if (p == null || p.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(p.getPhoto());
    }
}
