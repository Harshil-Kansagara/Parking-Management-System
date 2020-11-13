<?php

    require "init.php";
    require "create_employeeTable.php";
    require "create_notificationTable.php";

    $response = array();
    $allNotification_query = "select EmployeeId from ".$consumerNotificationTable." where IsActivated = 0";
    $allNotification_result = mysqli_query($connection, $allNotification_query);
    if(!$allNotification_result){
        echo "Error retrieving record '".mysqli_error($connection)."'";
    }
    else{
        while($row = mysqli_fetch_assoc($allNotification_result))
        {
            getNewParkingEmployeeData($row['EmployeeId']);
        }
        echo json_encode(array('server_response'=>$response));
    }

    function getNewParkingEmployeeData($employeeId){
        global $connection, $employeeTable, $response;
        $employee_query = "select id, EmployeeName, EmployeeMobileNumber from ".$employeeTable." where id = $employeeId";
        $employee_result = mysqli_query($connection, $employee_query);
        if(!$employee_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($employee_result);
            array_push($response, array('id'=>$row['id'], 'EmployeeName'=>$row['EmployeeName'], 'EmployeeMobileNumber'=>$row['EmployeeMobileNumber']));
        }
    }

?>