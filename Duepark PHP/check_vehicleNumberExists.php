<?php

    require "init.php";
    require "create_parkedVehicleTable.php";
    require "create_vehicleNumberMonthlyPassMappingTable.php";
    require "create_locationMonthlyPassMappingTable.php";

    $vehicleNumber = $_GET["VehicleNumber"];
    $parkingId = $_GET["ParkingId"];

    if($vehicleNumber != null)
    {
        $checkVehicleNumberInMonthlyPass_query = "select MonthlyPassId from ".$vehicleNumberMonthlyPassMappingTable." where VehicleNumber = '$vehicleNumber' limit 1";
        $checkVehicleNumberInMonthlyPass_result = mysqli_query($connection, $checkVehicleNumberInMonthlyPass_query);
        if(!$checkVehicleNumberInMonthlyPass_result)
        {
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($checkVehicleNumberInMonthlyPass_result) == 1)
            {
                $row = mysqli_fetch_assoc($checkVehicleNumberInMonthlyPass_result);
                $monthlyPassId = $row['MonthlyPassId'];
                $passIdDetail = getGeneratedMonthlyPassId($monthlyPassId);
                if(count($passIdDetail)!=0)
                {
                    $checkMonthlyPassIsValid_query = "select PassUserName, PassUserMobileNumber, IssuedDate, ExpiryDate, ActiveMonthlyPassState from monthlypass where id = $monthlyPassId limit 1";
                    $checkMonthlyPassIsValid_result = mysqli_query($connection, $checkMonthlyPassIsValid_query);
                    if(!$checkMonthlyPassIsValid_result){
                        echo "Error retrieving record '".mysqli_error($connection)."'";
                    }
                    else
                    {
                        if(mysqli_num_rows($checkMonthlyPassIsValid_result) == 1)
                        {
                            $monthPassRow = mysqli_fetch_assoc($checkMonthlyPassIsValid_result);
                            $activeMonthlyPassState = $monthPassRow['ActiveMonthlyPassState'];
                            if($activeMonthlyPassState == 1){
                                if(strtotime($monthPassRow["ExpiryDate"])<strtotime("today")){
                                    echo "true";
                                    updateMonthlyPassData($monthlyPassId);
                                    $array_result = checkInParkedVehicleNumber();
                                }
                                else{
                                    $checkIsParkedVehicleReleased_query = "select id, IsParkedVehicleReleased from ".$parkedVehicleTable." where VehicleNumber= '$vehicleNumber' order by id desc limit 1";
                                    $checkIsParkedVehicleReleased_result = mysqli_query($connection, $checkIsParkedVehicleReleased_query);
                                    if(!$checkIsParkedVehicleReleased_result){
                                        echo "Error retrieving record '".mysqli_error($connection)."'";
                                    }
                                    else{
                                        if(mysqli_num_rows($checkIsParkedVehicleReleased_result)==1){
                                           $isParkedVehicleReleasedRow = mysqli_fetch_assoc($checkIsParkedVehicleReleased_result);
                                            $isParkedVehicleReleased = $isParkedVehicleReleasedRow["IsParkedVehicleReleased"];
                                            $parkedVehicleId = $isParkedVehicleReleasedRow["id"];
                                        }
                                        else{
                                            $isParkedVehicleReleased = "1";
                                            $parkedVehicleId = "0";
                                        }
                                    }
                                    $monthlyPassDetail = array("MonthlyPassId"=>$monthlyPassId, "PassUserName"=>$monthPassRow["PassUserName"], "IsParkedVehicleReleased"=>$isParkedVehicleReleased, "ParkedVehicleId" =>$row["ParkedVehicleId"],
                                    "PassUserMobileNumber"=>$monthPassRow["PassUserMobileNumber"], "IssuedDate"=>$monthPassRow["IssuedDate"], "ExpiryDate"=>$monthPassRow["ExpiryDate"], "IsMonthlyPass"=>"true");
                                    $array_result = array_merge($monthlyPassDetail, $passIdDetail);
                                }
                            }
                            else
                            {
                                $array_result = checkInParkedVehicleNumber();
                            }
                        }
                    }
                }
                else{
                    $array_result = checkInParkedVehicleNumber();
                }
            }
            else
            {
                $array_result = checkInParkedVehicleNumber();
            }
        }
        echo json_encode(array("server_response"=>$array_result));
    }

    function getGeneratedMonthlyPassId($monthlyPassId){
        global $connection, $locationMonthlyPassMappingTable, $parkingId;
        $getGeneratedMonthlyPassId_query = "select LocationId, GeneratedMonthlyPassId, GeneratedLocationId from ".$locationMonthlyPassMappingTable." where MonthlyPassId = $monthlyPassId and ParkingId=$parkingId limit 1";
        $getGeneratedMonthlyPassId_result = mysqli_query($connection, $getGeneratedMonthlyPassId_query);
        if(!$getGeneratedMonthlyPassId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $passIdDetail = array();
            if(mysqli_num_rows($getGeneratedMonthlyPassId_result)==1){
                $row = mysqli_fetch_assoc($getGeneratedMonthlyPassId_result);
                $generatedParkedVehicleId = getGeneratedParkedVehicleId($row["LocationId"]);
                $passIdDetail = array("GeneratedMonthlyPassId"=>$row["GeneratedMonthlyPassId"], "GeneratedLocationId"=>$row["GeneratedLocationId"], "LocationId"=>$row["LocationId"], "GeneratedParkedVehicleId"=>$generatedParkedVehicleId);
                return $passIdDetail;
            }
            return $passIdDetail;
        }
    }

    function getGeneratedParkedVehicleId($locationId){
        global $connection, $parkedVehicleTable;
        $getGeneratedParkedVehicleId_query = "select GeneratedParkedVehicleId from ".$parkedVehicleTable." where LocationId = $locationId order by id desc limit 1";
        $getGeneratedParkedVehicleId_result = mysqli_query($connection, $getGeneratedParkedVehicleId_query);
        if(!$getGeneratedParkedVehicleId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getGeneratedParkedVehicleId_result)==1){
                $row = mysqli_fetch_assoc($getGeneratedParkedVehicleId_result);
                return $row["GeneratedParkedVehicleId"] + 1;
            }   
            else{
                return 1;
            } 
        }
    }

    function checkInParkedVehicleNumber(){
        global $connection, $vehicleNumber, $parkedVehicleTable;
        $checkVehicleNumberInParkedVehicle_query = "select id, FullName, MobileNumber, IsParkedVehicleReleased from ".$parkedVehicleTable." where VehicleNumber = '$vehicleNumber' order by id desc limit 1";
        $checkVehicleNumberInParkedVehicle_result = mysqli_query($connection, $checkVehicleNumberInParkedVehicle_query);
        if(!$checkVehicleNumberInParkedVehicle_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($checkVehicleNumberInParkedVehicle_result) == 1){
                $row = mysqli_fetch_assoc($checkVehicleNumberInParkedVehicle_result);
                $array_result = array("ParkedVehicleId"=>$row['id'], "FullName"=>$row["FullName"], 
                "MobileNumber"=>$row["MobileNumber"], "IsMonthlyPass"=>"false", "IsParkedVehicleReleased"=>$row["IsParkedVehicleReleased"]);
                return $array_result;
            }
        }
    }

    function updateMonthlyPassData($monthlyPassId){
        global $connection, $monthlyPassTable;
        $updateMonthlyPassActiveState_query = "update ".$monthlyPassTable." set ActiveMonthlyPassState = 0 where id = $monthlyPassId";
        $updateMonthlyPassActiveState_result = mysqli_query($connection, $updateMonthlyPassActiveState_query);
        if(!$updateMonthlyPassActiveState_result){
            echo "Error updating record '".mysqli_error($connection)."'";
        }
    }

?>