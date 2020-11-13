<?php

    require "init.php";
    require "create_parkedVehicleTable.php";
    require "create_monthlyPassTable.php";
    require "create_locationMonthlyPassMappingTable.php";

    $parkedVehicleId = $_GET["ParkedVehicleId"];

    if(!empty($parkedVehicleId)){

        $getReleasedVehicleDetail_query = "select * from ".$parkedVehicleTable." where id = $parkedVehicleId limit 1";
        $getReleasedVehicleDetail_result = mysqli_query($connection, $getReleasedVehicleDetail_query);
        if(!$getReleasedVehicleDetail_query){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getReleasedVehicleDetail_result) == 1){
                $row  = mysqli_fetch_assoc($getReleasedVehicleDetail_result);
                if(empty($row["ReleasedBy"])){
                    $currentStatus = "PARKED";
                }
                else{
                    $currentStatus = "RELEASED";
                }
                $data = strtoupper($row["VehicleNumber"]);
                $insertion = "-";
                $firstIndex = 4;
                $data1 = substr_replace($data, $insertion, $firstIndex, 0);
                $secIndex = 7;
                $vehicleNumber = substr_replace($data1, $insertion, $secIndex, 0);
                $generatedLocationId = getGeneratedLocationId($row["LocationId"]);
                $parkingIdDetail = getParkingId($row["LocationId"]);
                if($row["MonthlyPassId"] != null){
                    $generatedMonthlyPassIdData = getGeneratedMonthlyPassId($row["MonthlyPassId"]);
                    $parkedVehicleData = array("Id"=>$row["id"], "MonthlyPassId"=>$row["MonthlyPassId"], 
                        "FullName"=>$row["FullName"], "MobileNumber"=>$row["MobileNumber"], "VehicleNumber"=>$vehicleNumber,
                        "ParkedTime"=>$row["ParkedTime"], "ParkedDate"=>$row["ParkedDate"], "ParkedBy"=>$row["ParkedBy"],
                        "ReleasedTime"=>$row["ReleasedTime"], "ReleasedBy"=>$row["ReleasedBy"], "IsMonthlyPass"=>"true");
                    $array_result = array_merge($parkedVehicleData, $generatedMonthlyPassIdData, $parkingIdDetail, array("CurrentStatus"=>$currentStatus));
                }
                else{
                    $parkedVehicleData = array("Id"=>$row["id"], "LocationId"=>$row["LocationId"], "FullName"=>$row["FullName"], "VehicleNumber"=>$vehicleNumber,
                    "MobileNumber"=>$row["MobileNumber"], "PaidAmount"=>$row["PaidAmount"], "ParkedPaymentType"=>$row["ParkedPaymentType"], 
                    "ParkedTime"=>$row["ParkedTime"], "ParkedDate"=>$row["ParkedDate"], "IsPayLater"=>$row["IsPayLater"],
                    "IsParkingFree"=>$row["IsParkingFree"],"ReleasedAmount"=>$row["ReleasedAmount"], 
                    "ReleasedPaymentType"=>$row["ReleasedPaymentType"], "ParkedBy"=>$row["ParkedBy"], 
                    "ReleasedTime"=>$row["ReleasedTime"], "ReleasedBy"=>$row["ReleasedBy"], "IsMonthlyPass"=>"false");
                    $array_result = array_merge($parkedVehicleData, $parkingIdDetail, array("GeneratedLocationId"=>$generatedLocationId, "CurrentStatus"=>$currentStatus));
                }
                echo json_encode($array_result);
            }
        }
    }

    // Change the table name here 
    function getGeneratedLocationId($locationId){
        global $connection, $locationTable;
        $getGeneratedId_query = "select GeneratedLocationId from ".$locationTable." where id = $locationId limit 1";
        $getGeneratedId_result = mysqli_query($connection, $getGeneratedId_query);
        if(!$getGeneratedId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getGeneratedId_result)==1){
                $row = mysqli_fetch_assoc($getGeneratedId_result);
                return $row["GeneratedLocationId"];
            }
        }
    }
    
    function getParkingId($locationId){
        global $connection, $parkingTable;
        $getParkingId_query = "select ParkingId from parkinglocationmapping where LocationId = $locationId limit 1";
        $getParkingId_result = mysqli_query($connection, $getParkingId_query);
        if(!$getParkingId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getParkingId_result) == 1){
                $row = mysqli_fetch_assoc($getParkingId_result);
                $parkingId = $row["ParkingId"];
                $getParking_query = "select ParkingId, ParkingAcronym from ".$parkingTable." where id = $parkingId limit 1";
                $getParking_result = mysqli_query($connection, $getParking_query);
                if(!$getParking_result){
                    echo "Error retrieving record '".mysqli_error($connection)."'";
                }
                else{
                    if(mysqli_num_rows($getParking_result)==1){
                        $row = mysqli_fetch_assoc($getParking_result);
                        return array("GeneratedParkingId"=>$row["ParkingId"], "ParkingAcronym"=>$row["ParkingAcronym"]);
                    }
                }
            }
        }
    }

    function getGeneratedMonthlyPassId($monthlyPassId){
        global $connection, $locationMonthlyPassMappingTable;
        $getGeneratedMonthlyPassId_query = "select * from ".$locationMonthlyPassMappingTable." where MonthlyPassId = $monthlyPassId limit 1";
        $getGeneratedMonthlyPassId_result = mysqli_query($connection, $getGeneratedMonthlyPassId_query);
        if(!$getGeneratedMonthlyPassId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getGeneratedMonthlyPassId_result) == 1){
                $row = mysqli_fetch_assoc($getGeneratedMonthlyPassId_result);
                $generatedMonthlyPassIdData = array("GeneratedLocationId"=>$row["GeneratedLocationId"], "GeneratedMonthlyPassId"=>$row["GeneratedMonthlyPassId"]);
                return $generatedMonthlyPassIdData;
            }
        }
    }
?>