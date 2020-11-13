<?php

    require "init.php";
    require "create_vehicleNumberMonthlyPassMappingTable.php";

    $monthlyPassId = $_POST["MonthlyPassId"];
    $passUserMobileNumber = $_POST["PassUserMobileNumber"];
    $vehicleNumber = $_POST["VehicleNumber"];
    $isRenewed = $_POST["IsRenewed"];

    if($isRenewed == "false")
    {
        $updateMonthlyPass_query = "update ".$monthlyPassTable." set PassUserMobileNumber = '$passUserMobileNumber' where id = $monthlyPassId";
        $updateMonthlyPassVehicleNumber_query = "update ".$vehicleNumberMonthlyPassMappingTable." set VehicleNumber = '$vehicleNumber' where MonthlyPassId = $monthlyPassId";
        $updateMonthlyPass_result = mysqli_query($connection, $updateMonthlyPass_query);
        $updateMonthlyPassVehicleNumber_result = mysqli_query($connection, $updateMonthlyPassVehicleNumber_query);
        if(!$updateMonthlyPass_result || !$updateMonthlyPassVehicleNumber_result){
            echo "Error updating record '".mysqli_error($connection)."'";
        }
        else{
            echo "updated";
        }
    }

?>