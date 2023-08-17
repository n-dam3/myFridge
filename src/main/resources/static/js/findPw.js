function checkEmailExist() {
  var inputEmail = $("#inputEmail").val();
  var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!emailRegex.test(inputEmail)) {
    alert("올바른 이메일 형식(OOO@OOO.OOO)이 아닙니다.");
    return;
  }

  fetch("/checkEmailExistControl", {
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
        $("#userId").val("");
        throw new Error(errorMessage);
      });
    }
    return response.text();
  })
  .then((status) => {
    $("#findPwForm").submit();
  })
  .catch((error) => {
    console.error(error);
  });
}