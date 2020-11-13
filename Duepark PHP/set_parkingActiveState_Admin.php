<?php

    require "init.php";
    require "create_parkingTable.php";
    
    $parkingId = $_POST["ParkingId"];
    $parkingActiveState = $_POST["ActiveState"];

    $updateParking_query = "update ".$parkingTable." set ParkingActiveState = $parkingActiveState where id = $parkingId";
    $updateParking_result = mysqli_query($connection, $updateParking_query);
    if(!$updateParking_result){
        echo "Error updating record '".mysqli_error($connection)."'";
    }
    else{
        if($parkingActiveState == "1"){
            echo "activate";
        }
        else{
            echo "deactivate";
        }
    }

?>