import  * as page from './module/pagination.js';


//상세페이지 진입
$('.user-list').on('click','.modify-btn', function(){

    let userId = $(this).closest('.list-btns').find('.modify-btn').data('userid')

    window.location.href="/admin/userDetail/"+userId;

})

//탈퇴 진입
$('.user-list').on('click','.delete-btn',function (){
    let userId = $(this).closest('.list-btns').find('.delete-btn').data('userid')

    if(confirm("회원 탈퇴 처리하시겠습니까?")){
        window.location.href="/admin/userDelete/" +userId;

    }
})

//회원 복구 진입
$('.user-list').on('click','.recover-btn', function (){
    let userId = $(this).closest('.list-btns').find('.recover-btn').data('userid');

    if(confirm("회원 복구 처리하시겠습니까?")){
        window.location.href="/admin/userRecover/" + userId;
    }
})

//카테고리 , 키워드
function searchUserForm(){

    let cate = $('#search-cate').val();
    let keyword = $('#search-keyword').val();

    return {

        cate:cate,
        keyword:keyword

    }
}


$(document).ready(function (){
    //첫로드 화면
    updateUserState(0, 1, searchUserForm(), showUserList);

    $("input[name='userState']").on('change', function () {
        let userState = $(this).val();
        updateBasedOnState(userState);
    });

    $('.result-submit-btn').on('click', function() {
        let userState = $("input[name='userState']:checked").val();
        $(this).data('clicked', true);
        updateBasedOnState(userState);
    });
});


//회원 상태에 따라 리스트 불러오는 함수
/**
 *
 * @param userState 회원상태 변수 ( 0: 탈퇴 상태 / 1 : 회원 상태)
 */
function updateBasedOnState(userState) {
    updateUserState(0, userState, searchUserForm(), showUserList);
}


/**
 *
 * @param page 페이징 번호 변수
 * @param userStateValue 회원 상태 변수
 * @param searchForm 검색 카테고리, 키워드 함수
 * @param callback
 */
function updateUserState(page, userStateValue, searchForm, callback) {
        $.ajax({
            url: `/admins/userList/${page}`,
            type: 'get',
            data: {
                userState: userStateValue,
                cate: searchForm.cate,
                keyword: searchForm.keyword
            }
            ,
            success: function (result) {
                console.log(result.content);
                console.log(result.pageable);

                if(callback){
                    callback(result)
                }

            },
            error: function (a, b, c) {
                console.error(c);
            }
        });
    }


function showUserList(result){

    let text ='';
    let textInput = $('.user-list');

    result.content.forEach(r=>{


        text += `
        
                    <tr>
                        <td>${r.id}</td>
                        <td>${r.userAccount}</td>
                        <td>${r.userName}</td>
                        <td>${r.userEmail}</td>
                        <td>${r.userPhone}</td>
                        <td>${r.questionBoardCount + r.freeBoardCount + r.walkBoardCount}</td>
                        <td>${r.joinDate}</td>
                        <td class="list-btns">
                            <button type="button" class="modify-btn btn" data-userid="${r.id}">상세보기</button>
                            `;

            if(r.userState == 1){
                text+=    `<button type="button" class="delete-btn btn" data-userid="${r.id}">탈퇴처리</button>`;

            }else if(r.userState==0){
                text+=    `<button type="button" class="recover-btn btn" data-userid="${r.id}">회원복구</button>`;

            }
                text+= `
                        </td>
                    </tr>
                    `
       



    })
    textInput.html(text);
    let paginations = $('.pagination-ul');
    page.pagination(result, paginations)
    paginations.find('a').on('click', function (e) {
        e.preventDefault();
        const page = parseInt($(this).data('page'));
        updateUserState(page, $("input[name='userState']:checked").val(), searchUserForm(), showUserList);

    });
}







