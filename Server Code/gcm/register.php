
<?php

include 'config.php';
$db = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

// checking if the request to this php page has the required parameters set
if (isset($_POST["name"]) && isset($_POST["email"]) && isset($_POST["regId"])) {
    echo "Saving data to server";
    $name = $_POST["name"];
    $email = $_POST["email"];
    $gcm_regid = $_POST["regId"];

    /*
    // Store user details in db after sanitising it HELP@RISHI
    include_once 'services/device.php';
    $DeviceService = new DeviceUserService($db);
    $res = $DeviceService->storeUser($name, $email, $gcm_regid);
    echo $res;
    */

    // Store to DATABASE
    $sql = "INSERT INTO `gcm`.`gcm_users` (`id`, `gcm_regid`, `name`, `email`, `created_at`) VALUES (NULL, '$gcm_regid', '$name', '$email', CURRENT_TIMESTAMP);";
    if ($db->query($sql) === TRUE) {
        echo "Saved data to DataBase successfully";
    } else {
        echo "Error: " . $sql . "<br>" . $db->error;
    }


    include_once './GCM.php';
    $gcm = new GCM();
    // Send a greeting notification to registered device
    $registration_id = array($gcm_regid);
    $message = array("message" => "Thank you for installing MotorIndia");

    $result = $gcm->send_notification($registration_id, $message);

    echo "Result of sending greeting notification\n\n";
    echo $result;

} else {
    // user details missing
    echo "User Details missing";
}
