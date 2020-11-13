<?php

    require "init.php";
    require "create_employeeLocationMappingTable.php";

    $locationId = $_POST["LocationId"];
    $employeeIds = $_POST["EmployeeIds"];
    $employeeIdList = explode(",",$employeeIds);

    $i=0;
    $count = count($employeeIdList);
    $isUpdated = true;

    while($i<$count){
        $delete_query = "delete from ".$employeeLocationMappingTable." where LocationId = $locationId and EmployeeId = $employeeIdList[$i]";
        $delete_result = mysqli_query($connection, $delete_query);
        if(!$delete_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
            $isUpdated = false;
        }
        $i = $i + 1;
    }

    if($isUpdated){
        echo "updated";
    }
    else{
        echo "notupdated";
    }

?>