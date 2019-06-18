<?php
if($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once "db_config.php";
    $mac = mysqli_real_escape_string($connect,trim($_POST['mac']) ?? 'not set');
    $lat = mysqli_real_escape_string($connect,trim($_POST['latitude']) ?? 'not set');
    $long = mysqli_real_escape_string($connect,trim($_POST['longitude']) ?? 'not set');
    $name = mysqli_real_escape_string($connect,trim($_POST['name']) ?? 'not set');

    $sql = "SELECT * FROM location WHERE mac_address = '$mac'";
    $query = mysqli_query($connect,$sql);
    if($result = mysqli_fetch_row($query)) {
        $sql = "UPDATE location SET latitude='$lat',longitude='$long',is_active = 1 WHERE mac_address = '$mac'";
        $query = mysqli_query($connect,$sql);
        echo $lat.";".$long.";".$name;
        exit;
    }
    $sql = "INSERT INTO location(latitude,longitude,mac_address,device_name) VALUES('$lat','$long','$mac','$name')";
    $query = mysqli_query($connect,$sql);
    if($query) {
        echo $lat.";".$long.";".$name;
        exit;
    }
} else {
    echo json_encode(['Bad Request' => 1]);
}
