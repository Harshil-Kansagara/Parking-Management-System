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
                    while($row = mysqli_fetch_assoc($getAllLocationId_result)){
                        $locationId = $row["LocationId"];
                        $monthlyPassData = getMonthyPassData($locationId);
                        if(!empty($monthlyPassData["CarMonthlyPassRate"] && !empty($monthlyPassData["BikeMonthlyPassRate"])))
                        {
                            $locationData = getLocationData($locationId);
                            if(!empty($locationData["id"]) && !empty($locationData["GeneratedLocationId"]) && !empty($locationData["LocationName"]) && !empty($locationData["LocationActiveState"])){
                                $parkingData = getParkingData();
                                $array_result = array_merge($parkingData, $locationData, $monthlyPassData);
                                array_push($response, $array_result);
                            }
                        }
                    }
                }
                //else{
                    //array_push($response);
                    // $row = mysqli_fetch_assoc($getAllLocationId_result);
                    // $locationId = $row["LocationId"];
                    // $monthlyPassData = getMonthyPassData($locationId);
                    // if(!empty($monthlyPassData["CarMonthlyPassRate"] && !empty($monthlyPassData["BikeMonthlyPassRate"])))
                    // {
                    //     $locationData = getLocationData($locationId);
                    //     if(!empty($locationData["id"]) && !empty($locationData["GeneratedLocationId"]) && !empty($locationData["LocationName"]) && !empty($locationData["LocationActiveState"])){
                    //             $parkingData = getParkingData();
                    //             $array_result = array_merge($parkingData, $locationData, $monthlyPassData);
                    //             array_push($response, $array_result);
                    //     }
                    // }
                //}
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
                $monthlyPassData = getMonthyPassData($locationId);
                if(!empty($monthlyPassData["CarMonthlyPassRate"] && !empty($monthlyPassData["BikeMonthlyPassRate"])))
                {
                    $locationData = getLocationData($locationId);
                    if(!empty($locationData["id"]) && !empty($locationData["GeneratedLocationId"]) && !empty($locationData["LocationName"]) && !empty($locationData["LocationActiveState"])){
                        $parkingData = getParkingData();
                        $array_result = array_merge($parkingData, $locationData, $monthlyPassData);
                        array_push($response, $array_result);
                    }
                }
            }
            // else{
            //     $response = array();
            // }
        }
    }
    echo json_encode(array('server_response'=>$response));

    function getLocationData($locationId){
        global $connection, $locationTable;
        $getLocationData_query = "select id, GeneratedLocationId, LocationName, LocationActiveState from ".$locationTable." where id = $locationId and LocationActiveState = true  limit 1";
        $getLocationData_result = mysqli_query($connection, $getLocationData_query);
        if(!$getLocationData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            $row = mysqli_fetch_assoc($getLocationData_result);
            $locationData = array("id"=>$row["id"], "GeneratedLocationId"=>$row["GeneratedLocationId"], "LocationName"=>$row["LocationName"], "LocationActiveState"=>$row["LocationActiveState"]);
            return $locationData;
        }
    }

    function getParkingData(){
        global $connection, $parkingId, $parkingTable;
        $getParkingData_query = "select ParkingId, ParkingAcronym from ".$parkingTable." where id = $parkingId limit 1";
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

    function getMonthyPassData($locationId){
        global $connection;

        $getMonthlyPassData_query = "select CarMonthlyPassRate, BikeMonthlyPassRate from locationdetail where LocationId = $locationId limit 1";
        $getMonthlyPassData_result = mysqli_query($connection, $getMonthlyPassData_query);
        if(!$getMonthlyPassData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $monthlyPassData = array();
            if(mysqli_num_rows($getMonthlyPassData_result)==1){
                $row = mysqli_fetch_assoc($getMonthlyPassData_result);
                $monthlyPassData = array("CarMonthlyPassRate"=>$row["CarMonthlyPassRate"], "BikeMonthlyPassRate"=>$row["BikeMonthlyPassRate"]);
                return $monthlyPassData;
            }
            else{
                $monthlyPassData = array("CarMonthlyPassRate"=>null, "BikeMonthlyPassRate"=>null);
                return $monthlyPassData;
            }
        }
    }
?>