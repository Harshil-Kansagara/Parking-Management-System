<?php

    require "init.php";
    require "create_parkedVehicleTable.php";
    require "create_monthlyPassTable.php";
    require "create_locationMonthlyPassMappingTable.php";

    $parkedVehicleId = $_GET["ParkedVehicleId"];

    if(!empty($parkedVehicleId)){
        $getParkedVehicleDetail_query = "select * from ".$parkedVehicleTable." where id = $parkedVehicleId limit 1";
        $getParkedVehicleDetail_result = mysqli_query($connection, $getParkedVehicleDetail_query);

        if(!$getParkedVehicleDetail_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getParkedVehicleDetail_result)==1){
                $row  = mysqli_fetch_assoc($getParkedVehicleDetail_result);
                if($row["MonthlyPassId"] != null){
                    $monthlyPassData = getMonthlyPassDates($row["MonthlyPassId"]);
                    if(!empty($monthlyPassData["IssuedDate"])){
                        $parkedVehicleData = array("Id"=>$row["id"], "MonthlyPassId"=>$row["MonthlyPassId"], 
                        "FullName"=>$row["FullName"], "MobileNumber"=>$row["MobileNumber"], "ParkedPaymentType"=>$row["ParkedPaymentType"],
                        "ParkedTime"=>$row["ParkedTime"], "ParkedDate"=>$row["ParkedDate"], "IsMonthlyPass"=>"true");
                        $array_result = array_merge($parkedVehicleData, $monthlyPassData);
                    }
                }
                else
                {
                   $parkedVehicleData = array("Id"=>$row["id"], "LocationId" => $row["LocationId"], 
                        "FullName"=>$row["FullName"], "MobileNumber"=>$row["MobileNumber"], "PaidAmount"=>$row["PaidAmount"],
                        "PaymentTimeRate"=>$row["PaymentTimeRate"], "IsPayLater"=>$row["IsPayLater"], "IsParkingFree"=>$row["IsParkingFree"],
                        "ParkedPaymentType"=>$row["ParkedPaymentType"],
                        "ParkedTime"=>$row["ParkedTime"], "ParkedDate"=>$row["ParkedDate"], "IsMonthlyPass"=>"false");
                    if(!$row["IsParkingFree"]){
                        $locationData = getLocationDetail($row["LocationId"], $row["VehicleType"]);
                        $array_result = array_merge($parkedVehicleData, $locationData);
                    }
                    else{
                        $array_result = $parkedVehicleData;
                    }
                }
                echo json_encode($array_result);
            }
        }
    }

    // Here change the table name
    function getLocationDetail($locationId, $vehicleType){
        global $connection, $locationTable;
        $locationPaymentDetail = array();
        if($vehicleType == "Car"){
            $locationPaymentDetail_query = "select LocationCloseTime, CarFixHour, CarFixHourRate, CarChargesOption, CarChargesOptionRate from locationdetail where LocationId = $locationId limit 1";
            $locationPaymentDetail_result = mysqli_query($connection, $locationPaymentDetail_query);
            if(!$locationPaymentDetail_result){
                echo "Error retrieving record '".mysqli_error($connection)."'";
            }
            else
            {
                if(mysqli_num_rows($locationPaymentDetail_result)==1){
                    $row = mysqli_fetch_assoc($locationPaymentDetail_result);
                    $locationPaymentDetail = array("LocationCloseTime"=>$row["LocationCloseTime"], "CarFixHour"=>$row["CarFixHour"], "CarFixHourRate"=>$row["CarFixHourRate"],
                    "CarChargesOption"=>$row["CarChargesOption"], "CarChargesOptionRate"=>$row["CarChargesOptionRate"]);
                }
            }
        }
        else{
            $locationPaymentDetail_query = "select LocationCloseTime, BikeFixHour, BikeFixHourRate, BikeChargesOption, BikeChargesOptionRate from locationdetail where LocationId = $locationId";
            $locationPaymentDetail_result = mysqli_query($connection, $locationPaymentDetail_query);
            if(!$locationPaymentDetail_result){
                echo "Error retrieving record '".mysqli_error($connection)."'";
            }
            else{
                if(mysqli_num_rows($locationPaymentDetail_result)==1){
                    $row = mysqli_fetch_assoc($locationPaymentDetail_result);
                    $locationPaymentDetail = array("LocationCloseTime"=>$row["LocationCloseTime"], "BikeFixHour"=>$row["BikeFixHour"], "BikeFixHourRate"=>$row["BikeFixHourRate"], 
                    "BikeChargesOption"=>$row["BikeChargesOption"], "BikeChargesOptionRate"=>$row["BikeChargesOptionRate"]);
                }
            }
        }
        return $locationPaymentDetail;
    }

    function getMonthlyPassDates($monthlyPassId){
        global $connection, $monthlyPassTable;
        $getMonthlyPassId_query = "select IssuedDate, ExpiryDate from ".$monthlyPassTable." where id = $monthlyPassId limit 1";
        $getMonthlyPassId_result = mysqli_query($connection, $getMonthlyPassId_query);
        if(!$getMonthlyPassId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getMonthlyPassId_result)==1){
                $monthlyPassDates = array();
                $row = mysqli_fetch_assoc($getMonthlyPassId_result);
                if(strtotime($row["ExpiryDate"])<strtotime("today")){
                    updateMonthlyPassData($monthlyPassId);
                    return $monthlyPassDates;
                }
                else{
                    $generatedMonthlyPassIdData = getGeneratedMonthlyPassId($monthlyPassId);
                    $monthlyPassDates = array("IssuedDate"=>$row["IssuedDate"], "ExpiryDate"=>$row["ExpiryDate"]);
                    $monthlyPassData = array_merge($generatedMonthlyPassIdData, $monthlyPassDates);
                    return $monthlyPassData;
                }
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