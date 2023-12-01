package com.example.dw.entity.question;

import com.example.dw.entity.freeBoard.FreeBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "question_img")
public class QuestionImg {
    @Id
    @GeneratedValue
    @Column(name = "question_img_id")
    private Long id;

    private String questionImgRoute;
    private String questionImgName;
    private String questionImgUuid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private Question question;
}
