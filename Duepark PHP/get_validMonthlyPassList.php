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
    else
    {
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
        global $connection, $monthlyPassTable, $currentDate;
        $getMonthlyPassData_query = "select PassUserName, ExpiryDate from ".$monthlyPassTable." where id = $monthlyPassId and ActiveMonthlyPassState = 1 limit 1";
        $getMonthlyPassData_result = mysqli_query($connection, $getMonthlyPassData_query);
        if(!$getMonthlyPassData_query){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getMonthlyPassData_result) == 1){
                $monthlyPassData = array();
                $row = mysqli_fetch_assoc($getMonthlyPassData_result);
                if(strtotime($row["ExpiryDate"])<strtotime("today")){
                    updateMonthlyPassData($monthlyPassId);
                    return $monthlyPassData;
                }
                else{
                    $monthlyPassData = array("PassUserName"=>$row["PassUserName"]);
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
?>