<?php

    require "init.php";
    require "create_locationMonthlyPassMappingTable.php";
    require "create_vehicleNumberMonthlyPassMappingTable.php";

    $monthlyPassId = $_GET["MonthlyPassId"];

    $getPassUserDetail_query = "select PassUserName, PassUserMobileNumber from ".$monthlyPassTable." where id = $monthlyPassId limit 1";
    $getPassUserDetail_result = mysqli_query($connection, $getPassUserDetail_query);
    if(!$getPassUserDetail_result){
        echo "Error retrieving record '".mysqli_error($connection)."'";
    }
    else
    {
        if(mysqli_num_rows($getPassUserDetail_result)==1){
            $row = mysqli_fetch_assoc($getPassUserDetail_result);
            $passUserDetail = array("PassUserName"=>$row["PassUserName"], "PassUserMobileNumber"=>$row["PassUserMobileNumber"]);
            $passIdDetail = getMonthlyPassGeneratedId();
            $vehicleNumbers = getVehicleNumbersByMonthlyPass();
            $array_result = array_merge($passUserDetail, $passIdDetail, array("vehicleNumberList"=>$vehicleNumbers));
            echo json_encode(array("server_response"=>$array_result));
        }
    }

    function getMonthlyPassGeneratedId(){
        global $connection, $monthlyPassId, $locationMonthlyPassMappingTable;
        $getGeneratedMonthlyPassId_query = "select GeneratedMonthlyPassId, GeneratedLocationId from ".$locationMonthlyPassMappingTable." where MonthlyPassId = $monthlyPassId limit 1";
        $getGeneratedMonthlyPassId_result = mysqli_query($connection, $getGeneratedMonthlyPassId_query);
        if(!$getGeneratedMonthlyPassId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($getGeneratedMonthlyPassId_result);
            $passIdDetail = array("GeneratedMonthlyId"=>$row["GeneratedMonthlyPassId"], "GeneratedLocationId"=>$row["GeneratedLocationId"]);
            return $passIdDetail;
        }
    }

    function getVehicleNumbersByMonthlyPass()
    {
        global $connection, $monthlyPassId, $vehicleNumberMonthlyPassMappingTable;
        $getVehicleNumberList_query = "select id, VehicleType, VehicleNumber from ".$vehicleNumberMonthlyPassMappingTable." where MonthlyPassId = $monthlyPassId";
        $getVehicleNumberList_result = mysqli_query($connection, $getVehicleNumberList_query);

        if(!$getVehicleNumberList_query){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $vehicleNumbers = array();
            while($row = mysqli_fetch_assoc($getVehicleNumberList_result)){
                array_push($vehicleNumbers,array("VehicleNumberMonthlyPassId"=>$row['id'],"VehicleType"=>$row["VehicleType"], "VehicleNumber"=>$row["VehicleNumber"]));
            }
            return $vehicleNumbers;
        }
    }

?>