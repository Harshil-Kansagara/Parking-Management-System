<?php

    require "init.php";
    require "create_employeeTable.php";

    $employeeAttendanceTable = "employeeattendance";

    $sql_query = "SHOW TABLES LIKE '".$employeeAttendanceTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createEmployeeAttendanceTable();
    }

    function createEmployeeAttendanceTable(){
        global $employeeAttendanceTable, $connection;
        $sql_query = "create table ".$employeeAttendanceTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            EmployeeId int(200) NOT NULL,
            LoggedInDate varchar(255) NOT NULL,
            LoggedInTime varchar (255) NOT NULL,
            LoggedOutDate varchar(255) NULL,
            LoggedOutTime varchar (255) NULL,
            FOREIGN KEY (EmployeeId) references employee(id)
        )";

        if(!mysqli_query($connection,$sql_query)){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }
?>