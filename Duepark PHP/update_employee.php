<?php

    require "init.php";
    require "init_ftp.php";
    require "create_employeeTable.php";
    require "create_employeeLocationMappingTable.php";

    $employeeId = $_POST["EmployeeId"];
    $employeeMobileNumber = $_POST["EmployeeMobileNumber"];
    $employeeEmailId = $_POST["EmployeeEmailId"];
    $employeePassword = $_POST["EmployeePassword"];
    $employeeProfilePic = $_POST["EmployeeProfilePic"];
    $employeeAdharNumber = $_POST["EmployeeAdharNumber"];
    
    if(isset($_POST["LocationId"])){
        $locationId = $_POST["LocationId"];
    }
    else{
        $locationId = null;
    }

    if(isset($_POST["VehicleType"])){
        $vehicleType = $_POST["VehicleType"];
    }
    else{
        $vehicleType = null;
    }

    if($employeeProfilePic != "null"){
        $adharNumber = str_replace(' ','',$employeeAdharNumber);
    }
    else{
        $adharNumber = null;
    }

    $updateEmployee_query = "update ".$employeeTable." set EmployeeMobileNumber='$employeeMobileNumber', EmployeeEmailId='$employeeEmailId', 
                            EmployeePassword='$employeePassword', EmployeeProfilePic = '$adharNumber', VehicleType='$vehicleType' where id = $employeeId";
    $updateEmployee_result = mysqli_query($connection, $updateEmployee_query);
    if(!$updateEmployee_result){
        echo "Error updating record '".mysqli_error($connection)."'";
    }
    else{
        if($employeeProfilePic != "null"){
            addProfilePic();
        }
        if($locationId != "null" && $locationId != null){
            updateEmployeeLocationMappingData();
        }
        echo "UpdateSuccessfully";
    }

    function addProfilePic(){
        global $ftp_conn, $adharNumber, $employeeProfilePic;
        $ImagePath = "profilePic/".$adharNumber.".png";
        if(ftp_chdir($ftp_conn, "duepark/"))
        {
            if(!is_dir("profilePic"))
            {
                if(ftp_mkdir($ftp_conn, "profilePic"))
                {
                    $profilePic = base64_decode($employeeProfilePic);
                    file_put_contents($ImagePath,$profilePic);
                }
            }
            else
            {
                $profilePic = base64_decode($employeeProfilePic);
                file_put_contents($ImagePath,$profilePic);
            }
        }
        ftp_close($ftp_conn);
    }

    function updateEmployeeLocationMappingData(){
        global $connection, $locationId, $employeeLocationMappingTable, $employeeId;
        $getLocation_query = "select * from ".$employeeLocationMappingTable." where EmployeeId = $employeeId limit 1";
        $getLocation_result = mysqli_query($connection, $getLocation_query);
        if(!$getLocation_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getLocation_result) == 1){
                $updateLocation_query = "update ".$employeeLocationMappingTable." set LocationId = $locationId where EmployeeId = $employeeId";
                $updateLocation_result = mysqli_query($connection, $updateLocation_query);
                if(!$updateLocation_result){
                    echo "Error updating record '".mysqli_error($connection)."'";
                }
            }
            else{
                $addLocation_query = "insert into ".$employeeLocationMappingTable." (EmployeeId, LocationId) values ($employeeId, $locationId)";
                $addLocation_result = mysqli_query($connection, $addLocation_query);
                if(!$addLocation_result){
                    echo "Error creating new record '".mysqli_error($connection)."'";
                }
            }
        }
    }

?>