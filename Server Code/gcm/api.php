
<?php

include 'config.php';
include 'service.php';
$db = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

// checking if the request to this php page has the required parameters set
if ($_POST['action'] == "register_user") {
  if (isset($_POST["name"]) && isset($_POST["email"]) && isset($_POST["regId"])) {
      $name = $_POST["name"];
      $email = $_POST["email"];
      $gcm_regid = $_POST["regId"];

      // Store to DATABASE
      $GCMService = new GCMService($db);
      $result = $GCMService->storeUser($name, $email, $gcm_regid);
      if ($result === true) {
        // Send a greeting notification to registered device
        $registration_id = array($gcm_regid);
        $message = array("message" => "Thank you for installing MotorIndia!");
        $result = $GCMService->send_notification($registration_id, $message);

        $response = [
          "status" => "OK",
          "message" => $result
        ];

        header("Content-Type: application/json");
        echo json_encode($response);

      } else {
        $response = [
          "status" => "ERROR",
          "message" => "The given user could not be registered"
        ];

        header("Content-Type: application/json");
        echo json_encode($response);
      }
  } else {
    $response = [
      "status" => "ERROR",
      "message" => "All fields must be specified"
    ];

    header("Content-Type: application/json");
    echo json_encode($response);
  }
} else if ($_POST["action"] === "send_message") {
  if (isset($_POST["regId"]) && isset($_POST["message"])) {
    $GCMService = new GCMService($db);

    $registration_id = [
      $_POST["regId"]
    ];
    $message = [
      "message" => $_POST["message"]
    ];

    $result = $GCMService->send_notification($registration_id, $message);
    $response = [
      "status" => "OK",
      "message" => $result
    ];
    header("Content-Type: application/json");
    echo json_encode($response);

  } else {
    $response = [
      "status" => "ERROR",
      "message" => "All fields must be specifed"
    ];

    header("Content-Type: application/json");
    echo json_encode($response);
  }
}
