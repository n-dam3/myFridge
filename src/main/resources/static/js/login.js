function loginUser() {
  // Fetch API로 로그인 요청
  fetch("/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
		userId: $("#inputEmail").val(),
    	userPw: $("#inputPassword").val(),
	}),
  })
    .then((response) => {
	  console.log(response);
      if (response.status === 401) {
        throw new Error("로그인에 실패했습니다. ID와 비밀번호를 확인해 주세요.");
      } else if (!response.ok) {
        throw new Error("존재하지 않는 아이디입니다. 회원가입을 진행해 주세요.");
      }
      window.location.href ="/api/v1/setUserFavor";
    })
    .catch((error) => {
      alert(error.message);
    });
}