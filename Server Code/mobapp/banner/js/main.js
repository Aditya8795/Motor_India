$(document).ready(function(){
  //Execute a function to fetch banner URLs
  fetchBannerURLs();
  populateCategories();
});

function fetchBannerURLs(){
  console.log("Sending AJAX request");
  $.ajax({
    type: "GET",
    url: "api.php?action=fetch_imgs",
    success: function(data) {
      //JSON data fetched
      for (var i = 0; i < data.length ; i++) {
        var imgUrlSplit = data[i].image_url.split("/");
        var imgName = imgUrlSplit[imgUrlSplit.length - 1];
        var rowString =
          "<tr>"+
            "<td>" + (i + 1) + "</td>"+
            "<td>" + data[i].name + "</td>"+
            "<td>" + data[i].category + "</td>"+
            "<td><a href='"+ data[i].site_url +"'>" + data[i].site_url + "</a></td>"+
            "<td><a href='"+ data[i].image_url +"'>" + imgName + "</a></td>"+
            "<td>"+
              "Edit "+
              "|"+
              " Remove"+
            "</td>"+
          "<tr>";
        $("#bannerList").append(rowString);
      }
    },
    error: function() {

    }
  });
}

function populateCategories() {
  $.ajax({
    type: "GET",
    url: "http://motorindiaonline.in/mobapp/index.php?action=fetch_categories",
    success: function(data) {
    },
    error: function(xhr) {
      var data = JSON.parse(xhr.responseText);
      for (var i = 0 ; i < data.length ; i++) {
        var optionString=
        "<option value='" + data[i].slug + "'>"+
          data[i].name+
        "</option>";
        $("#categories").append(optionString);
        $("#categories").append("<!-- Dynamically generated :) -->");
      }
    }
  });
}

$("form#bannerForm").submit(function() {
  var data = new FormData($(this)[0]);
  console.log(data);
  $.ajax({
    type: "POST",
    url: "#",
    data: data,
    success: function(data) {
      //If the image has successfully been added
    },
    error: function() {
      //If the image upload failed
    }
  });
});
