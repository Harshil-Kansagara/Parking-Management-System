<?php

    require "init.php";
    require "create_parkedVehicleTable.php";

    $parkedVehicleId = $_POST["ParkedVehicleId"];
    $releasedAmount = $_POST["ReleasedAmount"];
    $releasedTime = $_POST["ReleasedTime"];
    $releasedDate = $_POST["ReleasedDate"];
    $releasedBy = $_POST["ReleasedBy"];
    $releasedPaymentType = $_POST["ReleasedPaymentType"];
    $isParkedVehicleReleased = $_POST["IsParkedVehicleReleased"];
    $releasedEmployeeId = $_POST["ReleasedEmployeeId"];

    // Add relesed employee id
    $releasedVehicle_query = "update ".$parkedVehicleTable." set ReleasedAmount = '$releasedAmount', 
                            ReleasedTime = '$releasedTime', ReleasedDate = '$releasedDate', 
                            ReleasedEmployeeId=$releasedEmployeeId, ReleasedBy = '$releasedBy', 
                            ReleasedPaymentType = '$releasedPaymentType', IsParkedVehicleReleased = $isParkedVehicleReleased 
                            where id = $parkedVehicleId";
    $releasedVehicle_result = mysqli_query($connection, $releasedVehicle_query);
    if(!$releasedVehicle_result){
        echo "Error updating record '".mysqli_error($connection)."'";
    }
    else{
        echo "released";
    }

?>