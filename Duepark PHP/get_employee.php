<?php

    require "init.php";
    require "create_employeeTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_employeeLocationMappingTable.php";
    require "create_parkingEmployeeMappingTable.php";
    //require "create_locationTable.php";

    $employeeId = $_GET["EmployeeId"];
    $entity =  $_GET["Entity"];
    $response = array();
    
    $getEmployeeData_query = "select id, EmployeeName, EmployeeMobileNumber, EmployeeEmailId, EmployeePassword, EmployeeAdharNumber, EmployeeProfilePic, VehicleType from ".$employeeTable." where id = $employeeId limit 1";
    $getEmployeeData_result = mysqli_query($connection, $getEmployeeData_query);

    if(!$getEmployeeData_result){
        echo  "Error retrieving record '".mysqli_error($connection)."'";
    }
    else{
        $generatedEmployeeIdData = getGeneratedEmployeeAndRole();
        $row = mysqli_fetch_assoc($getEmployeeData_result);
        if($entity == "AdminApp"){
            $parkingCount = getParkingCount($employeeId);
        }
        else{
            $parkingCount = 0;
        }
        $employeeData = array("id"=>$row['id'],"EmployeeName"=>$row["EmployeeName"], "EmployeeMobileNumber"=>$row["EmployeeMobileNumber"], 
                        "EmployeeEmailId"=>$row["EmployeeEmailId"], "EmployeePassword"=>$row["EmployeePassword"], "EmployeeAdharNumber"=>$row["EmployeeAdharNumber"], 
                        "EmployeeProfilePic"=>$row["EmployeeProfilePic"], "ParkingCount"=>$parkingCount, "VehicleType"=>$row["VehicleType"]);

        $array_result = array_merge($employeeData, $generatedEmployeeIdData);
        array_push($response, $array_result);
        echo json_encode(array('server_response'=>$response));
    }

    function getGeneratedEmployeeAndRole(){
        global $connection, $employeeEntityRoleMappingTable, $employeeId, $entity;

        $getEmployeeEntityRoleMapping_query = "select GeneratedEmployeeId, RoleId from ".$employeeEntityRoleMappingTable." where EmployeeId = $employeeId limit 1";
        $getEmployeeEntityRoleMapping_result = mysqli_query($connection, $getEmployeeEntityRoleMapping_query);

        if(!$getEmployeeEntityRoleMapping_result){
            echo "Error retrieveing record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($getEmployeeEntityRoleMapping_result);
            $role = getRole($row["RoleId"]);
            if($entity == "ConsumerApp" && ($role == "Manager" || $role == "Valet")){
                $locationName = getLocationData();
            }
            else{
                $locationName = null;
            }
            $generatedEmployeeIdData = array("GeneratedEmployeeId"=>$row['GeneratedEmployeeId'], "EmployeeRole"=>$role, "LocationName"=>$locationName);
            return $generatedEmployeeIdData;
        }
    }

    function getLocationData(){
        global $connection, $employeeLocationMappingTable, $employeeId, $locationTable;
        $getEmployeeLocation_query = "select LocationId from ".$employeeLocationMappingTable." where EmployeeId=$employeeId limit 1";
        $getEmployeeLocation_result = mysqli_query($connection, $getEmployeeLocation_query);
        if(!$getEmployeeLocation_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getEmployeeLocation_result)==1){
                $row = mysqli_fetch_assoc($getEmployeeLocation_result);
                $locationId = $row["LocationId"];
                if($locationId!=null){
                    $getLocationName_query = "select LocationName from ".$locationTable." where id = $locationId limit 1";
                    $getLocationName_result = mysqli_query($connection, $getLocationName_query);
                    if(!$getLocationName_result){
                        echo "Error retrieving record '".mysqli_error($connection)."'";
                    }
                    else{
                        $row = mysqli_fetch_assoc($getLocationName_result);
                        return $row["LocationName"];
                    }
                }
            }
            else{
                return null;
            }
        }
    }

    function getParkingCount($employeeId){
        global $connection, $parkingEmployeeMappingTable;
        $getAllAdminAppEmployeeId_query = "select ParkingId from ".$parkingEmployeeMappingTable." where AdminAppEmployeeId = $employeeId";
        $getAllAdminAppEmployeeId_result = mysqli_query($connection, $getAllAdminAppEmployeeId_query);
        if(!$getAllAdminAppEmployeeId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            return mysqli_num_rows($getAllAdminAppEmployeeId_result);
        }
    }

    function getRole($roleId){
        global $connection, $employeeRoleTable;
        $getRoleName_query = "select Role from ".$employeeRoleTable." where id = $roleId limit 1";
        $getRoleName_result = mysqli_query($connection, $getRoleName_query);
        if(!$getRoleName_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($getRoleName_result);
            return $row['Role'];
        }
    }

?>