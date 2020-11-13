<?php

    require "init.php";
    require "create_parkingTable.php";
    require "create_employeeTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_employeeEntityRoleMappingTable.php";

    $parkingId = $_POST["ParkingId"];
    $parkingName = $_POST["ParkingName"];
    $parkingType = $_POST["ParkingType"];
    $employeeEmailId = $_POST["EmployeeEmailId"];
    $employeeMobileNumber = $_POST["EmployeeMobileNumber"];
    
    if(isset($_POST["EmployeeId"])){
        $employeeId = $_POST["EmployeeId"];
    }
    else
    {
        $employeeId = null;
    }

    if(isset($_POST["EmployeePassword"])){
        $employeePassword = $_POST["EmployeePassword"];
    }
    else{
        $employeePassword = null;
    }

    if(isset($_POST["EmployeeName"])){
        $employeeName = $_POST["EmployeeName"];
    }
    else{
        $employeeName = null;
    }

    if(isset($_POST["ParkingAbbreviation"])){
        $parkingAbbreviation = $_POST["ParkingAbbreviation"];
    }
    else
    {
        $parkingAbbreviation = null;
    }

    if(isset($_POST["ParkingAddress"])){
        $parkingAddress = $_POST["ParkingAddress"];
    }
    else{
        $parkingAddress = null;
    }

    if(isset($_POST["ParkingCity"])){
        $parkingCity = $_POST["ParkingCity"];
    }
    else{
        $parkingCity = null;
    }

    if(isset($_POST["EmployeePhoneNumber"])){
        $employeePhoneNumber = $_POST["EmployeePhoneNumber"];
    }
    else{
        $employeePhoneNumber = null;
    }


    if($employeeId == null){
        $employeeId = getEmployeeIdByParkingId();
    }

    updateParkingData();
    function updateParkingData()
    {
        global $connection, $parkingTable, $parkingId, $parkingName, $parkingType, $parkingAbbreviation, $parkingAddress, $parkingCity;
        if($parkingAbbreviation != null && $parkingAddress != null && $parkingCity != null){
            $updateParking_query = "update ".$parkingTable." set ParkingAcronym = '$parkingAbbreviation', ParkingName = '$parkingName', ParkingType = '$parkingType', 
                    ParkingAddress = '$parkingAddress', ParkingCity='$parkingCity' where id = $parkingId";
        } 
        else{
            $updateParking_query = "update ".$parkingTable." set ParkingName = '$parkingName', ParkingType = '$parkingType' where id = $parkingId";
        }
        $updateParking_result = mysqli_query($connection, $updateParking_query);
        if(!$updateParking_result){
            echo "Error updating record '".mysqli_error($connection)."'";
        }
        else{
            updateEmployeeData();
        }
    }

    function updateEmployeeData(){
        global $connection, $employeeTable, $employeeId, $employeeEmailId, $employeeMobileNumber, $employeeName, $employeePassword, $employeePhoneNumber;
        if($employeeName == null && $employeePhoneNumber == null){
            $updateEmployee_query = "update ".$employeeTable." set EmployeeEmailId = '$employeeEmailId', EmployeeMobileNumber = '$employeeMobileNumber', 
                EmployeePassword = '$employeePassword' where id = $employeeId";
        }
        else
        {
            $updateEmployee_query = "update ".$employeeTable." set EmployeeEmailId = '$employeeEmailId', EmployeeMobileNumber = '$employeeMobileNumber', 
                EmployeePhoneNumber = '$employeePhoneNumber', EmployeeName = '$employeeName' where id = $employeeId";
        }
        $updateEmployee_result = mysqli_query($connection, $updateEmployee_query);
        if(!$updateEmployee_result){
            echo "Error updating record '".mysqli_error($connection)."'";
        }
        else{
            echo "UpdateSuccessfully";
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
            return $row['ConsumerAppEmployeeId'];
        }
    }


?>