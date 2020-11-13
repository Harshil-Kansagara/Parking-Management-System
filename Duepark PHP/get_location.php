<?php

    require "init.php";
    //require "create_locationTable.php";
    require "create_locationDetailTable.php";
    require "create_locationParkingMappingTable.php";
    
    $locationId = $_GET["LocationId"];

    if(!empty($locationId)){
        $response = array();
        $location_query = "select * from ".$locationTable." where id = $locationId limit 1";
        $location_result = mysqli_query($connection, $location_query);

        if(!$location_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            $row = mysqli_fetch_assoc($location_result);
            $parkingDetail = getParkingDetail();
            $locationDetail = getLocationDetail();
            $location = array("id"=>$row["id"], "GeneratedLocationId"=>$row["GeneratedLocationId"], "LocationName"=>$row["LocationName"], 
            "LocationType"=>$row["LocationType"], "LocationAddress"=>$row["LocationAddress"], "LocationDate"=>$row["LocationDate"], "LocationTime"=>$row["LocationTime"],
            "LocationActiveState"=>$row["LocationActiveState"]);
            $array_result = array_merge($location, $locationDetail, $parkingDetail);
            array_push($response, $array_result);
            echo json_encode(array('server_response'=>$response));
        }
    }

    function getLocationDetail(){
        global $connection, $locationDetailTable, $locationId;

        $locationDetail_query = "select * from ".$locationDetailTable." where LocationId = $locationId limit 1";
        $locationDetail_result = mysqli_query($connection, $locationDetail_query);

        if(!$locationDetail_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $locationDetail = array();
            if(mysqli_num_rows($locationDetail_result)==1){
                $row = mysqli_fetch_assoc($locationDetail_result);
                if(empty($row["LocationOpenTime"])){
                    $locationOpenTime = null;
                }
                else{
                    $locationOpenTime = $row["LocationOpenTime"];
                }

                if(empty($row["LocationCloseTime"])){
                    $locationCloseTime = null;
                }
                else{
                    $locationCloseTime = $row["LocationCloseTime"];
                }

                if(empty($row["CarChargeType"])){
                    $carChargeType = null;
                }
                else{
                    $carChargeType = $row["CarChargeType"];
                }

                if(empty($row["CarFixHour"])){
                    $carFixHour = null;
                }
                else{
                    $carFixHour = $row["CarFixHour"];
                }

                if(empty($row["CarFixHourRate"])){
                    $carFixHourRate = null;
                }
                else{
                    $carFixHourRate = $row["CarFixHourRate"];
                }

                if(empty($row["CarChargesOption"])){
                    $carChargesOption = null;
                }
                else{
                    $carChargesOption = $row["CarChargesOption"];
                }

                if(empty($row["CarChargesOptionRate"])){
                    $carChargesOptionRate = null;
                }
                else{
                    $carChargesOptionRate = $row["CarChargesOptionRate"];
                }

                if(empty($row["CarCapacity"])){
                    $carCapacity = null;
                }
                else{
                    $carCapacity = $row["CarCapacity"];
                }

                if(empty($row["BikeChargeType"])){
                    $bikeChargeType = null;
                }
                else{
                    $bikeChargeType = $row["BikeChargeType"];
                }

                if(empty($row["BikeFixHour"])){
                    $bikeFixHour = null;
                }
                else{
                    $bikeFixHour = $row["BikeFixHour"];
                }

                if(empty($row["BikeFixHourRate"])){
                    $bikeFixHourRate = null;
                }
                else{
                    $bikeFixHourRate = $row["BikeFixHourRate"];
                }

                if(empty($row["BikeChargesOption"])){
                    $bikeChargesOption = null;
                }
                else{
                    $bikeChargesOption = $row["BikeChargesOption"];
                }

                if(empty($row["BikeChargesOptionRate"])){
                    $bikeChargesOptionRate = null;
                }
                else{
                    $bikeChargesOptionRate = $row["BikeChargesOptionRate"];
                }

                if(empty($row["BikeCapacity"])){
                    $bikeCapacity = null;
                }
                else{
                    $bikeCapacity = $row["BikeCapacity"];
                }

                if(empty($row["CarMonthlyPassRate"])){
                    $carMonthlyPassRate = null;
                }
                else{
                    $carMonthlyPassRate = $row["CarMonthlyPassRate"];
                }

                if(empty($row["BikeMonthlyPassRate"])){
                    $bikeMonthlyPassRate = null;
                }
                else{
                    $bikeMonthlyPassRate = $row["BikeMonthlyPassRate"];
                }

                $locationDetail = array("LocationDetailId"=>$row["id"], "LocationOpenTime"=>$locationOpenTime, "LocationCloseTime"=>$locationCloseTime, "CarChargeType"=>$carChargeType,
                    "CarFixHour"=>$carFixHour, "CarFixHourRate"=>$carFixHourRate, 
                    "CarChargesOption"=>$carChargesOption, "CarChargesOptionRate"=>$carChargesOptionRate, 
                    "CarCapacity"=>$carCapacity, "BikeChargeType"=>$bikeChargeType, "BikeFixHour"=>$bikeFixHour, 
                    "BikeFixHourRate"=>$bikeFixHourRate, "BikeChargesOption"=>$bikeChargesOption, 
                    "BikeChargesOptionRate"=>$bikeChargesOptionRate, "BikeCapacity"=>$bikeCapacity, 
                    "CarMonthlyPassRate"=>$carMonthlyPassRate, "BikeMonthlyPassRate"=>$bikeMonthlyPassRate);
            }
            else
            {
                $locationDetail = array("LocationDetailId"=>null, "LocationOpenTime"=>null, "LocationCloseTime"=>null, "CarChargeType"=>null,
                "CarFixHour"=>null, "CarFixHourRate"=>null, "CarChargesOption"=>null, "CarChargesOptionRate"=>null, 
                "CarCapacity"=>null, "BikeChargeType"=>null, "BikeFixHour"=>null, "BikeFixHourRate"=>null,
                "BikeChargesOption"=>null, "BikeChargesOptionRate"=>null, "BikeCapacity"=>null, 
                "CarMonthlyPassRate"=>null, "BikeMonthlyPassRate"=>null);
            }
            return $locationDetail;
        }
    }

    function getParkingDetail(){
        global $connection, $locationId, $parkingLocationMappingTable, $parkingTable;

        $parkingId_query = "select ParkingId from ".$parkingLocationMappingTable." where LocationId = $locationId limit 1";
        $parkingId_result = mysqli_query($connection, $parkingId_query);
        if(!$parkingId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($parkingId_result) == 1){
                $row = mysqli_fetch_assoc($parkingId_result);
                $parkingId = $row["ParkingId"];
                $parkingData_query = "select ParkingId, ParkingAcronym, ParkingName from ".$parkingTable." where id = $parkingId limit 1";
                $parkingData_result = mysqli_query($connection, $parkingData_query);
                if(!$parkingData_result){
                    echo "Error retrieving record '".mysqli_error($connection)."'";
                }
                else{
                    if(mysqli_num_rows($parkingData_result)== 1){
                        $parkingRow = mysqli_fetch_assoc($parkingData_result);
                        $parkingArray = array("GeneratedParkingId"=>$parkingRow["ParkingId"], "ParkingAcronym"=>$parkingRow["ParkingAcronym"], "ParkingName"=>$parkingRow["ParkingName"]);
                        return $parkingArray;
                    }
                    else{
                        return array();
                    }
                }
            }
        }
    }

?>