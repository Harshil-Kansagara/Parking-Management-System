<?php

    require "init.php";
    require "create_employeeTable.php";
    require "create_parkingTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_notificationTable.php";

    // Employee Detail
    $employeeId = $_POST["employeeId"];
    $employeeName = $_POST["employeeName"];
    $employeeMobileNumber = $_POST["employeeMobileNumber"];
    $employeePhoneNumber = $_POST["employeePhoneNumber"];
    $employeeEmailId = $_POST["employeeEmailId"];

    // Parking Detail
    $parkingAcronym = $_POST["parkingAcronym"];
    $parkingName = $_POST["parkingName"];
    $parkingType = $_POST["parkingType"];
    $parkingAddress = $_POST["parkingAddress"];
    $parkingCity = $_POST["parkingCity"];
    $parkingDate = $_POST["parkingDate"];
    $parkingTime = $_POST["parkingTime"];
    $parkingActivationState = $_POST["parkingActivationState"];

    $entityId = getEntityId();
    //$newEmployeeId = 0;

    if($employeePhoneNumber == "null"){
        $employeePhoneNumber = null;
    }

    // If employee id is null than create new employee first
    if($employeeId == "null")
    {
        $employeeId = 0;
        if($entityId != 0)
        {
            fetchAllEmployeeFromEmployeeEntityRoleMappingTable();
        }
        else
        {
            echo "NoEntityFound";    
        }
    }
    // Else update employee value first
    else{
        updateEmployee();
        updateConsumerNotification();
    }

    // Get Entity Id
    function getEntityId(){
        global $connection, $entityTable, $employeeEntity;
        $entityId_query = "select id from ".$entityTable." where Entity = 'ConsumerApp'"; 
        $entityId_result = mysqli_query($connection,$entityId_query);
        if(!$entityId_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
            return 0;
        } 
        else
        {
            $row = mysqli_fetch_assoc($entityId_result);
            return $row['id'];
        }
    }

    // Fetch All employee from employee Entity Role Mapping table as per given entity Id
    function fetchAllEmployeeFromEmployeeEntityRoleMappingTable(){
        global $connection, $employeeEntityRoleMappingTable, $entityId, $employeeMobileNumber, $employeeEmailId, $employeeTable;

        $employeeList_query = "select EmployeeId from ".$employeeEntityRoleMappingTable." where EntityId = $entityId";
        $employeeList_result = mysqli_query($connection, $employeeList_query);
        
        if(!$employeeList_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else
        {
            if($employeeList_result->num_rows > 0)
            {                
                $isExist = false;
                $fetchEmployeeId = 0;
                while($row = mysqli_fetch_assoc($employeeList_result)){
                    $fetchEmployeeId = $row['EmployeeId'];
                    //$employee_query = "select EmployeeMobileNumber, EmployeeEmailId from ".$employeeTable." where id = $fetchEmployeeId";
                    $employee_query = "select * from ".$employeeTable." where id = $fetchEmployeeId and EmployeeMobileNumber = $employeeMobileNumber and EmployeeEmailId = '$employeeEmailId' limit 1";
                    $employee_result = mysqli_query($connection, $employee_query);

                    if(!$employee_result){
                        echo "Error creating new record '".mysqli_error($connection)."'";
                    }
                    else
                    {
                        if(mysqli_num_rows($employee_result) == 1){
                            $isExist = true;
                            break;
                        }
                        //$employee_row = mysqli_fetch_assoc($employee_result);
                        // if($employeeMobileNumber == $employee_row['EmployeeMobileNumber']){
                        //     $isExist = true;
                        //     break;
                        // }
                        // if($employeeEmailId != null && $employee_row['EmployeeEmailId'] != null)
                        // {
                        //     if($employeeEmailId == $employee_row['EmployeeEmailId']){
                        //         $isExist = true;
                        //         break;
                        //     }
                        // }

                    }
                }
                if($isExist){
                    echo "exist";
                }
                else{
                    addEmployee();
                }
            }
            else{
                addEmployee();
            }
        }
    }    

    // Add employee
    function addEmployee(){
        global $connection, $employeeTable, $employeeId, $employeeName, $employeeMobileNumber, $employeePhoneNumber, $employeeEmailId;
        
        $addEmployee_query = "insert into ".$employeeTable."(EmployeeName, EmployeeSate, EmployeeCity, EmployeeMobileNumber, EmployeePhoneNumber, 
        EmployeeEmailId, EmployeeAdharNumber, EmployeePassword, EmployeeProfilePic, IsEmployeeNewPasswordCreated, EmployeeActiveState) values ('$employeeName', 
            null, null, '$employeeMobileNumber', '$employeePhoneNumber', '$employeeEmailId', null, 'indianDuepark', null, false, true)";

        $addEmployee_result = mysqli_query($connection,$addEmployee_query);
        if(!$addEmployee_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else{
            $employeeId = mysqli_insert_id($connection);
            
            addEmployeeEntityRoleMapping();
        }
    }

    // Add Employee Entity Role Relationship Mapping
    function addEmployeeEntityRoleMapping(){
        global $connection, $employeeEntityRoleMappingTable, $employeeId, $entityId, $employeeRole, $employeeEntity;
        $roleId = getRoleId();

        $fetchLastGeneratedEmployeeIdLList_query = "select GeneratedEmployeeId from ".$employeeEntityRoleMappingTable." where EntityId = $entityId and RoleId = $roleId order by id desc limit 1";
        $fetchLastGeneratedEmployeeIdList_result = mysqli_query($connection, $fetchLastGeneratedEmployeeIdLList_query);
        if(!$fetchLastGeneratedEmployeeIdList_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else{
            $totalRow = $fetchLastGeneratedEmployeeIdList_result->num_rows;
            $newGeneratedEmployeeId = 1;
            if($totalRow == 1)
            {
                $row =  mysqli_fetch_assoc($fetchLastGeneratedEmployeeIdList_result);
                $newGeneratedEmployeeId = $row["GeneratedEmployeeId"];
                $newGeneratedEmployeeId = $newGeneratedEmployeeId + 1;
            }
            
            $add_employeeEntityRoleMapping_query = "insert into ".$employeeEntityRoleMappingTable." (EmployeeId, EntityId, RoleId, 
            GeneratedEmployeeId) values ($employeeId, $entityId, $roleId, $newGeneratedEmployeeId)";

            if(!mysqli_query($connection, $add_employeeEntityRoleMapping_query)){
                echo "Error creating new record '".mysqli_error($connection)."'";
            }
            else{
                getLastParkingIdAsPerAcronym();
            }
        }
    }

    // Get Role Id
    function getRoleId()
    {
        global $connection, $employeeRoleTable, $employeeRole;

        $roleId_query = "select id from ".$employeeRoleTable." where Role = 'SuperAdmin'";
        $roleId_result = mysqli_query($connection,$roleId_query);
        if(!$roleId_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
            return;
        } 
        else
        {
            $row = mysqli_fetch_assoc($roleId_result);
            return $row['id'];
        }        
    }

    // Update new Employee when Employee Id comes
    function updateEmployee(){
        global $connection, $employeeTable, $employeeId, $employeePhoneNumber, $employeeEmailId;

        $updateEmployee_query = "update ".$employeeTable." set EmployeePhoneNumber='$employeePhoneNumber', EmployeeEmailId='$employeeEmailId' where id=".$employeeId;   
        $updateEmployee_result = mysqli_query($connection,$updateEmployee_query);

        if(!$updateEmployee_result){
            echo "Error updating record '".mysqli_error($connection)."'";
        }
        else{
            getLastParkingIdAsPerAcronym();
        }
    }

    // Get last ParkingId as per Acronym
    function getLastParkingIdAsPerAcronym(){
        global $connection, $parkingTable, $parkingAcronym;
        $sql_query = "select ParkingId from ".$parkingTable." where ParkingAcronym ='".$parkingAcronym."' order by id desc limit 1";

        $result = mysqli_query($connection,$sql_query);
        if($result->num_rows == 0){
            addParking(1);
        }else{
            $row = mysqli_fetch_assoc($result);
            $parkingId = $row["ParkingId"];
            $parkingId = $parkingId + 1;
            addParking($parkingId);
        }
    }

    // Add Parking
    function addParking($parkingId){
        global $connection, $parkingTable, $parkingAcronym, $parkingName, $parkingType, $parkingAddress, $parkingCity, $parkingDate, $parkingTime,
                $parkingActivationState;

        $addParking_query = "insert into ".$parkingTable." (ParkingId, ParkingAcronym, ParkingName, ParkingType, ParkingAddress, ParkingCity,
                ParkingDate, ParkingTime, ParkingActiveState) values ($parkingId, '$parkingAcronym', '$parkingName', '$parkingType', '$parkingAddress', 
                '$parkingCity', '$parkingDate', '$parkingTime', $parkingActivationState)";
        $addParking_result = mysqli_query($connection, $addParking_query);
        if(!$addParking_result){
            //echo "Error creating record '".mysqli_error($connection)."'";
            echo "0";
        }
        else{
            $last_parkingId = mysqli_insert_id($connection);
            addParkingEmployeeMappingData($last_parkingId);
        }    
    }

    // Add Parking Employee Relationship
    function addParkingEmployeeMappingData($parkingId){
        global $connection, $employeeId, $parkingEmployeeMappingTable;
        $addParkingEmployeeMapping_query = "insert into ".$parkingEmployeeMappingTable." (ParkingId, ConsumerAppEmployeeId, AdminAppEmployeeId) 
                values ($parkingId, $employeeId, null)";
        $addParkingEmployeeMapping_result = mysqli_query($connection, $addParkingEmployeeMapping_query);
        if(!$addParkingEmployeeMapping_result){
            echo "Error creating record '".mysqli_error($connection)."'";
            //echo "0";
        }
        else{
            echo $parkingId;
        }
    }

    // Update the state from activation left to activated
    function updateConsumerNotification()
    {
        global $connection, $employeeId, $consumerNotificationTable;
        $updateConsumerNotification_query = "update ".$consumerNotificationTable." set IsActivated=true where EmployeeId=".$employeeId;
        if(!mysqli_query($connection, $updateConsumerNotification_query))
        {
            echo "Error updating record '".mysqli_error($connection)."'";
        }
    }
?>