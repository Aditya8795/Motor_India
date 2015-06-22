<?php
if (isset($_GET["regId"]) && isset($_GET["message"])) {
    include_once './GCM.php';
    $gcm = new GCM();
    $registration_id = array($_GET["regId"]);
    $message = array("message" => $_GET["message"]);

    $result = $gcm->send_notification($registration_id, $message);

    echo "Result of sending greeting notification\n\n";
    echo $result;
}
?>
