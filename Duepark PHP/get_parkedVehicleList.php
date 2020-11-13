<?php

    require "init.php";
    require "create_parkedVehicleTable.php";

    $parkingId = $_GET["ParkingId"];
    $locationId = $_GET["LocationId"];
    $vehicleType = $_GET["VehicleType"];
    $getParkedVehicleList = array();

    if($locationId != "0"){
        $getParkedVehicleList_query = "select id, VehicleNumber, VehicleType, PaidAmount, ParkedTime, ParkedDate from ".$parkedVehicleTable." where ParkingId = $parkingId and locationId = $locationId and VehicleType = '$vehicleType' and IsParkedVehicleReleased = false";
    }
    else{
        $getParkedVehicleList_query = "select id, VehicleNumber, VehicleType, PaidAmount, ParkedTime, ParkedDate from ".$parkedVehicleTable." where ParkingId = $parkingId and VehicleType = '$vehicleType' and IsParkedVehicleReleased = false";
    }
    $getParkedVehicleList_result = mysqli_query($connection, $getParkedVehicleList_query);
    if(!$getParkedVehicleList_result){
        echo "Error retrieving record '".mysqli_error($connection)."'";
    }
    else{
        if(mysqli_num_rows($getParkedVehicleList_result)>0){
            while($row = mysqli_fetch_assoc($getParkedVehicleList_result)){
                $data = strtoupper($row["VehicleNumber"]);
                $insertion = "-";
                $firstIndex = 4;
                $data1 = substr_replace($data, $insertion, $firstIndex, 0);
                $secIndex = 7;
                $vehicleNumber = substr_replace($data1, $insertion, $secIndex, 0);
                array_push($getParkedVehicleList, array("Id"=>$row["id"], "VehicleNumber"=>$vehicleNumber, "PaidAmount"=>$row["PaidAmount"], "ParkedTime"=>$row["ParkedTime"], "ParkedDate"=>$row["ParkedDate"], "VehicleType"=>$row["VehicleType"]));
            }
            echo json_encode(array("server_response"=>$getParkedVehicleList));
        }
    }

?>