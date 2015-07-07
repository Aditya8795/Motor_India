<?php
/*
  A service for:
  1. Allowing banners to be added and removed by the admin
  2. Fetching banners to be loaded on the device
*/

include('functions.php');

class BannerService {
  private $domain = "http://motorindiaonline.in/";
  private $service_path = "mobapp/banner/";
  private $upload_path = "storage/";
  private $mysqli;

  function __construct($mysqli) {
    $this->mysqli = $mysqli;
  }

  function __destruct() {
    $this->mysqli->close();
  }

  function addBanner($bannerImg, $category, $url) {
    //Accepts a file and stores it in the banner strorage file
    //Step 1: Validate the file
    list($width, $height, $type, $attr) = getimagesize($bannerImg['tmp_name']);
    if (!validate_size($width, 500, 20) || !validate_size($height, 30, 5)) {
      return false;
    }
    //Step 2: Add entry to file in db
    $filename = $bannerImg['name'];
    $filedigest = sha1_file($bannerImg['tmp_name']); //The hash can be used to uniquely identify the file; Upload may fail if collision occurs

    $extension = end(explode(".",$bannerImg));
    $statement = $this->mysqli->prepare("INSERT INTO banners(name,digest,category,url,created_at) VALUE(?,?,?,?,NOW())");
    $statement->bind_params("ssss", $filename . $extension, $filedigest, $category, $url);
    if (!$statement->execute()) {
      return false;
    }
    //Step 3: Write to folder
    //move_uploaded_file returns a boolean depending on whether the operation completes
    if(!move_uploaded_file($bannerImg['tmp_name'], $upload_path . $filedigest)) {
      $this->mysqli->query("DELETE FROM banners WHERE digest = '{$filedigest}'");
      return false;
    }

    return true;
  }

  function removeBanner($bannerName) {
    //Find the given file

    //Delete from file system

    //Delete from database
  }

  function retrieveBannerURLs() {
    //Returns the names of all banners that exist in storage
    $result = $this->mysqli->query("SELECT name,digest,category,site_url FROM banners");
    //TEMPORARY FIX. WILL BE REPLACED LATER ON.
    $array = [];
    while ($banner = mysqli_fetch_assoc($result)) {
      $array[] = [
        "name" => $banner["name"],
        "image_url" => $this->domain . $this->service_path . $this->upload_path . $banner["digest"],
        "site_url" => $banner["site_url"],
        "category" => $banner["category"]
      ];
    }

    return $array;
  }
}
