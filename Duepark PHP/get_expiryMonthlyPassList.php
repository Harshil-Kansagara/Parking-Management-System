<?php

    require "init.php";
    require "create_monthlyPassTable.php";
    require "create_locationMonthlyPassMappingTable.php";

    $parkingId = $_GET["ParkingId"];
    $locationId = $_GET["LocationId"];
    $response = array();

    if($locationId == "0"){
        $getAllMonthlyPassId_query = "select MonthlyPassId, GeneratedLocationId, GeneratedMonthlyPassId from ".$locationMonthlyPassMappingTable." where ParkingId = $parkingId";
        $getAllMonthlyPassId_result = mysqli_query($connection, $getAllMonthlyPassId_query);
        if(!$getAllMonthlyPassId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getAllMonthlyPassId_result)>0){
                while($row = mysqli_fetch_assoc($getAllMonthlyPassId_result)){
                    $idData = array("MonthlyPassId"=>$row["MonthlyPassId"],"GeneratedLocationId"=>$row["GeneratedLocationId"], "GeneratedMonthlyPassId"=>$row["GeneratedMonthlyPassId"]);
                    $monthlyPassData = getMonthlyPassData($row["MonthlyPassId"]);
                    if(!empty($monthlyPassData["PassUserName"])){
                        $array_result = array_merge($idData, $monthlyPassData);
                        array_push($response, $array_result);   
                    }
                }
                echo json_encode(array("server_response"=>$response));
            }
        }
    }
    else{
        $getMonthlyPassIdPerLocation_query = "select MonthlyPassId, GeneratedLocationId, GeneratedMonthlyPassId from ".$locationMonthlyPassMappingTable." where ParkingId = $parkingId and LocationId = $locationId";
        $getMonthlyPassIdPerLocation_result = mysqli_query($connection, $getMonthlyPassIdPerLocation_query);
        if(!$getMonthlyPassIdPerLocation_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getMonthlyPassIdPerLocation_result)>0){
                while($row = mysqli_fetch_assoc($getMonthlyPassIdPerLocation_result)){
                    $idData = array("MonthlyPassId"=>$row["MonthlyPassId"],"GeneratedLocationId"=>$row["GeneratedLocationId"], "GeneratedMonthlyPassId"=>$row["GeneratedMonthlyPassId"]);
                    $monthlyPassData = getMonthlyPassData($row["MonthlyPassId"]);
                    if(!empty($monthlyPassData["PassUserName"])){
                        $array_result = array_merge($idData, $monthlyPassData);
                        array_push($response, $array_result);   
                    }
                }
                echo json_encode(array("server_response"=>$response));
            }
        }
    }

    function getMonthlyPassData($monthlyPassId){
        global $connection, $monthlyPassTable;
        $getMonthlyPassData_query = "select PassUserName from ".$monthlyPassTable." where id = $monthlyPassId and ActiveMonthlyPassState = 0 limit 1";
        $getMonthlyPassData_result = mysqli_query($connection, $getMonthlyPassData_query);
        if(!$getMonthlyPassData_query){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getMonthlyPassData_result)==1){
                $row = mysqli_fetch_assoc($getMonthlyPassData_result);
                $monthlyPassData = array("PassUserName"=>$row["PassUserName"]);
                return $monthlyPassData;
            }
        }
    }

?>