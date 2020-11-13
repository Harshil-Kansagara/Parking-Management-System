<?php

    require "init.php";
    require "create_locationTable.php";
    require "create_locationParkingMappingTable.php";

    $locationName = $_POST["LocationName"];
    $locationAddress = $_POST["LocationAddress"];
    $locationType = $_POST["LocationType"];
    $locationDate = $_POST["LocationDate"];
    $locationTime = $_POST["LocationTime"];
    $locationActiveState = $_POST["LocationActiveState"];
    $parkingId = $_POST["ParkingId"];

    $locationId_query = "select LocationId from ".$parkingLocationMappingTable." where ParkingId ='".$parkingId."' order by id desc limit 1";

    $locationId_result = mysqli_query($connection, $locationId_query);

    if($locationId_result->num_rows == 0){
        createLocation(1);
    }else{
        $row = mysqli_fetch_assoc($locationId_result);
        $locationId = $row["LocationId"];
        
        $generatedLocationId_query = "select GeneratedLocationId from ".$locationTable." where id = $locationId order by id desc limit 1";
        $generatedLocationId_result = mysqli_query($connection, $generatedLocationId_query);

        if(!$generatedLocationId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($generatedLocationId_result);
            $generatedLocationId = $row["GeneratedLocationId"];
            $newGeneratedLocationId = $generatedLocationId + 1;
            createLocation($newGeneratedLocationId);
        }
    }

    function createLocation($generatedLocationId){
        global $connection, $locationTable, $locationName, $locationAddress, $locationType, $locationDate, $locationTime, $locationActiveState;
        $addLocation_query = "insert into ".$locationTable." (GeneratedLocationId, LocationName, LocationAddress, LocationType, LocationDate, LocationTime, LocationActiveState) 
                values ($generatedLocationId, '$locationName', '$locationAddress', '$locationType', '$locationDate', '$locationTime', $locationActiveState)";
        $addLocation_result = mysqli_query($connection, $addLocation_query);
        if(!$addLocation_result){
            //echo "Error creating record '".mysqli_error($connection)."'";
            echo "0";
        }
        else{
            $locationId = mysqli_insert_id($connection);
            createParkingLocationMapping($locationId);
        }
    }

    function createParkingLocationMapping($locationId){
        global $connection, $parkingLocationMappingTable, $parkingId;
        $addParkingLocationMapping_query = "insert into ".$parkingLocationMappingTable." (ParkingId, LocationId) values ($parkingId, $locationId)";
        $addParkingLocationMapping_result = mysqli_query($connection, $addParkingLocationMapping_query);
        if(!$addParkingLocationMapping_result){
            //echo "Error creating record '".mysqli_error($connection)."'";
            echo "0";
        }
        else{
            echo $locationId;
        }
    }
?>
