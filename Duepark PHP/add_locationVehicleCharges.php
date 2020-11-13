<?php

    require "init.php";
    require "create_locationDetailTable.php";

    $locationId = $_POST["LocationId"];
    $chargeType = $_POST["ChargeType"];
    $vehicleType = $_POST["VehicleType"];
    $fixHour = $_POST["FixHour"];
    $fixHourCharges = $_POST["FixHourCharges"];
    $chargesOption = $_POST["ChargesOption"];
    $chargesOptionRate = $_POST["ChargesOptionRate"];
    $locationDetailId = null;

    $getLocationDetailId_query = "select id from ".$locationDetailTable." where LocationId = $locationId limit 1";
    $getLocationDetailId_result = mysqli_query($connection, $getLocationDetailId_query);
    if(!$getLocationDetailId_result){
        echo "Error retrieving record '" . mysqli_error($connection) . "'";
    }
    else{
        if(mysqli_num_rows($getLocationDetailId_result) == 1){
            $row = mysqli_fetch_assoc($getLocationDetailId_result);
            $locationDetailId = $row["id"];
        }
    }

    $isAdd = true;
    $query = null;
    if(empty($locationDetailId)){
        if($vehicleType == "Car"){
            $query = "insert into ".$locationDetailTable." (LocationId, LocationOpenTime, LocationCloseTime, CarChargeType, CarFixHour, CarFixHourRate, CarChargesOption, CarChargesOptionRate) values 
                ($locationId, null, null, '$chargeType', '$fixHour', '$fixHourCharges', '$chargesOption', '$chargesOptionRate')";
        }
        else{
            $query = "insert into ".$locationDetailTable." (LocationId, LocationOpenTime, LocationCloseTime, BikeChargeType, BikeFixHour, BikeFixHourRate, BikeChargesOption, BikeChargesOptionRate) values 
            ($locationId, null, null, '$chargeType', '$fixHour', '$fixHourCharges', '$chargesOption', '$chargesOptionRate')";
        }
    }
    else{
        $isAdd = false;
        if($vehicleType == "Car"){
            $query = "update ".$locationDetailTable." set CarChargeType = '$chargeType', CarFixHour = '$fixHour', 
                CarFixHourRate = '$fixHourCharges', CarChargesOption = '$chargesOption', CarChargesOptionRate='$chargesOptionRate' 
                where id = $locationDetailId";
        }
        else{
            $query = "update ".$locationDetailTable." set BikeChargeType = '$chargeType', BikeFixHour = '$fixHour', 
                BikeFixHourRate = '$fixHourCharges', BikeChargesOption = '$chargesOption', BikeChargesOptionRate='$chargesOptionRate' 
                where id = $locationDetailId";
        }
    }

    if(!empty($query)){
        $result = mysqli_query($connection, $query);
        if(!$result){
            if($isAdd){
                echo "Error creating record '".mysqli_error($connection)."'";
            }
            else{
                echo "Error updating record '".mysqli_error($connection)."'";
            }
        }
        else{
            echo "successful";
        }
    }
?>