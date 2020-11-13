<?php

    require "init.php";
    require "create_employeeTable.php";

    $employeeId = $_POST["EmployeeId"];
    $employeeActiveState = $_POST["EmployeeActiveState"];

    if(!empty($employeeId) && !empty($employeeActiveState)){
        $updateEmployeeActiveState_query = "update ".$employeeTable." set EmployeeActiveState = $employeeActiveState where id = $employeeId";
        $updateEmployeeActiveState_result = mysqli_query($connection, $updateEmployeeActiveState_query);

        if($updateEmployeeActiveState_result){
            if($employeeActiveState == "false"){
                echo "Deactivate";
            }
            else{
                echo "Activate";
            }
        }
        else{
            echo "Error updating record '".mysqli_error($connection)."'";
        }
    }
?>