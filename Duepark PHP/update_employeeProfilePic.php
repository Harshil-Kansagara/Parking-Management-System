<?php

    require "init.php";
    require "init_ftp.php";
    require "create_employeeTable.php";

    $employeeId = $_POST["EmployeeId"];
    $employeeProfilePic = $_POST["EmployeeProfilePic"];

    if(!empty($employeeId)){
        $getEmployeeData_query = "select EmployeeAdharNumber, EmployeeProfilePic from ".$employeeTable." where id = $employeeId limit 1";
        $getEmployeeData_result = mysqli_query($connection, $getEmployeeData_query);

        if(!$getEmployeeData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getEmployeeData_result)==1){
                $row = mysqli_fetch_assoc($getEmployeeData_result);
                $employeeAdharNumber = $row["EmployeeAdharNumber"];
                addProfilePic($employeeAdharNumber);
            }
        }
    }

    function addEmployeeProfilePic($employeeAdharNumber){
        global $connection, $ftp_conn, $employeeId, $employeeTable, $employeeProfilePic;
        $adharNumber = str_replace(' ','',$employeeAdharNumber);
        $updateEmployeeProfilePic_query = "update ".$employeeTable." set EmployeeProfilePic='$adharNumber' where id = $employeeId";
        $updateEmployeeProfilePic_result = mysqli_query($connection, $updateEmployeeProfilePic_query);
        if(!$updateEmployeeProfilePic_result){
            echo "Error updating record '".mysqli_error($connection)."'";
        }
        else{
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
    }

?>