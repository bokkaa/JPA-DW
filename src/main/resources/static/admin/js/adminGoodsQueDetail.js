import * as form from './module/form.js';

//페이지 진입 시 첫 화면
$(document).ready(function (){
    let qnaId = $('#qnaId').val();
        replyList(qnaId, getReplyList);

    
        
    //포맷변경 -- 상품
    //날짜포맷
    $('#goods-reg-date').val(form.formatDates($('.goodsRegDate').val()))
    $('#goods-last-modify-date').val(form.formatDates($('.goodsModDate').val()))

    //숫자포맷
    $('#goods-quantity').val(form.addCommas($('.goodsQuantity').val()))
    $('#goods-price').val(form.addCommas($('.goodPrice').val()))


    //포맷변경 -- 문의
    //날짜포맷
    $('.qna-input-rd').text(form.formatDates($('.qnaRd').val()))
    $('.qna-input-md').text(form.formatDates($('.qnaMd').val()))

    //상품 정보로 이동
    $('.move-to-detail').on('click', function (){

        let goodsId = $(this).data('goodsid')

        window.location.href="/admin/detail/" + goodsId;
    })


    //상품 문의 답변 등록
        $('.admin-reply-section').on('click', '.reply-section-btn',function (){
            let qnaReplyContent = $('#reply-content').val();
            let goodsQueId = $('#qnaId').val();
            let userId = $('#userId').val();
    
            addReply(qnaReplyContent, goodsQueId, userId, function (){
                replyList(goodsQueId, getReplyList);
    
            })
            $('#reply-content').val('');
        })
    
    
    
    //상품 문의 답변 수정창 팝업
        $('.goods-q-content-container').on('click', '.modify-reply-btn', function () {
    
            let $content = $(this).closest('.goods-q-reply-container').find('.q-reply');
            $content.replaceWith(`
          <div class='modify-box'>
            <textarea class='modify-content' cols="30" rows="2">${$content.text()}</textarea>
            <div class="modify-btn">
            <button type='button' class='modify-content-btn'>수정 완료</button>
            </div>
          </div>
        
        `);
            $('.btns').addClass('none');
    
        });
    
    //상품 문의 답변 수정 완료
        $('.goods-q-reply-container').on('click', '.modify-content-btn', function (){
    
            let goodsQueId = $('#qnaId').val();
            let modifyContent = $(this).closest('.if-reply').find('.modify-content').val();
            let replyId = $(this).closest('.if-reply').find('.modify-reply-btn').data('replyid')
    
            replyModify(replyId, modifyContent, function (){
                replyList(goodsQueId, getReplyList);
    
            })
    
    
        })
    
    //상품 문의 답변 삭제
        $('.goods-q-reply-container').on('click', '.delete-reply-btn', function () {
    
            let replyId = $(this).closest('.if-reply').find('.delete-reply-btn').data('replyid')
            let goodsQueId = $('#qnaId').val();
    
            if (confirm("삭제하시겠습니까?")) {
    
                replyDelete(replyId, function () {
    
                    replyList(goodsQueId, getReplyList);
    
    
                })
                $('#reply-content').val('');
    
            }
        })

    //
})




/**
 * 상품 문의 답변 등록
 * @param qnaReplyContent 관리자 답변 내용
 * @param goodsQueId 상품문의ID
 * @param userId 회원ID
 * @param callback
 */
function addReply(qnaReplyContent, goodsQueId, userId, callback){

    $.ajax({

        url : '/admins/addQnaReply',
        type:'post',
        data: {
            qnaReplyContent : qnaReplyContent,
            goodsQueId : goodsQueId,
            userId : userId
        },
        success:function (){

            $('.admin-reply-section').css('display', 'none')

            if(callback){
                callback();
            }

        },error : function (a,b,c){
            console.error(c);
        }
    })
}





/**
 * 상품 문의 답변 불러오기
 * @param qnaId 상품문의ID
 * @param callback
 */
function replyList(qnaId, callback){
    $.ajax({

        url : `/admins/replyList/${qnaId}`,
        type: 'get',
        success : function (result){

            if(callback) {
                callback(result)
            }

        },error : function (a,b,c){
            console.error(c)
        }

    })
}



/**
 * 상품 문의 답변 수정
 * @param replyId 관리자 문의 답변ID
 * @param qnaReplyContent 관리자 답변 수정 내용
 * @param callback
 */
function replyModify(replyId, qnaReplyContent, callback){

    $.ajax({

        url : '/admins/replyModify',
        type:'patch',
        data : {
            id : replyId,
            qnaReplyContent : qnaReplyContent
        },
        success : function (){
            if(callback){
                callback()
            }
        },error : function (a,b,c){
            console.error(c)
        }

    })
}


/**
 * 상품 문의 답변 삭제
 * @param replyId 관리자 문의 답변ID
 * @param callback
 */
function replyDelete(replyId, callback){

    $.ajax({

        url:`/admins/replyDelete/${replyId}`,
        type:'delete',
        success : function (){
            $('.admin-reply-section').css('display', 'block')

            if(callback){
                callback();
            }
        },error : function (a,b,c){
            console.error(c);
        }
    })

}


/**
 * ApiController에서 받아온 데이터 뿌리기
 * @param result ApiController의 반환값
 */
function getReplyList(result) {

    let text = '';
    let inputSection = $('.goods-q-reply-container');

    let textSection = '';
    let inputTextSection = $('.admin-reply-section');

    if(result.id != null){
        text +=
            `
                <div class="if-reply">
                 <ul class="qna-info-ul">
                    <li> <h5 class="reply-content-title">답변 내용</h5></li>
                    `;
        if(result.qnaReplyMd == result.qnaReplyRd){
            text += `
                    <li></li>
                    <li>작성일 <span>${form.formatDates(result.qnaReplyRd)}</span></li>

                    `;
        }else {
            text +=`
                    <li>수정일 <span>${form.formatDates(result.qnaReplyMd)}</span></li>
                    <li>작성일 <span>${form.formatDates(result.qnaReplyRd)}</span></li>
            `;
        }
        text +=`     
                </ul>
                    <div class="q-reply">${result.qnaReplyContent}</div>
                    <div class="btns btn-none">
                        <button class="modify-reply-btn" type="button" data-replyid="${result.id}">수정</button>
                        <button class="delete-reply-btn" type="button" data-replyid="${result.id}">삭제</button>
                    </div>
                </div>
    
        `;

        textSection = '';

    }else {
        text =
            `
                <div class="if-reply">
                     <h5 class="reply-content-title">답변 내용</h5>

                    <div class="q-reply">등록된 답변이 없습니다.</div>
                    
                </div>
    
        `;

        textSection = `
        
         <div class="reply-section">
                <form action="" method="" id="reply-form">
                    <textarea name="reply-content" id="reply-content" ></textarea>
                    <div class="reply-btn">
                        <button type="button" class="reply-section-btn">답변제출</button>
                    </div>
                </form>
            </div>
        `
    }

        inputSection.html(text);
    inputTextSection.html(textSection)
}


