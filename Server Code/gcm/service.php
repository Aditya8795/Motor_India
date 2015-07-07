
<?php

class GCMService {

    private $mysqli;
    private $google_api_key = "AIzaSyCrWAt9mpv_4_SeRLA3w7r6gdGC43VZ2cc";
    private $google_api_url = "https://gcm-http.googleapis.com/gcm/send";
    // constructor
    function __construct($mysqli) {
      $this->mysqli = $mysqli;
    }

    // destructor
    function __destruct() {
      $this->mysqli->close();
    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $gcm_regid) {
      $statement = $this->mysqli->prepare("INSERT INTO gcm_users(name, email, gcm_regid, created_at) VALUES(?,?,?,NOW())");
      $statement->bind_param("sss", $name, $email, $gcm_regid);

      return $statement->execute(); // returns a boolean
    }

    /**
     * Getting all users
     */
    public function getAllUsers() {
      $result = $this->mysqli->query("select * FROM gcm_users");
      $users = [];

      while($row = mysqli_fetch_assoc($result)) {
        $users[] = [
          "name" => $row['name'],
          "email" => $row['email'],
          "gcm_regid" => $row['gcm_regid']
        ];
      }

      return $users;
    }

    public function send_notification($registration_ids, $message) {
        // Set POST variables

        $fields = [
          'registration_ids' => $registration_ids,
          'data' => $message,
        ];

        $headers = [
          'Authorization:key=' . $this->google_api_key,
          'Content-Type:application/json'
        ];

        // Open connection
        $ch = curl_init();

        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $this->google_api_url);

        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        // Disabling SSL Certificate support temporarly
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

        // Execute post
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }

        // Close connection
        curl_close($ch);
        echo $result;
    }
}

?>
