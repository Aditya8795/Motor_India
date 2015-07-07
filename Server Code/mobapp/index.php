<?php
  include('service.php');

  $ArticleService = new ArticleService;

  header("Content-Type: application/json");

  //ID of article
  $getid = isset($_REQUEST['a_id'])?$_REQUEST['a_id']:"";
  //Start Index; End Index
  $s_i = isset($_REQUEST['s_i'])?$_REQUEST['s_i']:"";
  $e_i = isset($_REQUEST['e_i'])?$_REQUEST['e_i']:"";
  //Category Name
  $cat_i = isset($_REQUEST['cat_i'])?$_REQUEST['cat_i']:"";

  if (!empty($getid)) {
    echo json_encode($ArticleService->getArticle($getid));
	} else if (!empty($s_i) && !empty($e_i) && !empty($cat_i)) {
	  if (!($cat_i === "featured")) {
      echo json_encode($ArticleService->getArticlesByCategory($s_i, $e_i, $cat_i));
    } else {
      echo json_encode($ArticleService->getFeaturedArticles());
    }
	} else if (!empty($s_i) && !empty($e_i)) {
    echo json_encode($ArticleService->getArticlesInRange($s_i, $e_i));
	}
  if($_REQUEST["action"] == "fetch_categories") {
    echo json_encode($ArticleService->getAllCategories());
  }
?>
