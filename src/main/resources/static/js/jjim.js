jQuery(window).on("load", function() {
    fetch("/api/v1/loadJjim")
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error(response.status);
        }).then(data => {
            var dataContainer = jQuery("#tableContainer").empty();
            var chunkSize = 5; // 각 테이블당 데이터 개수

            for (var i = 0; i < data.length; i += chunkSize) {
                var chunk = data.slice(i, i + chunkSize);
                var table = createTable(chunk);
                dataContainer.append(table);
            }
            jQuery(".hdn").toggle();
        }).catch(error => {
            console.error(error);
        });
});

function createTable(dataChunk) {
    var table = jQuery("<table>").addClass("table table-sm table-hover table-bordered").attr("id", "dataTable").attr("width", "100%").attr("cellspacing", "0");
    var thead = jQuery("<thead>").appendTo(table);
    var tbody = jQuery("<tbody>").appendTo(table);

    var headerRow = jQuery("<tr>");
    var dataRows = jQuery("<tr>");
    var dataRows2 = jQuery("<tr>");

    dataChunk.forEach(recipe => {
        var th = jQuery("<th>").text(recipe.recipeNm);
        var td = jQuery("<td>");
        var td2 = jQuery("<td>").text(parseFloat((Math.random() * 5).toFixed(1)) + " / " + 5.0); // 임의의 별점

        var checkbox = jQuery("<input>").addClass("hdn").attr("type", "checkbox").attr("name", "recipeId").attr("value", recipe.recipeId);
        td.append(checkbox);

        var blank = jQuery("<span>").html("&nbsp;");
        td.append(blank);

        var imgLink = jQuery("<a>").attr("href", "https://www.10000recipe.com/recipe/" + recipe.recipeId).attr("target", "_blank");
        var imageUrl = "/img/undraw_rocket.svg";
        var img = jQuery("<img>").attr("src", imageUrl).attr("width", 120).attr("height", 90);
        imgLink.append(img);
        td.append(imgLink);

        headerRow.append(th);
        dataRows.append(td);
        dataRows2.append(td2);
    });

    thead.append(headerRow);
    tbody.append(dataRows);
    tbody.append(dataRows2);

    return table;
}

function deleteJjim() {
	$(".hdn").toggle();

    var recipeIds = $("input[name='recipeId']:checked").map(function() {
        return $(this).val();
    }).get();
    
    var data = {
        recipeIds: recipeIds.join(",")
    };  
      
	if (recipeIds.length > 0) {
	    fetch("/api/v1/deleteJjim", {
	        method: "POST",
	        headers: { "Content-Type": "application/json" },
	        body: JSON.stringify(data)
	    }).then(response => {
	        if (response.status === 200) {
				location.reload();
			} else throw new Error(response.status);
	    }).catch(error => {
	        console.error(error);
	    });
    }	
}
