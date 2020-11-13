<?php

    require "init.php";
    require "create_locationDetailTable.php";

    $vehicleType = $_GET["VehicleType"];
    $locationId = $_GET["LocationId"];

    $locationPaymentDetail = array("noData"=>null);         
    if($vehicleType == "Car"){
        $locationPaymentDetail_query = "select CarChargeType, CarFixHour, CarFixHourRate, CarChargesOption, CarChargesOptionRate from ".$locationDetailTable." where LocationId = $locationId";
        $locationPaymentDetail_result = mysqli_query($connection, $locationPaymentDetail_query);
        if(!$locationPaymentDetail_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($locationPaymentDetail_result)==1){
                $row = mysqli_fetch_assoc($locationPaymentDetail_result);
                $locationPaymentDetail = array("CarChargeType"=>$row["CarChargeType"],"CarFixHour"=>$row["CarFixHour"], 
                "CarFixHourRate"=>$row["CarFixHourRate"], "CarChargesOption"=>$row["CarChargesOption"],
                "CarChargesOptionRate"=>$row["CarChargesOptionRate"], "noData"=>"notNull");
            }
        }
    }
    else
    {
        $locationPaymentDetail_query = "select BikeChargeType, BikeFixHour, BikeFixHourRate, BikeChargesOption, BikeChargesOptionRate from ".$locationDetailTable." where LocationId = $locationId";
        $locationPaymentDetail_result = mysqli_query($connection, $locationPaymentDetail_query);
        if(!$locationPaymentDetail_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($locationPaymentDetail_result)==1){
                $row = mysqli_fetch_assoc($locationPaymentDetail_result);
                $locationPaymentDetail = array("BikeChargeType"=>$row["BikeChargeType"],"BikeFixHour"=>$row["BikeFixHour"],
                "BikeFixHourRate"=>$row["BikeFixHourRate"], "BikeChargesOption"=>$row["BikeChargesOption"],
                "BikeChargesOptionRate"=>$row["BikeChargesOptionRate"], "noData"=>"notNull");
            }
        }
    }

    echo json_encode($locationPaymentDetail);
?>