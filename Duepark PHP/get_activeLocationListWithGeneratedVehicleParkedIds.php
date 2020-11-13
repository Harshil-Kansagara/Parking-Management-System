<?php

    require "init.php";
    require "create_locationParkingMappingTable.php";
    require "create_employeeLocationMappingTable.php";

    $parkingId = $_GET["ParkingId"];
    $loggedInEmployeeId = $_GET["EmployeeId"];
    $loggedInEmployeeRole = $_GET["EmployeeRole"];
    $response = array();

    if($loggedInEmployeeRole != "Manager" && $loggedInEmployeeRole != "Valet"){    
        if($parkingId != null){
            $getAllLocationData = array();
            $getAllLocationId_query = "select LocationId from ".$parkingLocationMappingTable." where ParkingId = $parkingId ";
            $getAllLocationId_result = mysqli_query($connection, $getAllLocationId_query);

            if(!$getAllLocationId_result){
                echo "Error retrieving record '".mysqli_error($connection)."'";
            }
            else{
                if(mysqli_num_rows($getAllLocationId_result)>0){
                    while($row = mysqli_fetch_assoc($getAllLocationId_result))
                    {
                        $locationId = $row["LocationId"];
                        $parkedVehicle_result = getParkedVehicleData($locationId);
                        if(!empty($parkedVehicle_result["LocationId"]) && !empty($parkedVehicle_result["GeneratedLocationId"]))
                        {
                            array_push($response, $parkedVehicle_result);
                        }
                    }
                }
                // else{
                //     $row = mysqli_fetch_assoc($getAllLocationId_result);
                //     $locationId = $row["LocationId"];
                //     $parkedVehicle_result = getParkedVehicleData($locationId);
                //     if(!empty($parkedVehicle_result["LocationId"]) && !empty($parkedVehicle_result["GeneratedLocationId"]))
                //     {
                //         array_push($response, $parkedVehicle_result);
                //     }
                // }
            }
        }
    }
    else{
        $getLocationId_query = "select LocationId from ".$employeeLocationMappingTable." where EmployeeId = $loggedInEmployeeId limit 1";
        $getLocationId_result = mysqli_query($connection, $getLocationId_query);
        if(!$getLocationId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getLocationId_result)==1){
                $row = mysqli_fetch_assoc($getLocationId_result);
                $locationId = $row["LocationId"];
                $parkedVehicle_result = getParkedVehicleData($locationId);
                if(!empty($parkedVehicle_result["LocationId"]) && !empty($parkedVehicle_result["GeneratedLocationId"]))
                {
                    array_push($response, $parkedVehicle_result);
                }
            }
        }
    }
    echo json_encode(array('server_response'=>$response));


    function getParkedVehicleData($locationId){
        global $connection;

        $getParkedVehicleData_query = "select GeneratedParkedVehicleId from parkedvehicle where LocationId = $locationId order by id desc limit 1";
        $getParkedVehicleData_result = mysqli_query($connection, $getParkedVehicleData_query);
        if(!$getParkedVehicleData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $getParkingData = getParkingData();
            $locationData = getLocationData($locationId);
            if(!empty($locationData["LocationId"]) && !empty($locationData["GeneratedLocationId"]))
            {
                if(mysqli_num_rows($getParkedVehicleData_result)==0){
                    $row = mysqli_fetch_assoc($getParkedVehicleData_result);
                    $monthlyPassData = array("GeneratedParkedVehicleId"=>"0");
                    //return $monthlyPassData;
                }
                else{
                    $row = mysqli_fetch_assoc($getParkedVehicleData_result);
                    $monthlyPassData = array("GeneratedParkedVehicleId"=>$row["GeneratedParkedVehicleId"]);
                    //return $monthlyPassData;
                }
                $parkedVehicle_result = array_merge($monthlyPassData, $locationData, $getParkingData);
                return $parkedVehicle_result;
            }
        }
    }

    function getParkingData(){
        global $connection, $parkingId;
        $getParkingData_query = "select ParkingId, ParkingAcronym from parking where id = $parkingId limit 1";
        $getParkingData_result = mysqli_query($connection, $getParkingData_query);
        if(!$getParkingData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            $row = mysqli_fetch_assoc($getParkingData_result);
            $parkingData = array("GeneratedParkingId"=>$row["ParkingId"], "ParkingAcronym"=>$row["ParkingAcronym"]);
            return $parkingData;
        }
    }

    function getLocationData($locationId){
        global $connection, $locationTable;
        $getLocationData_query = "select id, GeneratedLocationId from location where id = $locationId and LocationActiveState = true  limit 1";
        $getLocationData_result = mysqli_query($connection, $getLocationData_query);
        if(!$getLocationData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getLocationData_result) == 1){
                $row = mysqli_fetch_assoc($getLocationData_result);
                $locationData = array("LocationId"=>$row["id"], "GeneratedLocationId"=>$row["GeneratedLocationId"]);
                return $locationData;
            }
        }
    }
?>