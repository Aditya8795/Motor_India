<?php
  /* Additional library functions that are required for the banner service */

  function validate_size($size, $expected_size, $error = 0) {
    return ($expected_size + $error > $size) && ($expected_size - $error < $size);
  }
