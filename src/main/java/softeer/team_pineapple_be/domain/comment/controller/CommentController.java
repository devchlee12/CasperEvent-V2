package softeer.team_pineapple_be.domain.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import softeer.team_pineapple_be.domain.comment.request.CommentLikeRequest;
import softeer.team_pineapple_be.domain.comment.request.CommentRequest;
import softeer.team_pineapple_be.domain.comment.response.CommentPageResponse;
import softeer.team_pineapple_be.domain.comment.response.CommentResponse;
import softeer.team_pineapple_be.domain.comment.service.CommentService;
import softeer.team_pineapple_be.global.auth.annotation.Auth;
import softeer.team_pineapple_be.global.auth.service.AuthMemberService;
import softeer.team_pineapple_be.global.common.response.SuccessResponse;

/**
 * 기대평 컨트롤러
 */
@Tag(name = "기대평 API", description = "기대평 기능 처리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
  private final CommentService commentService;
  private final AuthMemberService authMemberService;

  @Auth
  @Operation(summary = "기대평 남기기")
  @PostMapping
  public ResponseEntity<SuccessResponse> addComment(@Valid @RequestBody CommentRequest commentRequest) {
    String memberPhoneNumber = authMemberService.getMemberPhoneNumber();
    commentService.saveComment(memberPhoneNumber, commentRequest);
    return ResponseEntity.ok(new SuccessResponse());
  }

  @Auth
  @Operation(summary = "좋아요 누르기")
  @PostMapping("/likes")
  public ResponseEntity<SuccessResponse> addLikes(@Valid @RequestBody CommentLikeRequest commentLikeRequest) {
    String memberPhoneNumber = authMemberService.getMemberPhoneNumber();
    commentService.saveCommentLike(memberPhoneNumber, commentLikeRequest);
    return ResponseEntity.ok(new SuccessResponse());
  }

  @Operation(summary = "하나의 댓글을 가져오는 api")
  @GetMapping("/commentId")
  public ResponseEntity<CommentResponse> getComment(@RequestParam(name = "id") Long id) {
    return ResponseEntity.ok(commentService.getCommentById(id));
  }

  @Operation(summary = "page, 정렬순서, 날짜에 따라 기대평 가져오기")
  @GetMapping
  public ResponseEntity<CommentPageResponse> getComments(
      @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(name = "sort", required = false, defaultValue = "") String sort,
      @RequestParam(name = "date") LocalDate date) {
    if (sort.equals("like")) {
      return ResponseEntity.ok(commentService.getCommentsSortedByLikes(page, date));
    }
    return ResponseEntity.ok(commentService.getCommentsSortedByRecent(page, date));
  }
}
