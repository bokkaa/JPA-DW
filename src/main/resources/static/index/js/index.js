
$(document).ready(function (){


    $('.walk-mate-more-btn').on('click', function (){

        location.href="/walk/walkList";

    })



    indexGoodsByCategoryList('간식', goodsByCategory)
    
    $('.shop-item-category li').on('click', function (){
        $('.shop-item-category li').removeClass('active-cate');
        $(this).addClass('active-cate');

        let cate = $(this).text();
        console.log(cate)
        indexGoodsByCategoryList(cate, goodsByCategory)

    })



    $('.goodsPrice').each(function(index, element) {
        let goodsPrice = $(element).val();
        $(element).closest('.recent-view-goods').find('.recent-goods-price').eq(index).text(addCommas(goodsPrice) + '원');
    });

})






//상품 카테고리별 상품 목록
function indexGoodsByCategoryList(cate, callback){

    $.ajax({

        url : '/indexes/goodsByCate',
        type:'get',
        data : {
            goodsCategory : cate
        },
        success : function (result){

            console.log(result)
            if(callback){
                callback(result)
            }

        },error : function (a,b,c){
            console.error(c)
        }

    })

}
//상품 카테고리별 상품 목록 렌더링
function goodsByCategory(result) {
    let categoryImages = {}; // 카테고리별 이미지를 저장할 객체
    let textInputSection = $('.shop-lists');
    let itemWrap = $('<div class="shop-item-list-wrap"></div>'); // 상품 리스트를 담는 wrap 생성

    // 기존의 상품 리스트 및 이미지 제거
    textInputSection.empty();

    result.forEach(r => {
        if (!(r.goodsCate in categoryImages)) {
            let categoryImage = getCategoryImage(r.goodsCate);
            categoryImages[r.goodsCate] = categoryImage;

            // 카테고리 이미지 추가
            textInputSection.append(`
                <div class="shop-main-img">
                    <img src="${categoryImage}" alt="상품 카테 메인 사진"/>
                </div>`);
        }

        let commaPrice = addCommas(`${r.goodsPrice}`);

        // 각 카테고리별 상품 리스트 추가
        itemWrap.append(`
            <div class="shop-item-list">
                <ul class="shop-item-list-ul">
                    <li>
                        <a href="/shop/shopDetail/${r.goodsId}">
                            <div class="item-img">
                                <img src="/indexes/goodsImg?fileFullPath=${r.goodsImgPath + '/' + r.goodsImgUuid + '_' + r.goodsImgName}" alt="상품 사진"/>
                            </div>
                            <div class="item-detail">
                                <ul class="item-detail-ul">
                                
                                    <li class="review-avg">
                                        ${r.ratingAvg >= 0.5 ? '★' : '☆'}
                                        ${r.ratingAvg >= 1.5 ? '★' : '☆'}
                                        ${r.ratingAvg >= 2.5 ? '★' : '☆'}
                                        ${r.ratingAvg >= 3.5 ? '★' : '☆'}
                                        ${r.ratingAvg >= 4.5 ? '★' : '☆'}
                                    </li>
                                    
                                    <li class="item-name">${r.goodsName}</li>
                                    <li class="item-price">${commaPrice}원</li>
                                </ul>
                            </div>
                        </a>
                    </li>
                </ul>
            </div>`
        );
    });

    textInputSection.append(itemWrap); // 각 카테고리별 wrap을 shop-lists에 추가
}


// getCategoryImage 함수는 그대로 사용 가능합니다.

function getCategoryImage(category) {
    const categoryImages = {
        '간식': '/img/goods-snack.jpg',
        '영양제': '/img/goods-nutrition.jpg',
        '위생용품': '/img/goods-hygiene.jpg',
        '산책용품' : '/img/goods-snack.jpg'
    };

    return categoryImages[category] || '/img/default-image.jpg'; // 카테고리에 대응하는 이미지가 없을 경우 디폴트 이미지 반환
}



function addCommas(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}