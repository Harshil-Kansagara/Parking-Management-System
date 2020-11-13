<?php

    require "init.php";
    //require "create_employeeTable.php";
    require "create_employeeAttendanceTable.php";
    require "create_notificationTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_adminAppEmployeeTokenTable.php";
    require "create_parkingTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_employeeLocationMappingTable.php";

    $employeePassword = $_POST["EmployeePassword"];
    $employeeEntity = $_POST["EmployeeEntity"];
  
    if(isset($_POST["EmployeeEmailId"]))
    {
        $employeeEmailId = $_POST["EmployeeEmailId"];
    }
    else{
        $employeeEmailId = null;
    }

    if(isset($_POST["EmployeeMobileNumber"]))
    {
        $employeeMobileNumber = $_POST["EmployeeMobileNumber"];
    }
    else{
        $employeeMobileNumber = null;
    }

    $entityId = getEntityId();
    $employeeId = 0;
    
    if($entityId != 0)
    {
        loginEmployee();
    }
    else
    {
        echo "NoEntityFound";    
    }

    function getEntityId(){
        global $connection, $entityTable, $employeeEntity;
        $entityId_query = "select id from ".$entityTable." where Entity = '$employeeEntity'"; 
        $entityId_result = mysqli_query($connection,$entityId_query);
        if(!$entityId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
            //return 0;
        } 
        else
        {
            $row = mysqli_fetch_assoc($entityId_result);
            return $row['id'];
        }
    }

    function loginEmployee()
    {
        global $connection, $employeeTable, $employeeMobileNumber, $employeeEmailId, $employeePassword;
        if($employeeEmailId != null){
            $login_query = "select id from ".$employeeTable." where EmployeeEmailId = '$employeeEmailId' and EmployeePassword = '$employeePassword'";
        }
        else if ($employeeMobileNumber != null){
            $login_query = "select id from ".$employeeTable." where EmployeeMobileNumber = '$employeeMobileNumber' and EmployeePassword = '$employeePassword' and EmployeeActiveState = true ";
        }
        $login_result = mysqli_query($connection,$login_query);
        if(!$login_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
            //echo "0";
        } 
        else
        {
            if(mysqli_num_rows($login_result) > 0)
            {
                while($row = mysqli_fetch_assoc($login_result)){
                    getGeneratedEmployeeId($row['id']);
                }
            }
            // else if(mysqli_num_rows($login_result) == 1)
            // {
            //     $row = mysqli_fetch_assoc($login_result);
            //     getGeneratedEmployeeId($row['id']);
            // }
            else{
                echo "0";
            }
        }
    }

    function getGeneratedEmployeeId($employeeId){
        global $connection, $employeeEntityRoleMappingTable, $entityId;
        $generatedEmployeeId_query = "select EmployeeId, RoleId, GeneratedEmployeeId from ".$employeeEntityRoleMappingTable." where EntityId = $entityId and EmployeeId = $employeeId";
        $generatedEmployeeId_result = mysqli_query($connection, $generatedEmployeeId_query);

        if(!$generatedEmployeeId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
            //echo "0";
        }
        else{
            if($generatedEmployeeId_result->num_rows == 1)
            {
                $row = mysqli_fetch_assoc($generatedEmployeeId_result);
                getEmployeeData($row['EmployeeId'], $row['RoleId'], $row['GeneratedEmployeeId']);
            }
        }
    }

    function getEmployeeData($employeeId, $roleId, $generatedEmployeeId)
    {
        if(is_numeric($employeeId) && is_numeric($roleId) && is_numeric($generatedEmployeeId)){
            $response = array();
            $parkingDataArray = array();
            global $connection, $employeeTable, $employeeEntity;
            $role = getRoleName($roleId);
            if($employeeEntity == "ConsumerApp"){
                $locationIdArray = getLocationId($employeeId);
                $isActivated = getIsActivatedStatus($employeeId);
                $parkingDataArray = getParkingData($employeeId);
                addEmployeeAttendanceData($employeeId);
            }
            else{
                $isActivated = "notificationNotNeeded";
            }
            $getEmployee_query  = "select id, EmployeeName, EmployeeProfilePic, VehicleType, IsEmployeeNewPasswordCreated from ".$employeeTable." where id = $employeeId";
            $getEmployee_result = mysqli_query($connection, $getEmployee_query);
            if(!$getEmployee_result){
                echo "Error retrieving record '".mysqli_error($connection)."'";
                //echo "0";
            }
            else{
                $row = mysqli_fetch_assoc($getEmployee_result);
                if(empty($row["VehicleType"])){
                    $vehicleType="null";
                }
                else{
                    $vehicleType = $row["VehicleType"];
                }
                $employeeDataArray = array('id'=>$row['id'], 'EmployeeName'=>$row['EmployeeName'], 'Role'=>$role, 'IsActivated'=>$isActivated, 
                            'EmployeeProfilePic'=>$row['EmployeeProfilePic'], 'IsEmployeeNewPasswordCreated'=>$row['IsEmployeeNewPasswordCreated'],
                            'VehicleType'=>$vehicleType, 'GeneratedEmployeeId'=>$generatedEmployeeId);
                if($employeeEntity == "ConsumerApp"){
                    $array_result = array_merge($employeeDataArray, $parkingDataArray, $locationIdArray);
                    array_push($response, $array_result);
                }
                else{
                    array_push($response, $employeeDataArray);
                }
                echo json_encode(array('server_response'=>$response));
            }
        }
    }

    function getLocationId($employeeId){
        global $connection, $employeeLocationMappingTable;
        $locationId_query = "select LocationId from ".$employeeLocationMappingTable." where EmployeeId = $employeeId limit 1";
        $locationId_result = mysqli_query($connection, $locationId_query);
        if(!$locationId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($locationId_result) == 1){
                $row = mysqli_fetch_assoc($locationId_result);
                $locationIdArray = array('LocationId'=>$row['LocationId']);
                
            }
            else{
                $locationIdArray = array('LocationId'=> "0");
            }
            return $locationIdArray;
        }
    }

/*    function getEmployeeData($employeeId, $roleId, $generatedEmployeeId)
    {
        if(is_numeric($employeeId) && is_numeric($roleId) && is_numeric($generatedEmployeeId)){
            $response = array();
            global $connection, $employeeTable, $employeeEntity;
            $role = getRoleName($roleId);
            $parkingDataArray = getParkingData($employeeId);
            if($employeeEntity == "ConsumerApp"){
                $isActivated = getIsActivatedStatus($employeeId);
            }
            else{
                $isActivated = "notificationNotNeeded";
            }
            $getEmployee_query  = "select id, EmployeeName, EmployeeProfilePic, IsEmployeeNewPasswordCreated from ".$employeeTable." where id = $employeeId";
            $getEmployee_result = mysqli_query($connection, $getEmployee_query);
            if(!$getEmployee_result){
                //echo "Error retrieving record '".mysqli_error($connection)."'";
                echo "0";
            }
            else{
                $row = mysqli_fetch_assoc($getEmployee_result);
                $employeeDataArray = array('id'=>$row['id'], 'EmployeeName'=>$row['EmployeeName'], 'Role'=>$role, 'IsActivated'=>$isActivated, 
                            'EmployeeProfilePic'=>$row['EmployeeProfilePic'], 'IsEmployeeNewPasswordCreated'=>$row['IsEmployeeNewPasswordCreated'],
                            'GeneratedEmployeeId'=>$generatedEmployeeId);
                
                $array_result = array_merge($employeeDataArray, $parkingDataArray);
                array_push($response, $array_result);
                echo json_encode(array('server_response'=>$response));
    
                if($employeeEntity == "AdminApp"){
                    getEmployeeRegisterToken_FCM();
                }
            }
        }
    }*/

    function getRoleName($roleId){
        global $connection, $employeeRoleTable;
        $role_query = "select Role from ".$employeeRoleTable." where id = $roleId";
        $role_result = mysqli_query($connection, $role_query);
        if(!$role_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
            //return 0;
        }
        else{
            $row = mysqli_fetch_assoc($role_result);
            return $row['Role'];
        }
    }

    function getIsActivatedStatus($employeeId){
        global $connection, $consumerNotificationTable;
        $notification_query = "select IsActivated from ".$consumerNotificationTable." where EmployeeId = $employeeId";
        $notification_result = mysqli_query($connection, $notification_query);
        if(!$notification_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
            //return 0;
        }
        else{
            if(mysqli_num_rows($notification_result) == 1){
                $row = mysqli_fetch_assoc($notification_result);
                return $row['IsActivated'];
            }
            else{
                return null;
            }
        }
    }

    function getParkingData($employeeId){
        global $parkingTable, $connection, $parkingEmployeeMappingTable;
        $getParkingId_query = "select ParkingId from ".$parkingEmployeeMappingTable." where ConsumerAppEmployeeId = $employeeId limit 1";
        $getParkingId_result = mysqli_query($connection, $getParkingId_query);
        if(!$getParkingId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($getParkingId_result);
            $parkingId = $row['ParkingId'];
            if(!empty($parkingId)){
                $parkingData_query = "select id, ParkingName from ".$parkingTable." where id = $parkingId limit 1";
                $parkingData_result = mysqli_query($connection, $parkingData_query);
                if(!$parkingData_result){
                    echo "Error retrieving record '".mysqli_error($connection)."'";
                }
                else{
                    if(mysqli_num_rows($parkingData_result)==1){
                        $row = mysqli_fetch_assoc($parkingData_result);
                        $parkingDataArray = array('ParkingId'=>$row['id'], 'ParkingName'=>$row['ParkingName']);
                        return $parkingDataArray;
                    }
                }
            }else{
                $parkingDataArray = array("ParkingId"=>"0", "ParkingName"=>"null");
                return $parkingDataArray;
            }
        }
    }

    function addEmployeeAttendanceData($employeeId){
        date_default_timezone_set('Asia/Kolkata');
        $loggedInDate = date("d-m-Y");
        $loggedInTime = date("H:i A");
        global $connection, $employeeAttendanceTable;
        $addAttendanceData_query = "insert into ".$employeeAttendanceTable." (EmployeeId, LoggedInDate, LoggedInTime) values 
        ($employeeId, '$loggedInDate', '$loggedInTime')";
        $addAttendanceData_result = mysqli_query($connection, $addAttendanceData_query);
        if(!$addAttendanceData_result){
            echo "Error creating record '".mysqli_error($connection)."'";
        }
    }
?>