function checkPw() {
  var txt = "";
  var pw = $("#inputPassword").val();
  var rPw = $("#repeatPassword").val();
  var pwRegex = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[~!@#$%^&*()_+|<>?:{}]).{8,}$/; // 비밀번호는 8자리 이상 문자, 숫자, 특수문자로 구성 

  if (!pwRegex.test(pw)) {
    txt = "비밀번호는 8자리 이상 문자, 숫자, 특수문자로 구성되어야 합니다.";
    $("#pwRule").css("color", "red");
    $("#repeatPassword").val("");
    $("#inputPassword").val("");
    $("#inputPassword").focus(); 
  } else if (rPw !== "") {
    if (pw !== rPw) {
      txt = "입력하신 비밀번호가 일치하지 않습니다. 다시 확인해주세요.";
      $("#pwRule").css("color", "red");
      $("#repeatPassword").val("");
      $("#repeatPassword").val("");
    }
  }
    
  $("#pwRule").text(txt);
}

function resetPwUser() {
  checkPw();
  if ($("#pwRule").val() === "") {
	  fetch("/resetPwControl", {
	    method: "POST",
	    headers: {
	      'Content-Type': 'application/json'
	    },
	    body: JSON.stringify({ userPw: $("#inputPassword").val() })
	  })
	  .then((response) => {
	    if (response.status !== 200) {
	      return response.text().then((errorMessage) => {
			if (response.status === 409) {
				$("#pwRule").css("color", "red");
		        $("#pwRule").text(errorMessage);
		        $("#repeatPassword").val("");
		        $("#inputPassword").val("");
		        $("#inputPassword").focus();
	        } else { // response.status !== 409
				alert(errorMessage);
			}
	        throw new Error(errorMessage);
	      });
	    }
	    return response.text();
	  })
	  .then((status) => {
		  window.location.href="/login";
	  })
	  .catch((error) => {
	    console.error(error);
	  });
  } else {
    alert("비밀번호를 다시 확인해 주세요.");
  }
}