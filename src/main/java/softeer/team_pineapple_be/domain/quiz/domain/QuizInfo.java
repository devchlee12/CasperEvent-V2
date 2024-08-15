package softeer.team_pineapple_be.domain.quiz.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import softeer.team_pineapple_be.domain.quiz.request.QuizInfoModifyRequest;

//TODO: quizDescription 넣어야 될지 얘기해보기

/**
 * QuizInfo의 엔티티 타입 Quiz의 정답 정보 및 이미지 저장
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuizInfo {

  @Id
  private Integer id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "id", referencedColumnName = "id")
  private QuizContent quizContent;

  @Column(nullable = false)
  private Byte answerNum;

  @Column(nullable = false)
  private String quizImage;

  public void update(QuizInfoModifyRequest quizInfoModifyRequest) {
    this.answerNum = quizInfoModifyRequest.getAnswerNum();
    this.quizImage = quizInfoModifyRequest.getQuizImage();
  }
}
