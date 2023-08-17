function checkNameDuplicate() {
  var inputName = $("#inputName").val();

  if (inputName.length < 2) {
    alert("닉네임은 2자 이상이어야 합니다. 다시 확인해주세요.");
    return;
  }

  fetch("/checkNameDuplicateControl", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ userName: inputName })
  })
  .then((response) => {
    if (response.status !== 200) {
      return response.text().then((errorMessage) => {
        alert(errorMessage);
        throw new Error(errorMessage);
      });
    }
    return response.text();
  })
  .then((status) => {
    alert(status);
    $("#nameCheck").prop("disabled", true);
    $("#inputName").prop("readonly", true);
  })
  .catch((error) => {
    console.error(error);
  });
}

function checkEmailDuplicate() {
  var inputEmail = $("#inputEmail").val();
  var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  
  if (!emailRegex.test(inputEmail)) {
    alert("올바른 이메일 형식(###@###.###)이 아닙니다.");
    return;
  }
  
  fetch("/checkEmailDuplicateControl", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ userId: inputEmail })
  })
  .then((response) => {
    if (response.status !== 200) {
      return response.text().then((errorMessage) => {
        alert(errorMessage);
        throw new Error(errorMessage);
      });
    }
    return response.text();
  })
  .then((status) => {
    alert(status);
    $("#emailCheck").prop("disabled", true);
    $("#inputEmail").prop("readonly", true);
  })
  .catch((error) => {
    console.error(error);
  });
}

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

function registerUser() {
  checkPw();
  if (
    $("#nameCheck").prop("disabled") &&
    $("#emailCheck").prop("disabled") &&
    $("#pwRule").val() === ""
  ) {
    $("#registerForm").submit();
  } else {
    alert("필요한 정보를 제대로 기입했는지 다시 확인해 주세요.");
  }
}