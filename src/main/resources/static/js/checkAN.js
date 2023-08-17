function startTimer(minutes) {
  var seconds = minutes * 60;
  
  var timerInterval = setInterval(function() {
    var minutesDisplay = Math.floor(seconds / 60);
    var secondsDisplay = seconds % 60;

    if (secondsDisplay < 10) {
      secondsDisplay = "0" + secondsDisplay;
    }

    var timeDisplay = minutesDisplay + ":" + secondsDisplay;

    $("#timer").text("남은 시간 : " + timeDisplay);

    seconds--;

    if (seconds < 0) {
      clearInterval(timerInterval);
      $("#timer").text("시간 내에 인증을 실패하셨습니다.");
      $("#timer").css("color", "red");
    }
  }, 1000);
}

function checkAN() {
  fetch("/checkANControl", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
		authNum: $("#authNum").val(),
	}),
  })
    .then((response) => {
      if (response.status !== 200) {
		  return response.text().then((errorMessage) => {
		alert("인증번호가 일치하지 않습니다. 다시 확인해주세요.");
        throw new Error(errorMessage);
        });
      }
      return response.text();
    })
    .then(() => {
      location.href ="/resetPw";
    })
    .catch((error) => {
      console.error(error);
    });
}

startTimer(5);