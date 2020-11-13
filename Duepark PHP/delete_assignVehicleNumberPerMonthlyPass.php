<?php

    require "init.php";
    require "create_vehicleNumberMonthlyPassMappingTable.php";

    $vehicleNumberIds = $_POST["VehicleNumberIds"];
    $vehicleNumberIdList = explode(",",$vehicleNumberIds);

    $i=0;
    $count = count($vehicleNumberIdList);
    $isUpdated = true;

    while($i<$count){
        $delete_query = "delete from ".$vehicleNumberMonthlyPassMappingTable." where id = $vehicleNumberIdList[$i]";
        $delete_result = mysqli_query($connection, $delete_query);
        if(!$delete_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
            $isUpdated = false;
        }
        $i = $i + 1;
    }

    if($isUpdated){
        echo "updated";
    }
    else{
        echo "notupdated";
    }

?>