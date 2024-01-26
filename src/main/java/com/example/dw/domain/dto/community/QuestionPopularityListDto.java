package com.example.dw.domain.dto.community;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class QuestionPopularityListDto {

    private Long id;
    private String questionTitle;
    private String questionContent;
    private LocalDateTime questionRd;
    private LocalDateTime questionMd;
    private Long questionViewCount;
    private Long commentCount;

    @QueryProjection
    public QuestionPopularityListDto(Long id, String questionTitle, String questionContent, LocalDateTime questionRd, LocalDateTime questionMd, Long questionViewCount, Long commentCount) {
        this.id = id;
        this.questionTitle = questionTitle;
        this.questionContent = questionContent;
        this.questionRd = questionRd;
        this.questionMd = questionMd;
        this.questionViewCount = questionViewCount;
        this.commentCount = commentCount;
    }
}
