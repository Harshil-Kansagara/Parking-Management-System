<?php
    require "init.php";

    $employeeTable = "employee";

    $sql_query = "SHOW TABLES LIKE '".$employeeTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createEmployeeTable();
    }

    function createEmployeeTable(){
        global $employeeTable, $connection;
        $sql_query = "create table ".$employeeTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            EmployeeName varchar(255) NOT NULL,
            EmployeeSate varchar(255) NULL,
            EmployeeCity varchar(255) NULL,
            EmployeeMobileNumber varchar(255) NOT NULL,
            EmployeePhoneNumber varchar(255) NULL,
            EmployeeEmailId varchar(255) NULL,
            EmployeeAdharNumber varchar(255) NULL,
            EmployeePassword varchar(255) NOT NULL,
            EmployeeProfilePic varchar(255) NULL,
            VehicleType varchar(255) NULL,
            IsEmployeeNewPasswordCreated boolean NOT NULL,
            EmployeeActiveState boolean NULL
        )";

        if(!mysqli_query($connection,$sql_query)){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }
?>