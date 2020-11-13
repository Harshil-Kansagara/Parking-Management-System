<?php

    require "init.php";
    require "create_employeeAttendanceTable.php";

    $employeeId = $_GET["EmployeeId"];

    if(!empty($employeeId)){

        date_default_timezone_set('Asia/Kolkata');
        $loggedOutDate = date("d-m-Y");
        $loggedOutTime = date("H:i A");

        $addLoggedOutData_query = "update ".$employeeAttendanceTable." set LoggedOutDate = '$loggedOutDate', LoggedOutTime = '$loggedOutTime'
            where EmployeeId = $employeeId";
        $addLoggedOutData_result = mysqli_query($connection, $addLoggedOutData_query);

        if(!$addLoggedOutData_result){
            echo "Error updating record '".mysqli_error($connection)."'";
        }
        else{
            echo "LogoutSuccessfully";
        }
    }

?>