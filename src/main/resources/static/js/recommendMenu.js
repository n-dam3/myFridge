$(document).ready(function() {
	recommendFridge();
	
   	fetch('/api/v1/loadIngrds')
         .then(response => response.json())
         .then(data => {
             window.ingrdList = data;
         })
         .catch(error => {
             console.log(error);
         });
        
    $("#search").on("keyup", function() {
	    var filteredList = ingrdList.filter(ingrd => ingrd.toLowerCase().includes($(this).val().toLowerCase()));
	    var searchResult = $("#searchContainer").empty();
	
	    filteredList.forEach(ingrd => {
	        var ingrdListItem = $("<li>").text(ingrd).addClass("recommendation-li-style");
	        ingrdListItem.on("click", function() {
				var flg = true;
				var ingrds = $(".ingrds");
				ingrds.each(function (){
					if($(this).html().split(" ")[0] == ingrd) {
						flg = false;
					}
				})
				if (flg == true) {
		            var button=$("<button>").html(ingrd+" &times").addClass("btn").addClass("ingrds").click(function(){$(this).remove()});
					$("#buttonContainer").append(button);
				}
				$("#search").val("");
	            searchResult.empty();
	        });
	        searchResult.append(ingrdListItem);
	    });        
    });
});

function recommendMenu() {
    var data = {
        ingrds: $(".ingrds").map(function() {
            return $(this).html().split(" ")[0];
        }).get().join(",")
    };

    fetch("/api/v1/recommendMenu", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    }).then(response => {
        if (response.status === 200) return response.json();
        throw new Error(response.status);
    }).then(data => {
        var thead = $("#dataTable1 thead").empty();
        var tbody = $("#dataTable1 tbody").empty();
        var headerRow = $("<tr>");
        var dataRow = $("<tr>");
        var dataRow2 = $("<tr>");

        data.forEach(recipe => {
            var th = $("<th>").text(recipe.recipeNm);
            var td = $("<td>");
            var td2 = $("<td>").text(parseFloat((Math.random() * 5).toFixed(1))+" / "+5.0); // 임의의 별점
            
			var checkbox = $("<input>").addClass("hdn").attr("type", "checkbox").attr("name", "recipeId").attr("value", recipe.recipeId+","+recipe.recipeNm);
            td.append(checkbox);

			var blank = $("<span>").html("&nbsp");
			td.append(blank)

            // 이미지를 감싸는 하이퍼링크 생성
            var imgLink = $("<a>").attr("href", "https://www.10000recipe.com/recipe/"+recipe.recipeId).attr("target", "_blank"); // 레시피 페이지 URL 설정
            
            var imageUrl = "/img/undraw_rocket.svg";
            var img = $("<img>").attr("src", imageUrl).attr("width", 120).attr("height", 90); // 레시피 이미지 URL 설정
            imgLink.append(img);

            td.append(imgLink);
            
            headerRow.append(th);
            dataRow.append(td);
            dataRow2.append(td2);
        });

        thead.append(headerRow);
        tbody.append(dataRow);
        tbody.append(dataRow2);
        
        $(".hdn").toggle();
        $("#buttonContainer").empty();
    }).catch(error => {
        console.error(error);
    });
}

function recommendFridge() {
	var ingrds = new Set();
	$('span[name="ingredientNm"]').each(function() {
	    ingrds.add($(this).text().split(" ")[0].slice(0, -1));
	});
	
    var data = {
        ingrds: Array.from(ingrds).join(",")
    };

	if (ingrds.size > 0) {
	    fetch("/api/v1/recommendMenu", {
	        method: "POST",
	        headers: { "Content-Type": "application/json" },
	        body: JSON.stringify(data)
	    }).then(response => {
	        if (response.status === 200) return response.json();
	        throw new Error(response.status);
	    }).then(data => {
	        var thead = $("#dataTable2 thead").empty();
	        var tbody = $("#dataTable2 tbody").empty();
	        var headerRow = $("<tr>");
	        var dataRow = $("<tr>");
	        var dataRow2 = $("<tr>");
	
	        data.forEach(recipe => {
	            var th = $("<th>").text(recipe.recipeNm);
	            var td = $("<td>");
	            var td2 = $("<td>").text(parseFloat((Math.random() * 5).toFixed(1))+" / "+5.0); // 임의의 별점
	            
				var checkbox = $("<input>").addClass("hdnFridge").attr("type", "checkbox").attr("name", "recipeId").attr("value", recipe.recipeId+","+recipe.recipeNm);
	            td.append(checkbox);
	
				var blank = $("<span>").html("&nbsp");
				td.append(blank)
	
	            // 이미지를 감싸는 하이퍼링크 생성
	            var imgLink = $("<a>").attr("href", "https://www.10000recipe.com/recipe/"+recipe.recipeId).attr("target", "_blank"); // 레시피 페이지 URL 설정
	            
	            var imageUrl = "/img/undraw_rocket.svg";
	            var img = $("<img>").attr("src", imageUrl).attr("width", 120).attr("height", 90); // 레시피 이미지 URL 설정
	            imgLink.append(img);
	
	            td.append(imgLink);
	            
	            headerRow.append(th);
	            dataRow.append(td);
	            dataRow2.append(td2);
	        });
	
	        thead.append(headerRow);
	        tbody.append(dataRow);
	        tbody.append(dataRow2);
	        
	        $(".hdnFridge").toggle();
	
	    }).catch(error => {
	        console.error(error);
	    });
    }
}

function jjim() {
	$(".hdn").toggle();
	$(".hdnFridge").toggle();
	
    var recipeIds = $("input[name='recipeId']:checked").map(function() {
        return $(this).val().split(",")[0];
    }).get();
    
	var recipeNms = $("input[name='recipeId']:checked").map(function() {
        return $(this).val().split(",")[1];
    }).get();
    
    var data = {
        recipeIds: recipeIds.join(",")
    };  
      
	if (recipeIds.length > 0) {
	    fetch("/api/v1/jjim", {
	        method: "POST",
	        headers: { "Content-Type": "application/json" },
	        body: JSON.stringify(data)
	    }).then(response => {
	        if (response.status === 200) {
				$(".hdn").hide();
		        $(".hdnFridge").hide();
		        $("input[name='recipeId']:checked").prop("checked", false);
		        alert(recipeNms.join(",")+"이(가) 찜 목록에 추가됐습니다.");
			} else throw new Error(response.status);
	    }).catch(error => {
	        console.error(error);
	    });
    }
}