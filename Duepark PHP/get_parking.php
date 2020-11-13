<?php

    require "init.php";
    require "create_parkingTable.php";
    require "create_employeeTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_employeeEntityRoleMappingTable.php";

    $response = array();
    if(isset($_GET["EmployeeId"]))
    {
        $employeeId = $_GET["EmployeeId"];
    }
    else{
        $employeeId = null;
    }

    if(isset($_GET["ParkingId"]))
    {
        $parkingId = $_GET["ParkingId"];
    }
    else{
        $parkingId = null;
    }

    if($employeeId != null){
        getParkingIdByEmployeeId();
    }
    if($parkingId != null){
        getEmployeeIdByParkingId();
    }

    function getParkingIdByEmployeeId(){
        global $connection, $employeeId, $parkingEmployeeMappingTable;
        $getParkingId_query = "select ParkingId from ".$parkingEmployeeMappingTable." where ConsumerAppEmployeeId = $employeeId limit 1";
        $getParkingId_result = mysqli_query($connection, $getParkingId_query);
        if(!$getParkingId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($getParkingId_result);
            $parkingId = $row['ParkingId'];
            getParkingData($parkingId, $employeeId);
        }
    }

    function getEmployeeIdByParkingId(){
        global $connection, $parkingId, $parkingEmployeeMappingTable;
        $getConsumerAppEmployeeId_query = "select ConsumerAppEmployeeId from ".$parkingEmployeeMappingTable." where ParkingId = $parkingId limit 1";
        $getConsumerAppEmployeeId_result = mysqli_query($connection, $getConsumerAppEmployeeId_query);
        if(!$getConsumerAppEmployeeId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($getConsumerAppEmployeeId_result);
            $employeeId = $row['ConsumerAppEmployeeId'];
            getParkingData($parkingId, $employeeId);
        }
    }

    function getParkingData($parkingId, $employeeId){
        global $connection, $parkingTable, $response;

        $employeeDataArray = getEmployeeData($employeeId);
        $parkingData_query = "select * from ".$parkingTable." where id = $parkingId limit 1";
        $parkingData_result = mysqli_query($connection, $parkingData_query);
        if(!$parkingData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($parkingData_result)==1){
                $row = mysqli_fetch_assoc($parkingData_result);
                $parkingDataArray = array('ParkingId'=>$row['id'],'GeneratedParkingId'=>$row['ParkingId'], 'ParkingAcronym'=>$row['ParkingAcronym'],'ParkingName'=>$row['ParkingName'],
                    'ParkingType'=>$row['ParkingType'],'ParkingAddress'=>$row['ParkingAddress'],'ParkingCity'=>$row['ParkingCity'], 'ParkingDate'=>$row['ParkingDate'], 
                    'ParkingTime'=>$row['ParkingTime'],'ParkingActiveState'=>$row['ParkingActiveState']);
            }
            $array_result = array_merge($employeeDataArray, $parkingDataArray);
            array_push($response, $array_result);
            echo json_encode(array('server_response'=>$response));

        }
    }

    function getEmployeeData($employeeId)
    {
        global $connection, $employeeTable;
        $role = getRoleName($employeeId);
        $employeeData_query = "select id, EmployeeName, EmployeeMobileNumber, EmployeePhoneNumber, EmployeeEmailId, EmployeePassword from ".$employeeTable." where id = $employeeId limit 1";
        $employeeData_result = mysqli_query($connection, $employeeData_query);
        $employeeDataArray = array();
        if(!$employeeData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
            return $employeeDataArray;
        }
        else{
            if(mysqli_num_rows($employeeData_result)==1){
                $row = mysqli_fetch_assoc($employeeData_result);
                $employeeDataArray = array('EmployeeId'=>$row['id'], 'EmployeeName'=>$row['EmployeeName'], 'EmployeeMobileNumber'=>$row['EmployeeMobileNumber'], 'EmployeePhoneNumber'=>$row['EmployeePhoneNumber'],
                        'EmployeeEmailId'=>$row['EmployeeEmailId'], 'EmployeePassword'=>$row['EmployeePassword'], 'EmployeeRole'=>$role);
            }
            return $employeeDataArray;
        }
    }

    function getRoleName($employeeId){
        global $connection, $employeeEntityRoleMappingTable, $employeeRoleTable;

        $getRoleId_query = "select RoleId from ".$employeeEntityRoleMappingTable." where EmployeeId = $employeeId limit 1";
        $getRoleId_result = mysqli_query($connection, $getRoleId_query);
        if(!$getRoleId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($getRoleId_result);
            $roleId = $row['RoleId'];

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
    }

?>