<?php

    require "init.php";
    require "create_locationParkingMappingTable.php";
    require "create_locationDetailTable.php";

    $parkingId = $_GET["ParkingId"];
    
    if(!empty($parkingId)){
        $getAllLocationId_query = "select LocationId from ".$parkingLocationMappingTable." where ParkingId = $parkingId";
        $getAllLocationId_result = mysqli_query($connection, $getAllLocationId_query);
        if(!$getAllLocationId_result){
            echo "Error retrieveing record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getAllLocationId_result)>0)
            {
                $array_result = array();
                while($row = mysqli_fetch_assoc($getAllLocationId_result))
                {
                    $locationName = getLocationName($row["LocationId"]);
                    $locationCapacityArray = getTotalLocationCapacityDetail($row["LocationId"]);
                    $occupiedLocationCapacityArray = getOccupiedLocationCapacityDetail($row["LocationId"]);
                    array_push($array_result, array_merge(array("id"=>$row["LocationId"],"LocationName"=>$locationName), $locationCapacityArray, $occupiedLocationCapacityArray));
                }
                echo json_encode(array("server_response"=>$array_result));
            }
            else{
                echo json_encode(array("server_response"=>array()));
            }
        }
    }

    function getLocationName($locationId){
        global $connection, $locationTable;
        $getLocationName_query = "select LocationName from ".$locationTable." where id = $locationId limit 1";
        $getLocationName_result = mysqli_query($connection, $getLocationName_query);
        if(!$getLocationName_result){
            echo "Error retrieveing record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getLocationName_result)==1)
            {
                $row = mysqli_fetch_assoc($getLocationName_result);
                return $row["LocationName"];
            }
        }
    }

    function getTotalLocationCapacityDetail($locationId){
        global $connection, $locationDetailTable;
        $getLocationCapacity_query = "select CarCapacity, BikeCapacity from ".$locationDetailTable." where LocationId = $locationId limit 1";
        $getLocationCapacity_result = mysqli_query($connection, $getLocationCapacity_query);
        if(!$getLocationCapacity_result){
            echo "Error retrieveing record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getLocationCapacity_result)==1)
            {
                $carCapacity = 0;
                $bikeCapacity = 0;
                $row = mysqli_fetch_assoc($getLocationCapacity_result);
                if(!empty($row["CarCapacity"])){
                    $carCapacity = $row["CarCapacity"];
                }
                if(!empty($row["BikeCapacity"])){
                    $bikeCapacity = $row["BikeCapacity"];
                }
                $locationCapacityArray = array("CarCapacity"=>$carCapacity, "BikeCapacity"=>$bikeCapacity);
                return $locationCapacityArray;
            }
        }
    }

    // Change the table name here
    function getOccupiedLocationCapacityDetail($locationId){
        global $connection;
        $getOccupiedLocationCapacity_query = "select VehicleType from parkedvehicle where LocationId = $locationId";
        $getOccupiedLocationCapacity_result = mysqli_query($connection, $getOccupiedLocationCapacity_query);
        if(!$getOccupiedLocationCapacity_query){
            echo "Error retrieveing record '".mysqli_error($connection)."'";
        }
        else{
            $occupiedCarCapacity = 0;
            $occupiedBikeCapacity = 0;
            if(mysqli_num_rows($getOccupiedLocationCapacity_result)>0)
            {
                
                while($row = mysqli_fetch_assoc($getOccupiedLocationCapacity_result)){
                    if($row["VehicleType"]=="Car"){
                        $occupiedCarCapacity = $occupiedCarCapacity + 1;
                    }
                    else {
                        $occupiedBikeCapacity = $occupiedBikeCapacity + 1;
                    }
                }
                $occupiedLocationCapacityArray = array("OccupiedCarCapacity"=>$occupiedCarCapacity, "OccupiedBikeCapacity"=>$occupiedBikeCapacity);
                return $occupiedLocationCapacityArray;
            }
            else{
                $occupiedLocationCapacityArray = array("OccupiedCarCapacity"=>$occupiedCarCapacity, "OccupiedBikeCapacity"=>$occupiedBikeCapacity);
                return $occupiedLocationCapacityArray;
            }
        }
    }
?>