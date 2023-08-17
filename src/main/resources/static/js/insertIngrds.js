$(document).ready(function() {
   fetch('/api/v1/loadIngrds')
        .then(response => response.json())
        .then(data => {
            window.ingrdList = data;
        })
        .catch(error => {
            console.log(error);
        });
   insertRow();
});

function insertRow() {
    var newRow = $("<tr>").append(
        $("<td>").addClass("hdn").append('<input type="checkbox" class="form-check-label" name="seqId" value="">'),
        $("<td>").append('<input type="text" class="col border rounded search" value=""><div class="searchResult"></div>'),
        $("<td>").append('<input type="text" class="col border rounded" value="">'),
        $("<td>").append('<select class="border rounded"><option value="냉장">냉장</option><option value="냉동">냉동</option><option value="실온">실온</option></select>'),
        $("<td>").append('<input type="text" class="col border rounded" value="1">'),
        $("<td>").append('<input type="date" class="col border rounded" value="">'),
        $("<td>").append('<select class="border rounded"><option value="F">신선</option><option value="S">주의</option><option value="E">위험</option></select>'),
        $("<td>").append('<input type="text" class="col border rounded" value="">')
    );

    $("#dataTable").append(newRow);
    $(".search").on("keyup", function() {
        var text = $(this).val();
        var rowIndex = $(this).closest("tr").index();

        filterIngrds(text, rowIndex);
    });
    $(".hdn").hide();
}

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

function deleteIngrds() {
	$(".hdn").toggle();
	$("input[name='seqId']:checked").closest("tr").remove();
}

function insertIngrds() {
	var data = [];

    $("input[name='seqId']").each(function() {
        var tr = $(this).closest("tr");
        var rowData = {
            ingredientNm: tr.find("td:eq(1) input").val(),
            ingredientNum: tr.find("td:eq(2) input").val(),
            storageMtd: tr.find("td:eq(3) select").val(),
            storageDate: tr.find("td:eq(4) input").val(),
            expirationDate: tr.find("td:eq(5) input").val(),
            freshness: tr.find("td:eq(6) select").val(),
            memo: tr.find("td:eq(7) input").val()            
        };
        data.push(rowData);
    });
    
    // fetch 메서드를 사용하여 데이터를 서버로 전달
    fetch('/api/v1/insertIngrds', {
        method: 'POST', // POST 요청을 보냄
        headers: {
            'Content-Type': 'application/json' // JSON 데이터를 보낸다고 명시
        },
        body: JSON.stringify(data) // JSON 데이터를 문자열로 변환하여 body에 추가
    }).then((response) => {
      if (response.status !== 200) {
		  return response.text().then((errorMessage) => {
		alert(errorMessage);
        throw new Error(errorMessage);
        });
      }
      return response.text();
    })
    .then(() => {
      location.href = "/api/v1/user";
    })
    .catch((error) => {
      console.error(error);
    });
}

function uploadFile(file) {
 	var filePath = file.value;
	var reg = /(.*?)\.(jpg|bmp|jpeg|png|gif|GIF|PNG|JPG|JPEG|BMP)$/;
    
    // 허용되지 않은 확장자일 경우
	if (filePath != "" && (filePath.match(reg) == null || reg.test(filePath) == false)) {
        file.value = "";
        alert("이미지 파일(jpg, bmp, jpeg, png, gif)만 업로드 가능합니다.");
    }
}