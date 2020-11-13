<?php
    require "init.php";
    require "create_employeeTable.php";

    
    $newPassword = $_POST["newPassword"];

    if(isset($_POST["EmployeeId"])){
        $employeeId = $_POST["EmployeeId"];
    }
    else{
        $employeeId = null;
    }

    if(isset($_POST["MobileNumber"])){
        $mobileNumber = $_POST["MobileNumber"];
    }
    else{
        $mobileNumber = null;
    }

    if(is_null($mobileNumber)){
        $sql_query = "update ".$employeeTable." set EmployeePassword='$newPassword', IsEmployeeNewPasswordCreated=true where id=".$employeeId;
    
        $result = mysqli_query($connection,$sql_query);

        if($result){
            echo "1";
        }
        else{
            echo "0";
        }
    }
    else{
        $sql_query = "update ".$employeeTable." set EmployeePassword='$newPassword' where EmployeeMobileNumber='$mobileNumber'";
    
        $result = mysqli_query($connection,$sql_query);

        if($result){
            echo "1";
        }
        else{
            echo "0";
        }
    }

?>