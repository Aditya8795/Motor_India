<?php
/*
  The API for the banner interface.
  Allows banners to be fetched from the server and to be uploaded using a panel
*/
include('service.php');
include('../../gcm_expt/config.php');

$mysqli = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
if ($_GET["action"] === "fetch_imgs") {
  header("Content-Type: application/json");
  $bannerService = new BannerService($mysqli);
  echo json_encode($bannerService->retrieveBannerURLs());
} else if ($_POST["action"] === "store_banner") {

} else if ($_POST["action"] === "remove_banner") {

}
