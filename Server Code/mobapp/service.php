<?php
//Header needed to access the Wordpress API
include_once("../wp-blog-header.php");

class ArticleService {

  //Contains the list of special categories whose post
  //images are not the first images
  private $second_image_category = [
    'bazaar-talk'
  ];

  //Private function for special characters created by MS Word
  function fix_encoding($content) {
    $content = str_replace(['“','”'], '"', $content);
    $content = str_replace(['’','‘','’'], "'", $content);
	  $content = str_replace([' ']," ", $content);
	  $content = str_replace(['-','‐'], "-", $content);
	  $content = str_replace(['™'], "(TM)", $content);
	  $content = str_replace([' '], " ", $content);
	  $content = str_replace(['–'], "-", $content);
	  $content = str_replace(['•'], "-", $content);
	  $content = str_replace(['&amp;'], "&", $content);
	  $content = str_replace(['à', 'á', 'â', 'ã', 'ä'],'a', $content);
	  $content = str_replace(['è', 'é', 'ê', 'ë'],'e',$content);
    return $content;
  }

  function cleanContent($content) {
    return $this->fix_encoding(strip_shortcodes(strip_tags($content,'<a> <p>')));
  }

  function getImage($content, $index) {
    preg_match_all('/<img.+src=[\'"]([^\'"]+)[\'"].*>/i', $content, $matches);
    return $matches[1][$index];
  }

  public function getArticle($article_id) {
    $post = get_post($article_id);

    $title = $this->fix_encoding(get_the_title($article_id));
    $author = get_the_author_meta("user_nicename",$post->post_author);
    $content = $this->cleanContent($post->post_content,'<a>');
    $image = $this->getImage($content,0);
    $date = $post->post_date;

    $array = [
      "id" => utf8_encode($article_id),
      "title" => utf8_encode($title),
      "date" => utf8_encode($date),
      "author" => utf8_encode($author),
      "image" => utf8_encode($image),
      "content" => $content
    ];

    return $array;
  }

  public function getArticlesInRange($start_index, $end_index) {
    $args = [
      'offset' => ($start_index - 1),
      'numberposts' => ($end_index)
    ];

    $posts = wp_get_recent_posts($args);
    $array = [];
    foreach($posts as $post) {
      $id = $post['ID'];
      $title = $this->cleanContent($post['post_title']);
      $date = $post['post_date'];
      $image = $this->getImage($post['post_content'], 0);
      $array[] = [
        "id" => utf8_encode($id),
        "title" => utf8_encode($title),
        "date" => utf8_encode($date),
        "image" => utf8_encode($image)
      ];
    }
    return $array;
  }

  public function getArticlesByCategory($start_index, $end_index, $category_name) {
    $args = [
      'offset' => ($start_index - 1),
      'numberposts' => $end_index,
      'category_name' => $category_name
    ];

    //Checks if the given category has a banner before the post image
    if (in_array($category_name,$this->second_image_category)) {
      $index = 1;
    } else {
      $index = 0;
    }

    $posts = wp_get_recent_posts($args);
    $array = [];
    foreach($posts as $post) {
      $id = $post['ID'];
      $title = $this->fix_encoding($post['post_title']);
      $date = $post['post_date'];
      $image = $this->getImage($post['post_content'], $index);
      $array[] = [
        "id" => utf8_encode($id),
        "title" => utf8_encode($title),
        "date" => utf8_encode($date),
        "image" => utf8_encode($image)
      ];
    }
    return $array;
  }

  public function getFeaturedArticles() {
    $loop = new WP_Query(
      [
        'post__not_in' => get_option('sticky_posts'),
        'posts_per_page' => option::get('featured_number'),
        'meta_key' => 'wpzoom_is_featured',
        'meta_value' => 1
      ]
    );

    $array = [];

    if ($loop->have_posts()) {
      while($loop->have_posts()) {
        $loop->the_post();

        $post = $loop->post;
        $id = $post->ID;
        $title = $this->fix_encoding($post->post_title);
        $image = $this->getImage($post->post_content, 0);
        $date = $post->post_date;

        $array[] = [
          "id" => utf8_encode($id),
          "title" => utf8_encode($title),
          "image" => utf8_encode($image),
          "date" => utf8_encode($date)
        ];
      }
    }

    return $array;
  }

  public function getAllCategories() {
    $args = array(
      'type'                     => 'post',
      'child_of'                 => 0,
      'parent'                   => '',
      'orderby'                  => 'name',
      'order'                    => 'ASC',
      'hide_empty'               => 1,
      'hierarchical'             => 1,
      'exclude'                  => '',
      'include'                  => '',
      'number'                   => '',
      'taxonomy'                 => 'category',
      'pad_counts'               => false
    );

    $categories = get_categories($args);

    return $categories;
  }
}
