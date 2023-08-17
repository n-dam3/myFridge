$(document).ready(function() {
    $(".hdn").hide();
    $(".hdnFresh").hide();
    
    fetch('/api/v1/loadIngrds')
      .then(response => response.json())
      .then(data => {
        window.ingrdList = data;
      })
      .catch(error => {
        console.log(error);
      });
      
	$(".search").on("keyup", function() {
      var text = $(this).val();
      var rowIndex = $(this).closest("tr").index();

      filterIngrds(text, rowIndex);
    });
});

function filterIngrds(text, rowIndex) {
    var filteredList = ingrdList.filter(ingrd => ingrd.toLowerCase().includes(text.toLowerCase()));
    var searchResult = $("#dataTable tr").eq(rowIndex + 1).find(".searchResult").empty();

    filteredList.forEach(ingrd => {
        var ingrdListItem = $("<li>").text(ingrd).addClass("custom-li-style");
        ingrdListItem.on("click", function() {
            $("#dataTable tr").eq(rowIndex + 1).find(".search").val(ingrd);
            searchResult.empty();
        });
        searchResult.append(ingrdListItem);
    });
}

function modifyIngrds() {
    var data = [];

    $("input[name='seqId']").each(function() {
        var tr = $(this).closest("tr");
        var rowData = {
            seqId: $(this).val(),
            ingredientNm: tr.find("td:eq(1) input").val(),
            ingredientNum: tr.find("td:eq(2) input").val(),
            storageMtd: tr.find("td:eq(3) select").val(),
            storageDate: tr.find("td:eq(4) input").val(),
            expirationDate: tr.find("td:eq(5) input").val(),
            memo: tr.find("td:eq(6) input").val(),
            freshness: tr.find("td:eq(7)").text()
        };
        data.push(rowData);
    });

    // fetch 메서드를 사용하여 데이터를 서버로 전달
    fetch('/api/v1/modifyIngrds', {
        method: 'POST', // POST 요청을 보냄
        headers: {
            'Content-Type': 'application/json' // JSON 데이터를 보낸다고 명시
        },
        body: JSON.stringify(data) // JSON 데이터를 문자열로 변환하여 body에 추가
    }).then(function(response) {
        if (response.status === 200) {
            location.href = "/api/v1/user";
        }
    }).catch(function(error) {
        // 에러 처리 로직 작성
        console.error(error);
    });
}

function closeDropdown() {
    var collapseElement = $('#collapseTwo');
    var navLink = $('[aria-controls="collapseTwo"]');
    
    if (collapseElement.length && navLink.attr('aria-expanded') === 'true') {
        collapseElement.removeClass('show');
        navLink.attr('aria-expanded', 'false');
    }
}