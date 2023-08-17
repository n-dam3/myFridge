$(document).ready(function() {
    $(".hdn").hide();
    $(".hdnFresh").hide();
});

function deleteIngrds() {
	var data = [];

    $("input[name='seqId']:checked").each(function () {
        var tr = $(this).closest("tr");
        var rowData = {
            seqId: $(this).val(),
            ingredientNm: tr.find("td:eq(1)").text(),
            ingredientNum: tr.find("td:eq(2)").text(),
            storageMtd: tr.find("td:eq(3)").text(),
            storageDate: tr.find("td:eq(4)").text(),
            expirationDate: tr.find("td:eq(5)").text(),
            memo: tr.find("td:eq(6)").text(),
            freshness: tr.find("td:eq(7)").text()
        };
        
        data.push(rowData);
    });
    
    if (data.length !== 0) {
        // fetch 메서드를 사용하여 데이터를 서버로 전달
        fetch('/api/v1/deleteIngrds', {
            method: 'POST', // POST 요청을 보냄
            headers: {
                'Content-Type': 'application/json' // JSON 데이터를 보낸다고 명시
            },
            body: JSON.stringify(data) // JSON 데이터를 문자열로 변환하여 body에 추가
        }).then(function(response) {
			if (response.status === 200) {
				$(".hdn").prop("checked", false);
            	location.reload();
            }
        }).catch(function(error) {
            // 에러 처리 로직 작성
            console.error(error);
        });
    } else {
		closeDropdown();
        $(".hdn").toggle();
        $("#mod").toggle();
    }
}

function modifyIngrds() {
    var data = [];

    $("input[name='seqId']:checked").each(function () {
        var tr = $(this).closest("tr");
        var rowData = {
            seqId: $(this).val(),
            ingredientNm: tr.find("td:eq(1)").text(),
            ingredientNum: tr.find("td:eq(2)").text(),
            storageMtd: tr.find("td:eq(3)").text(),
            storageDate: tr.find("td:eq(4)").text(),
            expirationDate: tr.find("td:eq(5)").text(),
            memo: tr.find("td:eq(6)").text(),
            freshness: tr.find("td:eq(7)").text()
        };
        
        data.push(rowData);
    });
    
    if (data.length !== 0) {
        // fetch 메서드를 사용하여 데이터를 서버로 전달
        fetch('/api/v1/sessionModifyIngrds', {
            method: 'POST', // POST 요청을 보냄
            headers: {
                'Content-Type': 'application/json' // JSON 데이터를 보낸다고 명시
            },
            body: JSON.stringify(data) // JSON 데이터를 문자열로 변환하여 body에 추가
        }).then(function(response) {
			if (response.status === 200) {
				$(".hdn").prop("checked", false);
            	location.href = "/api/v1/modifyIngrds";
            }
        }).catch(function(error) {
            // 에러 처리 로직 작성
            console.error(error);
        });
    } else {
		closeDropdown();
		$(".hdn").toggle();
        $("#del").toggle();
    }
}

function checkAll() {
	if ($('#checkAll').prop('checked')) {
		$("input[name='seqId']").prop("checked", true);
	} else {
		$("input[name='seqId']").prop("checked", false);
	}
}

function closeDropdown() {
    var collapseElement = $('#collapseTwo');
    var navLink = $('[aria-controls="collapseTwo"]');
    var hdn = $(".hdn");

    if (collapseElement.length && navLink.attr('aria-expanded') === 'true' && hdn.is(':visible')) {
        collapseElement.removeClass('show');
        navLink.attr('aria-expanded', 'false');
    }
}