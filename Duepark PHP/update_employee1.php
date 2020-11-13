<?php

    require "init.php";
    //require "init_ftp.php";
    require "create_employeeTable.php";
    require "create_employeeLocationMappingTable.php";
    require "create_employeeEntityRoleMappingTable.php";

    $employeeId = $_POST["EmployeeId"];
    $entity = $_POST["Entity"];

    if(isset($_POST["EmployeeEmailId"])){
        $employeeEmailId = $_POST["EmployeeEmailId"];
        if($employeeEmailId == "null"){
            $employeeEmailId = null;
        }
    }
    else{
        $employeeEmailId = null;
    }

    if(isset($_POST["EmployeeOldPassword"])){
        $employeeOldPassword = $_POST["EmployeeOldPassword"];
        if($employeeOldPassword == "null"){
            $employeeOldPassword = null;
        }
    }
    else{
        $employeeOldPassword = null;
    }

    if(isset($_POST["EmployeeNewPassword"])){
        $employeeNewPassword = $_POST["EmployeeNewPassword"];
        if($employeeNewPassword == "null"){
            $employeeNewPassword = null;
        }
    }
    else{
        $employeeNewPassword = null;
    }

    if(isset($_POST["EmployeeMobileNumber"])){
        $employeeMobileNumber = $_POST["EmployeeMobileNumber"];
        if($employeeMobileNumber == "null"){
            $employeeMobileNumber = null;
        }
    }
    else{
        $employeeMobileNumber = null;
    }
    
    if(isset($_POST["EmployeeVehicleType"])){
        $employeeVehicleType = $_POST["EmployeeVehicleType"];
        if($employeeVehicleType == "null"){
            $employeeVehicleType = null;
        }
    }
    else{
        $employeeVehicleType = null;
    }

    if(isset($_POST["EmployeeAdharNumber"])){
        $employeeAdharNumber = $_POST["EmployeeAdharNumber"];
        if($employeeAdharNumber == "null"){
            $employeeAdharNumber = null;
        }
    }
    else{
        $employeeAdharNumber = null;
    }
    
    if(isset($_POST["EmployeeName"])){
        $employeeName = $_POST["EmployeeName"];
        if($employeeName == "null"){
            $employeeName = null;
        }
    }
    else{
        $employeeName = null;
    }

    $update_query = null;
    if(!empty($employeeEmailId)){
        //$update_query = "update ".$employeeTable." set EmployeeEmailId='$employeeEmailId' where id = $employeeId";
        $isEmailIdExists = false;
        $checkEmailIdExists_query = "select id from ".$employeeTable." where EmployeeEmailId='$employeeEmailId'";
        $checkEmailIdExists_result = mysqli_query($connection, $checkEmailIdExists_query);
        if(!$checkEmailIdExists_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($checkEmailIdExists_result)>0){
                $entityId = getEntityId();
                while($row = mysqli_fetch_assoc($checkEmailIdExists_result)){
                    $dbEmployeeId = $row["id"];
                    $checkEmployeeIdIsOfSameEntity_query = "select EmployeeId from ".$employeeEntityRoleMappingTable." where EmployeeId = $dbEmployeeId and EntityId = $entityId limit 1";
                    $checkEmployeeIdIsOfSameEntity_result = mysqli_query($connection, $checkEmployeeIdIsOfSameEntity_query);
                    if(!$checkEmployeeIdIsOfSameEntity_result){
                        echo "Error retrieving record '".mysqli_error($connection)."'";
                    }
                    else{
                        if(mysqli_num_rows($checkEmployeeIdIsOfSameEntity_result)==1){
                            $isEmailIdExists = true;
                            break;
                        }
                    }
                }
            }
        }
        if(!$isEmailIdExists){
            $update_query = "update ".$employeeTable." set EmployeeEmailId='$employeeEmailId' where id = $employeeId";
        }
        else{
            echo "Email Id already exists...";
        }
    }
    else if(!empty($employeeOldPassword) && !empty($employeeNewPassword)){
        $check_query = "select * from ".$employeeTable." where id = $employeeId and EmployeePassword = '$employeeOldPassword' limit 1";
        $check_result = mysqli_query($connection, $check_query);
        if(!$check_result){
            echo "Error retrieving record '".mysqli_error($connection)."'"; 
        }
        else{
            if(mysqli_num_rows($check_result)==1){
                $update_query = "update ".$employeeTable." set EmployeePassword='$employeeNewPassword' where id = $employeeId";
            }
            else{
                echo "Please enter valid current password...";
            }
        }
    }
    else if(!empty($employeeMobileNumber)){
        $isMobileNumberExists = false;
        $checkMobileNumberExists_query = "select id from ".$employeeTable." where EmployeeMobileNumber='$employeeMobileNumber'";
        $checkMobileNumberExists_result = mysqli_query($connection, $checkMobileNumberExists_query);
        if(!$checkMobileNumberExists_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($checkMobileNumberExists_result)>0){
                $entityId = getEntityId();
                while($row = mysqli_fetch_assoc($checkMobileNumberExists_result)){
                    $dbEmployeeId = $row["id"];
                    $checkEmployeeIdIsOfSameEntity_query = "select EmployeeId from ".$employeeEntityRoleMappingTable." where EmployeeId = $dbEmployeeId and EntityId = $entityId limit 1";
                    $checkEmployeeIdIsOfSameEntity_result = mysqli_query($connection, $checkEmployeeIdIsOfSameEntity_query);
                    if(!$checkEmployeeIdIsOfSameEntity_result){
                        echo "Error retrieving record '".mysqli_error($connection)."'";
                    }
                    else{
                        if(mysqli_num_rows($checkEmployeeIdIsOfSameEntity_result)==1){
                            $isMobileNumberExists = true;
                            break;
                        }
                    }
                }
            }
        }
        if(!$isMobileNumberExists){
            $update_query = "update ".$employeeTable." set EmployeeMobileNumber='$employeeMobileNumber' where id = $employeeId";
        }
        else{
            echo "Mobile Number already exists...";
        }
    }
    else if(!empty($employeeVehicleType)){
        $update_query = "update ".$employeeTable." set VehicleType='$employeeVehicleType' where id = $employeeId";
    }
    else if(!empty($employeeAdharNumber)){
        $isAdharNumberExists = false;
        $checkAdharNumberExists_query = "select id from ".$employeeTable." where EmployeeAdharNumber='$employeeAdharNumber'";
        $checkAdharNumberExists_result = mysqli_query($connection, $checkAdharNumberExists_query);
        if(!$checkAdharNumberExists_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($checkAdharNumberExists_result)>0){
                $entityId = getEntityId();
                while($row = mysqli_fetch_assoc($checkAdharNumberExists_result)){
                    $dbEmployeeId = $row["id"];
                    $checkEmployeeIdIsOfSameEntity_query = "select EmployeeId from ".$employeeEntityRoleMappingTable." where EmployeeId = $dbEmployeeId and EntityId = $entityId limit 1";
                    $checkEmployeeIdIsOfSameEntity_result = mysqli_query($connection, $checkEmployeeIdIsOfSameEntity_query);
                    if(!$checkEmployeeIdIsOfSameEntity_result){
                        echo "Error retrieving record '".mysqli_error($connection)."'";
                    }
                    else{
                        if(mysqli_num_rows($checkEmployeeIdIsOfSameEntity_result)==1){
                            $isAdharNumberExists = true;
                            break;
                        }
                    }
                }
            }
        }
        if(!$isAdharNumberExists){
            $update_query = "update ".$employeeTable." set EmployeeAdharNumber='$employeeAdharNumber' where id = $employeeId";
        }
        else{
            echo "Adhar Number already exists...";
        }
    }
    else if(!empty($employeeName)){
        $update_query = "update ".$employeeTable." set EmployeeName='$employeeName' where id = $employeeId";
    }

    if($update_query != null){
        $update_result = mysqli_query($connection, $update_query);
        if(!$update_result){
            echo "Error updating record '".mysqli_error($connection)."'";
        }
        else{
            // if($employeeProfilePic != "null"){
            //     addProfilePic();
            // }
            // if($locationId != "null" && $locationId != null){
            //     updateEmployeeLocationMappingData();
            // }
            echo "UpdateSuccessfully";
        }
    }

    function getEntityId(){
        global $connection, $entityTable, $entity;
        $getEntityId_query = "select id from ".$entityTable." where Entity = '$entity' limit 1";
        $getEntityId_result = mysqli_query($connection, $getEntityId_query);
        if(!$getEntityId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getEntityId_result)==1){
                $row = mysqli_fetch_assoc($getEntityId_result);
                return $row["id"];
            }
        }
    }
?>