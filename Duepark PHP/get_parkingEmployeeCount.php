<?php

    require "init.php";
    //require "create_parkingTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_locationParkingMappingTable.php";
    require "create_employeeLocationMappingTable.php";

    $parkingId = $_GET["ParkingId"];
    $totalSuperAdminCount = 0;
    $totalAdminCount = 0;
    $totalManagerCount = 0;
    $totalValetCount = 0;
    $locationEmployeeCount = array();
    
    if(!empty($parkingId)){
        $getEmployeeIdList_query = "select ConsumerAppEmployeeId from ".$parkingEmployeeMappingTable." where ParkingId = $parkingId";
        $getEmployeeIdList_result = mysqli_query($connection, $getEmployeeIdList_query);
        if(!$getEmployeeIdList_result){
            echo "Error retrieveing record '".mysqli_error($connection)."'";
        }            
        else{
            $parkingDataArray = getParkingData();
            if(mysqli_num_rows($getEmployeeIdList_result)>0){
                while($row = mysqli_fetch_assoc($getEmployeeIdList_result)){
                    $employeeId = $row["ConsumerAppEmployeeId"];
                    if($employeeId!=0){
                        $getEmployeeRole_query = "select RoleId from ".$employeeEntityRoleMappingTable." where EmployeeId = $employeeId limit 1";
                        $getEmployeeRole_result = mysqli_query($connection, $getEmployeeRole_query);
                        if(!$getEmployeeRole_result)
                        {
                            echo "Error retrieveing record '".mysqli_error($connection)."'";
                        }
                        else
                        {
                        if(mysqli_num_rows($getEmployeeRole_result) == 1){
                            $row1 = mysqli_fetch_assoc($getEmployeeRole_result);
                            $roleId = $row1["RoleId"];
                            if($roleId == 1){
                                    $totalSuperAdminCount = $totalSuperAdminCount + 1;
                            }
                            else if($roleId == 2){
                                $totalAdminCount = $totalAdminCount + 1;
                            }
                            else if($roleId == 3){
                                $totalManagerCount = $totalManagerCount + 1;
                            }
                            else if($roleId == 4){
                                $totalValetCount = $totalValetCount + 1;
                            }
                        } 
                        }
                    }
                }
            }
            $locationEmployeeCount = getLocationEmployeeCount();
            $array_result = array_merge($parkingDataArray, array("TotalSuperAdminCount"=>$totalSuperAdminCount, "TotalAdminCount"=>$totalAdminCount, 
                                          "TotalManagerCount"=>$totalManagerCount, "TotalValetCount"=>$totalValetCount));
            array_push($array_result, $locationEmployeeCount);
            echo json_encode($array_result);
        }
    }

    function getParkingData(){
        global $connection, $parkingTable, $parkingId;

        $parkingData_query = "select * from ".$parkingTable." where id = $parkingId limit 1";
        $parkingData_result = mysqli_query($connection, $parkingData_query);
        if(!$parkingData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($parkingData_result)==1){
                $row = mysqli_fetch_assoc($parkingData_result);
                $parkingDataArray = array('ParkingId'=>$row['id'],'GeneratedParkingId'=>$row['ParkingId'], 
                                        'ParkingAcronym'=>$row['ParkingAcronym'],'ParkingName'=>$row['ParkingName']);
            }
            return $parkingDataArray;
        }
    }

    function getLocationEmployeeCount()
    {
        $locationEmployeeData = array();
        global $connection, $parkingLocationMappingTable, $parkingId, $employeeLocationMappingTable, $employeeEntityRoleMappingTable;
        $getAllLocationId_query = "select LocationId from ".$parkingLocationMappingTable." where ParkingId=$parkingId";
        $getAllLocationId_result = mysqli_query($connection, $getAllLocationId_query);
        if(!$getAllLocationId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getAllLocationId_result)>0){
                while($row = mysqli_fetch_assoc($getAllLocationId_result))
                {
                    $locationManagerCount = 0;
                    $locationValetCount = 0;
                    $locationId = $row["LocationId"];
                    $locationData = getLocationData($locationId);
                    $getLocationAllEmployeeId_query = "select EmployeeId from ".$employeeLocationMappingTable." where LocationId = $locationId";
                    $getLocationAllEmployeeId_result = mysqli_query($connection, $getLocationAllEmployeeId_query);
                    if(!$getLocationAllEmployeeId_result){
                        echo "Error retrieving record '".mysqli_error($connection)."'";
                    }
                    else{
                        if(mysqli_num_rows($getLocationAllEmployeeId_result)>0){
                            while($row1 = mysqli_fetch_assoc($getLocationAllEmployeeId_result)){
                                $employeeId = $row1["EmployeeId"];
                                $getEmployeeRole_query = "select RoleId from ".$employeeEntityRoleMappingTable." where EmployeeId=$employeeId limit 1";
                                $getEmployeeRole_result = mysqli_query($connection, $getEmployeeRole_query);
                                if(!$getEmployeeRole_result)
                                {
                                    echo "Error retrieveing record '".mysqli_error($connection)."'";
                                }
                                else
                                {
                                    if(mysqli_num_rows($getEmployeeRole_result) == 1){
                                        $row2 = mysqli_fetch_assoc($getEmployeeRole_result);
                                        $roleId = $row2["RoleId"];
                                        if($roleId == 3){
                                            $locationManagerCount = $locationManagerCount + 1;
                                        }
                                        else if($roleId == 4){
                                            $locationValetCount = $locationValetCount + 1;
                                        }
                                    } 
                                }
                            }
                        }
                    }
                    $result = array_merge($locationData, array("LocationManagerCount"=>$locationManagerCount, "LocationValetCount"=>$locationValetCount));
                    array_push($locationEmployeeData, $result);
                    
                }
            }
            //echo json_encode($locationEmployeeData);
            return $locationEmployeeData;
        }
    }

    function getLocationData($locationId){
        global $connection, $locationTable;
        $getLocationDetail_query = "select GeneratedLocationId, LocationName from ".$locationTable." where id = $locationId limit 1";
        $getLocationDetail_result = mysqli_query($connection, $getLocationDetail_query);
        if(!$getLocationDetail_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getLocationDetail_result)==1){
                $row = mysqli_fetch_assoc($getLocationDetail_result);
                return array("LocationId"=>$locationId,"GeneratedLocationId"=>$row["GeneratedLocationId"], "LocationName"=>$row["LocationName"]);
            }
        }
    }

?>