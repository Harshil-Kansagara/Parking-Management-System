<?php

    require "init.php";
    //require "create_locationTable.php";
    require "create_locationDetailTable.php";

    $locationId = $_POST["LocationId"];
    $locationName = $_POST["LocationName"];
    $locationAddress = $_POST["LocationAddress"];
    $locationType = $_POST["LocationType"];
    $locationDetailId = $_POST["LocationDetailId"];
    $locationOpenTime = $_POST["LocationOpenTime"];
    $locationCloseTime = $_POST["LocationCloseTime"];
    $carChargeType = $_POST["CarChargeType"];
    $carFixHour = $_POST["CarFixHour"];
    $carFixHourRate = $_POST["CarFixHourRate"];
    $carChargesOption = $_POST["CarChargesOption"];
    $carChargesOptionRate = $_POST["CarChargesOptionRate"];
    $carCapacity = $_POST["CarCapacity"];
    $bikeChargeType = $_POST["BikeChargeType"];
    $bikeFixHour = $_POST["BikeFixHour"];
    $bikeFixHourRate = $_POST["BikeFixHourRate"];
    $bikeChargesOption = $_POST["BikeChargesOption"];
    $bikeChargesOptionRate = $_POST["BikeChargesOptionRate"];
    $bikeCapacity = $_POST["BikeCapacity"];
    $carMonthlyPassRate = $_POST["CarMonthlyPassRate"];
    $bikeMonthlyPassRate = $_POST["BikeMonthlyPassRate"];


    if($locationDetailId == "null"){
        addLocationDetailData();
    }
    else{
        updateLocationDetailData();
    }

    function addLocationDetailData(){
        global $connection, $locationDetailTable, $locationId,  $locationOpenTime, $locationCloseTime, $carChargeType, $carFixHour, $carFixHourRate, $carChargesOption, $carChargesOptionRate, 
            $carCapacity, $bikeChargeType, $bikeFixHour, $bikeFixHourRate, $bikeChargesOption, $bikeChargesOptionRate, $bikeCapacity, $carMonthlyPassRate, $bikeMonthlyPassRate; 

        $addLocationDetail_query = "insert into $locationDetailTable (LocationId, LocationOpenTime, LocationCloseTime, CarChargeType, CarFixHour, CarFixHourRate, CarChargesOption, CarChargesOptionRate, CarCapacity, 
        BikeChargeType, BikeFixHour, BikeFixHourRate, BikeChargesOption, BikeChargesOptionRate, BikeCapacity, CarMonthlyPassRate, BikeMonthlyPassRate) values ($locationId, '$locationOpenTime', '$locationCloseTime', '$carChargeType',
        '$carFixHour', '$carFixHourRate', '$carChargesOption', '$carChargesOptionRate', '$carCapacity', '$bikeChargeType', '$bikeFixHour', '$bikeFixHourRate', '$bikeChargesOption', '$bikeChargesOptionRate', 
        '$bikeCapacity', '$carMonthlyPassRate', '$bikeMonthlyPassRate')";

        $addLocationDetail_result = mysqli_query($connection, $addLocationDetail_query);
        if(!$addLocationDetail_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else{
            updateLocationData();
        }

    }

    function updateLocationDetailData(){
        global $connection, $locationDetailTable, $locationDetailId,  $locationOpenTime, $locationCloseTime, $carChargeType, $carFixHour, $carFixHourRate, $carChargesOption, $carChargesOptionRate,
            $carCapacity, $bikeChargeType, $bikeFixHour, $bikeFixHourRate, $bikeChargesOption, $bikeChargesOptionRate, $bikeCapacity, $carMonthlyPassRate, $bikeMonthlyPassRate;

        $updateLocationDetail_query = "update ".$locationDetailTable." set LocationOpenTime = '$locationOpenTime', LocationCloseTime = '$locationCloseTime', CarChargeType = '$carChargeType', 
        CarFixHour = '$carFixHour', CarFixHourRate = '$carFixHourRate', CarChargesOption='$carChargesOption', CarChargesOptionRate = '$carChargesOptionRate', CarCapacity = '$carCapacity', 
        BikeChargeType = '$bikeChargeType',  BikeFixHour = '$bikeFixHour', BikeFixHourRate = '$bikeFixHourRate', BikeChargesOption='$bikeChargesOption', bikeChargesOptionRate = '$bikeChargesOptionRate', 
        BikeCapacity = '$bikeCapacity', CarMonthlyPassRate = '$carMonthlyPassRate', BikeMonthlyPassRate = '$bikeMonthlyPassRate' where id = $locationDetailId";
        $updateLocationDetail_result = mysqli_query($connection, $updateLocationDetail_query);

        if(!$updateLocationDetail_result){
            echo "Error updating record '".mysqli_error($connection)."'";
        }
        else{
            updateLocationData();
        }
    }

    function updateLocationData(){
        global $connection, $locationTable, $locationName, $locationAddress, $locationType, $locationId;
        $updateLocation_query="update ".$locationTable." set LocationName='$locationName', LocationAddress='$locationAddress', LocationType='$locationType' 
            where id=$locationId";

        $updateLocation_result = mysqli_query($connection,$updateLocation_query);

        if($updateLocation_result){
            echo "UpdatedLocation";
        }
        else{
            echo "NotupdatedLocation";
        }
    }

?>