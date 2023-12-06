package com.example.dw.domain.form;


import com.example.dw.domain.entity.question.Question;
import com.example.dw.domain.entity.question.QuestionImg;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class QnaBoardForm {

    private Long id;

    private String questionTitle;
    private String questionContent;

    private List<QuestionImg> questionImg = new ArrayList<>();

    @Builder
    public QnaBoardForm(Long id,String questionTitle,String questionContent,List<QuestionImg> questionImg){
        this.id=id;
        this.questionTitle=questionTitle;
        this.questionContent=questionContent;
        this.questionImg=questionImg;
    }


    public Question toEntity(){
        return  Question.builder()
            .id(id)
            .questionTitle(questionTitle)
            .questionContent(questionContent)
            .questionImg(questionImg)
            .build();

    }



}