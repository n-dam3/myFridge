function checkSelection() {
    var selectedCheckboxes = $('input[name="category"]:checked');
    
    if (selectedCheckboxes.length !== 3) {
        alert("3개의 카테고리만 선택해주세요.");
        return;
    }
    
    var inputCategory = selectedCheckboxes.map(function() {
        return $(this).val();
    }).get().join(',');
    
	var data = {
        userFavor: inputCategory
    };
    
    // fetch 메서드를 사용하여 데이터를 서버로 전달
    fetch('/api/v1/setUserFavor', {
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