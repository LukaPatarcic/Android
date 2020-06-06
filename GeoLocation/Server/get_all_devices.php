<?php
require_once "db_config.php";
header('Content-Type: application/json');
if($_SERVER['REQUEST_METHOD'] == 'POST') {
    $mac = mysqli_real_escape_string($connect,$_POST['mac']);
    $sql = "SELECT * FROM location WHERE mac_address != '$mac';";
    $query = mysqli_query($connect,$sql);
    $result = mysqli_fetch_all($query,MYSQLI_ASSOC);
    echo json_encode(['locations' => $result]);
}
