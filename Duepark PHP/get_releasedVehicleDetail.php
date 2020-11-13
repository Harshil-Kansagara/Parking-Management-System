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
        else{
            if(mysqli_num_rows($getReleasedVehicleDetail_result) == 1){
                $row  = mysqli_fetch_assoc($getReleasedVehicleDetail_result);
                $locationName = getLocationName($row["LocationId"]);
                if($row["MonthlyPassId"] != null){
                    $generatedMonthlyPassIdData = getGeneratedMonthlyPassId($row["MonthlyPassId"]);
                    $parkedVehicleData = array("Id"=>$row["id"], "MonthlyPassId"=>$row["MonthlyPassId"], 
                        "FullName"=>$row["FullName"], "MobileNumber"=>$row["MobileNumber"], 
                        "ParkedTime"=>$row["ParkedTime"], "ParkedDate"=>$row["ParkedDate"], "ParkedBy"=>$row["ParkedBy"],
                        "ReleasedTime"=>$row["ReleasedTime"], "ReleasedBy"=>$row["ReleasedBy"], "IsMonthlyPass"=>"true", "LocationName"=>$locationName);
                    $array_result = array_merge($parkedVehicleData, $generatedMonthlyPassIdData);
                }
                else{
                    $parkedVehicleData = array("Id"=>$row["id"], "LocationId"=>$row["LocationId"], "FullName"=>$row["FullName"], 
                    "MobileNumber"=>$row["MobileNumber"], "PaidAmount"=>$row["PaidAmount"], "ParkedPaymentType"=>$row["ParkedPaymentType"], 
                    "ParkedTime"=>$row["ParkedTime"], "ParkedDate"=>$row["ParkedDate"], "IsPayLater"=>$row["IsPayLater"],
                    "IsParkingFree"=>$row["IsParkingFree"],"ReleasedAmount"=>$row["ReleasedAmount"], 
                    "ReleasedPaymentType"=>$row["ReleasedPaymentType"], "ParkedBy"=>$row["ParkedBy"], 
                    "ReleasedTime"=>$row["ReleasedTime"], "ReleasedBy"=>$row["ReleasedBy"], "IsMonthlyPass"=>"false", "LocationName"=>$locationName);
                    $array_result = $parkedVehicleData;
                }
                echo json_encode($array_result);
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