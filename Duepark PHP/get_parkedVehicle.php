<?php

    require "init.php";
    require "create_parkedVehicleTable.php";    
    require "create_locationMonthlyPassMappingTable.php";

    $parkedVehicleId = $_GET["ParkedVehicleId"];

    if(!is_null($parkedVehicleId)){

        $getParkedVehicle_query = "select id, LocationId, MonthlyPassId, GeneratedParkedVehicleId, FullName, MobileNumber, VehicleType, VehicleNumber, 
                        PaidAmount, ParkedPaymentType, PaymentTimeRate, IsPayLater, IsParkingFree, ParkedBy, ParkedTime, ParkedDate from ".$parkedVehicleTable." where id = $parkedVehicleId limit 1";

        $getParkedVehicle_result = mysqli_query($connection, $getParkedVehicle_query);
        
        if(!$getParkedVehicle_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getParkedVehicle_result)==1){
                $row = mysqli_fetch_assoc($getParkedVehicle_result);
                $monthlyPassId = $row["MonthlyPassId"];
                $locationName = getLocationName($row["LocationId"]);
                if($monthlyPassId == null){
                    $parkedVehicleDetail = array("id"=>$row["id"], "LocationId"=>$row["LocationId"], "MonthlyPassId"=>$row["MonthlyPassId"], 
                        "FullName"=>$row["FullName"], "MobileNumber"=>$row["MobileNumber"], "VehicleType"=>$row["VehicleType"], "VehicleNumber"=>$row["VehicleNumber"],
                        "PaidAmount"=>$row["PaidAmount"], "ParkedPaymentType"=>$row["ParkedPaymentType"], "IsPayLater"=>$row["IsPayLater"],
                        "PaymentTimeRate"=>$row["PaymentTimeRate"], "IsParkingFree"=>$row["IsParkingFree"], "ParkedBy"=>$row["ParkedBy"], "ParkedTime"=>$row["ParkedTime"],
                        "ParkedDate"=>$row["ParkedDate"], "LocationName"=>$locationName);
                }
                else{
                    $generatedMonthlyPassIdData = getGeneratedMonthlyPassId($monthlyPassId);
                    $parkedVehicleData = array("id"=>$row["id"], "LocationId"=>$row["LocationId"], "MonthlyPassId"=>$row["MonthlyPassId"], 
                                "FullName"=>$row["FullName"], "MobileNumber"=>$row["MobileNumber"], "VehicleType"=>$row["VehicleType"], "VehicleNumber"=>$row["VehicleNumber"],
                                "ParkedBy"=>$row["ParkedBy"], "ParkedTime"=>$row["ParkedTime"],"ParkedDate"=>$row["ParkedDate"], "LocationName"=>$locationName);
                    $parkedVehicleDetail = array_merge($parkedVehicleData, $generatedMonthlyPassIdData);
                }
                echo json_encode($parkedVehicleDetail);
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

    function getLocationName($locationId){
        global $connection, $locationTable;
        $getLocationName_query = "select LocationName from ".$locationTable." where id = $locationId limit 1";
        $getLocationName_result = mysqli_query($connection, $getLocationName_query);
        if(!$getLocationName_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getLocationName_result)){
                $row = mysqli_fetch_assoc($getLocationName_result);
                return $row["LocationName"];
            }
        }
    }
?>