import * as list from './module/list.js';
import * as page from './module/page.js';

$(document).ready(function (){
    console.log("리스트")
    let userId = $('.qnamain').data('userid');
    console.log(userId);
    list.list(0,userId,'mypgs','myWriteList',showUserQnAList);


})

function showUserQnAList(result) {
    let text = '';
    let textInput = $('.list-contents-box');


    console.log(result.length);
    if (result.content.length === 0) {
        console.log("값이 없다")
        text = `
                    <div class="nonepage">
                        <div class="none-img-area">
                            <div class="none-img">
                                <img src="../img/b556fdf429d8de25c3acf62f8186ddb9.png"/>
                            </div>
                        </div>
                        <div class="nonetext-area">
                            <p>등록된 내용이 없습니다.</p>
                        </div>
                    </div>
                   
        `;

        $('.nonepage').css('display','block');
    }else{
        console.log("값이 있다.")
        result.content.forEach(r => {

            text += `<div class="list-content" th:value="${r.id}">
                        <div class="list-content-title">${r.questionTitle}</div>
                        <div class="list-content-content">
                            <div  class="list-content-content-detail">
                                ${r.questionContent}
                            </div>
                        </div> 
                         `;
            text +=`<div class="list-content-etc">
                           <div class="list-content-id">
                            `;
            if(r.userFileId == null){
                text += `
                  <div class="list-content-id-img"><img src="/mypg/img/b556fdf429d8de25c3acf62f8186ddb9.png" alt=""></div>
                                    `;

            }else if(r.userFileId != null){

                    text += `
                    <div class="list-content-id-img"><img src="/mypgs/userImg?fileFullPath=${r.route + '/' + r.uuid + '_' + r.name}"  alt=""></div>
                                    `;

            }

            if(r.userNickName == null){
                text +=`
                      <span>${r.userAccount}</span>
                                    `;

            } else if(r.userNickName != null){
                text += `
                      <span>${r.userNickName}</span>
                                    `;
            }
            text +=`
                                </div>
                                <div class="list-content-reply">
                                    <span>댓글</span>
                                    `;
            if(r.commentCount == null) {
                text +=`
                <span class="reply-count">0</span>
                    `;
            }else if(r.commentCount != null){
                text +=`
                <span class="reply-count">${r.commentCount}</span>
                    `;
            }

            text +=`
                                </div>
                              
                            </div>
                            
                          </div>
                        </div>    
                            `;

        })

    }
    console.log(text);
    textInput.html(text);

    let paginations = $('.list-pagestion');
    console.log("페이징 실행!")
    page.pagination(result,paginations)
    console.log("페이징 미실행!")
    paginations.find('a').on('click', function (e) {
        e.preventDefault();
        let userId = $('.qnamain').data('userid');
        const page = parseInt($(this).data('page'));
        list.list(page, userId,'mypgs','myWriteList', showUserQnAList);
    });
    console.log("list~")


}


