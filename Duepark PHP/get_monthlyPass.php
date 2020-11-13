<?php

    require "init.php";
    require "create_locationMonthlyPassMappingTable.php";
    require "create_vehicleNumberMonthlyPassMappingTable.php";

    $monthlyPassId = $_GET["MonthlyPassId"];
    $response = array();

    $getMonthlyPass_query = "select * from ".$monthlyPassTable." where id = $monthlyPassId limit 1";
    $getMonthlyPass_result = mysqli_query($connection, $getMonthlyPass_query);
    if(!$getMonthlyPass_result){
        echo "Error retrieving record '".mysqli_error($connection)."'";
    }
    else
    {
        if(mysqli_num_rows($getMonthlyPass_result) == 1){
            $row = mysqli_fetch_assoc($getMonthlyPass_result);
            $generatedMonthlyPassIdData = getGeneratedMonthlyPassIdData();
            $vehicleNumberData = getVehicleNumberData();
            $monthlyPassData = array("id"=>$row['id'], "PassUserName"=>$row["PassUserName"], "PassUserMobileNumber"=>$row["PassUserMobileNumber"], "PassDuration"=>$row["PassDuration"],
            "PayableAmount"=>$row['PayableAmount'], "PayableType"=>$row["PayableType"], "IssuedDate"=>$row["IssuedDate"], "ExpiryDate"=>$row["ExpiryDate"],
            "IssuedBy"=>$row["IssuedBy"], "IssuedTime"=>$row["IssuedTime"]);
            $array_result = array_merge($monthlyPassData, $vehicleNumberData, $generatedMonthlyPassIdData);
            array_push($response, $array_result);
            echo json_encode($array_result);
        }
    }

    function getGeneratedMonthlyPassIdData(){
        global $connection, $locationMonthlyPassMappingTable, $monthlyPassId;
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

    function getVehicleNumberData(){
        global $connection, $vehicleNumberMonthlyPassMappingTable, $monthlyPassId;
        $getVehicleNumber_query = "select * from ".$vehicleNumberMonthlyPassMappingTable." where MonthlyPassId = $monthlyPassId";
        $getVehicleNumber_result = mysqli_query($connection, $getVehicleNumber_query);
        if(!$getVehicleNumber_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getVehicleNumber_result) == 1){
                $row = mysqli_fetch_assoc($getVehicleNumber_result);
                $vehicleNumberData = array("VehicleNumber"=>$row["VehicleNumber"], "VehicleType"=>$row["VehicleType"]);
                return $vehicleNumberData;
            }
            else{
                $allVehicleNumber = "";
                while($row = mysqli_fetch_assoc($getVehicleNumber_result))
                {
                    $allVehicleNumber = $row["VehicleNumber"].","; 
                }
                $allVehicleNumber = rtrim($allVehicleNumber, ",");
                $vehicleNumberData = array("VehicleNumber"=>$allVehicleNumber, "VehicleType"=>$row["VehicleType"]);
                return $vehicleNumberData;
            }
        }
    }

?>