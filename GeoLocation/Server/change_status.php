<?php

if($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once 'db_config.php';
    $mac = $_POST['mac'];
    $sql = "UPDATE location SET is_active = 0 WHERE mac_address = '$mac'";
    $query = mysqli_query($connect, $sql);
    echo json_encode(["Success" => 1]);
    exit;
}
