<?php

    require "init.php";
    //require "create_monthlyPassTable.php";
    require "create_vehicleNumberMonthlyPassMappingTable.php";
    require "create_locationMonthlyPassMappingTable.php";

    $locationId = $_POST["LocationId"];
    $passUserName = $_POST["PassUserName"];
    $passUserMobileNumber = $_POST["PassUserMobileNumber"];
    $vehicleType = $_POST["VehicleType"];
    $vehicleNumbers = strtoLower($_POST["VehicleNumbers"]);
    $passDuration = $_POST["PassDuration"];
    $payableAmount = $_POST["PayableAmount"];
    $payableType = $_POST["PayableType"];
    $issuedDate = $_POST["IssuedDate"];
    $expiryDate = $_POST["ExpiryDate"];
    $issuedTime = $_POST["IssuedTime"];
    $issuedBy = $_POST["IssuedBy"];
    $activeMonthlyPassState = $_POST["ActiveMonthlyPassState"];
    $generatedLocationId = $_POST["GeneratedLocationId"];
    $parkingId = $_POST["ParkingId"];
    $issuedEmployeeId = $_POST["IssuedEmployeeId"];

    if($vehicleNumbers != "null"){
        $vehicleNumberList = explode(",",$vehicleNumbers);
    }

    $response = array();
    $i=0;
    $count = count($vehicleNumberList);
    $isVehicleNumberExists = false;

    while($i<$count){
        $checkVehicleNumber_query = "select MonthlyPassId from ".$vehicleNumberMonthlyPassMappingTable." where VehicleNumber = '$vehicleNumberList[$i]' limit 1";
        $checkVehicleNumber_result = mysqli_query($connection, $checkVehicleNumber_query);
        if(!$checkVehicleNumber_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";    
        }
        else{
            if(mysqli_num_rows($checkVehicleNumber_result) == 1){
                $row = mysqli_fetch_assoc($checkVehicleNumber_result);
                $isVehicleNumberExists = true;
                $response = array("exists"=>"true", "monthlyPassId"=>$row["MonthlyPassId"]);
                echo json_encode($response);
                break;
            }
        }
        $i++;
    }

    if(!$isVehicleNumberExists){
        addMonthlyPass();
    }
    // else{
    //     echo "exists";
    // }

    // $checkVehicleNumber_query = "select * from ".$monthlyPassTable." where VehicleNumber = '$vehicleNumber' limit 1";
    // $checkVehicleNumber_result = mysqli_query($connection, $checkVehicleNumber_query);
    // if(!$checkVehicleNumber_result){
    //     echo "Error retrieving record '".mysqli_error($connection)."'";
    // }
    // else{
    //     if(mysqli_num_rows($checkVehicleNumber_result) == 1){
    //         echo "exists";
    //     }
    //     else{
            
    //     }
    // }

    function addMonthlyPass(){
        global $connection, $monthlyPassTable, $passUserName, $passUserMobileNumber, $passDuration, $payableAmount,
            $payableType, $issuedDate, $expiryDate, $issuedTime, $issuedEmployeeId, $issuedBy, $activeMonthlyPassState;
        
        $addMonthlyPass_query = "insert into ".$monthlyPassTable." (PassUserName, PassUserMobileNumber, PassDuration, PayableAmount, PayableType, IssuedDate, ExpiryDate, IssuedTime, IssuedEmployeeId, IssuedBy, ActiveMonthlyPassState) values ('$passUserName', 
                '$passUserMobileNumber', '$passDuration', '$payableAmount', '$payableType', '$issuedDate', 
                '$expiryDate', '$issuedTime', $issuedEmployeeId, '$issuedBy', $activeMonthlyPassState)";
        $addMonthlyPass_result = mysqli_query($connection, $addMonthlyPass_query);
        if(!$addMonthlyPass_result){
            echo "Error creating record '".mysqli_error($connection)."'";
        }
        else{
            $newMonthlyPassId = mysqli_insert_id($connection);
            //echo $newMonthlyPassId;
            addVehicleNumberMonthlyPassMappingTable($newMonthlyPassId);
            addLocationMonthlyPassMappingTable($newMonthlyPassId);
        }
    }

    function addVehicleNumberMonthlyPassMappingTable($newMonthlyPassId){
        global $connection, $vehicleNumberList, $vehicleType, $vehicleNumberMonthlyPassMappingTable, $count;
        $i = 0;
        while($i<$count){
            $vehicle_number = strtoupper($vehicleNumberList[$i]);
            $addVehicleNumber_query = "insert into ".$vehicleNumberMonthlyPassMappingTable." (MonthlyPassId, VehicleType, VehicleNumber) 
                        values ($newMonthlyPassId, '$vehicleType', '$vehicle_number')";
            $addVehicleNumber_result = mysqli_query($connection, $addVehicleNumber_query);
            if(!$addVehicleNumber_result){
                echo "Error creating record '".mysqli_error($connection)."'";
            }
            $i++;
        }
    }

    function addLocationMonthlyPassMappingTable($newMonthlyPassId){
        global $connection, $locationId, $locationMonthlyPassMappingTable, $generatedLocationId, $parkingId;
        $lastGeneratedMonthlyPassId_query = "select GeneratedMonthlyPassId from ".$locationMonthlyPassMappingTable." where LocationId = $locationId order by id desc limit 1";
        $lastGeneratedMonthlyPassId_result = mysqli_query($connection, $lastGeneratedMonthlyPassId_query);
        if(!$lastGeneratedMonthlyPassId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($lastGeneratedMonthlyPassId_result)==0){
                $newGeneratedId = 1;
            }else{
                $row = mysqli_fetch_assoc($lastGeneratedMonthlyPassId_result);
                $newGeneratedId = $row["GeneratedMonthlyPassId"] + 1;
            }
            $addLocationMonthlyPassMapping_query = "insert into ".$locationMonthlyPassMappingTable."(LocationId, ParkingId, MonthlyPassId, GeneratedMonthlyPassId, GeneratedLocationId) 
                values ($locationId, $parkingId, $newMonthlyPassId, $newGeneratedId, '$generatedLocationId')";
            $addLocationMonthlyPassMapping_result = mysqli_query($connection, $addLocationMonthlyPassMapping_query);
            if(!$addLocationMonthlyPassMapping_result){
                echo "Error creating record '".mysqli_error($connection)."'";
            }
            else{
                $response = array("exists"=>"false", "monthlyPassId"=>$newMonthlyPassId);
                echo json_encode($response);
                //echo $newMonthlyPassId;
            }
        }
    }

?>