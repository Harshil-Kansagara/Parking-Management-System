<?php

    require "init.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_employeeEntityRoleMappingTable.php";

    $parkingId = $_POST["ParkingId"];
    $managerIds = $_POST["ManagerIds"];
    $role = $_POST["Role"];

    $roleId = getRoleId($role);
    if($managerIds != "null"){
        $managerIdList = explode(",",$managerIds);
    }
    else {
        $managerIdList = [];
    }

    $i=0;
    $count = count($managerIdList);

    $getAllAdminAppEmployeeId_query = "select AdminAppEmployeeId from ".$parkingEmployeeMappingTable." where ParkingId=".$parkingId;
    $getAllAdminAppEmployeeId_result = mysqli_query($connection, $getAllAdminAppEmployeeId_query);
    
    if(!$getAllAdminAppEmployeeId_result){
        echo "Error retrieving record '" . mysqli_error($connection) . "'";
    }
    else{
        if(mysqli_num_rows($getAllAdminAppEmployeeId_result)>0)
        {
            if($count>0)
            {
                //For adding Data;
                while($i<$count)
                {
                    $checked = true;
                    while($row = mysqli_fetch_array($getAllAdminAppEmployeeId_result))
                    {
                        if(!empty($row["AdminAppEmployeeId"])){
                            if($managerIdList[$i] == $row['AdminAppEmployeeId'])
                            {
                                $checked = false;
                                break;
                            }
                        }
                    }
                    mysqli_data_seek($getAllAdminAppEmployeeId_result, 0);
                    if($checked)
                    {
                        addManagerPartnerData($managerIdList[$i]);
                    }
                    $i++;
                }
    
                //For deleting data;
                mysqli_data_seek($getAllAdminAppEmployeeId_result, 0);
                while($row = mysqli_fetch_array($getAllAdminAppEmployeeId_result))
                {
                    $i=0;
                    $checked = true;
                    while($i<$count)
                    {
                        if(!empty($row["AdminAppEmployeeId"])){
                            if($managerIdList[$i] == $row['AdminAppEmployeeId'])
                            {
                                $checked = false;
                                break;
                            }
                        }
                        $i++;
                    }
                    if($checked)
                    {
                        if(!empty($row["AdminAppEmployeeId"])){
                            deleteManagerPartnerData($row['AdminAppEmployeeId']);
                        }
                    }
                    
                }
            }
            else
            {
                while($row = mysqli_fetch_array($getAllAdminAppEmployeeId_result))
                {
                    if(!empty($row["AdminAppEmployeeId"])){
                        deleteManagerPartnerData($row['AdminAppEmployeeId']);
                    }
                }   
            }
        }
        else
        {
            while($i<$count)
            {
                 addManagerPartnerData($managerIdList[$i]);
                 $i++;
            }
        }
    }

    function deleteManagerPartnerData($managerEmployeeId)
    {
        global $connection, $employeeEntityRoleMappingTable, $roleId, $parkingEmployeeMappingTable, $parkingId;
        $getEmployeeId_query = "select EmployeeId from ".$employeeEntityRoleMappingTable." where EmployeeId = $managerEmployeeId and RoleId = $roleId limit 1";
        $getEmployeeId_result = mysqli_query($connection, $getEmployeeId_query);
        if(!$getEmployeeId_result){
            echo "Error retrieving record '" . mysqli_error($connection) . "'";
        }
        else{
            if(mysqli_num_rows($getEmployeeId_result) == 1){
                $deleteEmployeeFromParking_query = "delete from ".$parkingEmployeeMappingTable." where AdminAppEmployeeId = $managerEmployeeId and ParkingId = $parkingId";
                $deleteEmployeeFromParking_result = mysqli_query($connection, $deleteEmployeeFromParking_query);
                if(!$deleteEmployeeFromParking_result){
                    echo "Error deleting record '" . mysqli_error($connection) . "'";
                }
                else{
                    echo "update";
                }
            }
        }
    }

    function addManagerPartnerData($managerId)
    {
        global $connection, $parkingEmployeeMappingTable, $parkingId;
        $addManagerParking_query = "insert into ".$parkingEmployeeMappingTable." (ParkingId, ConsumerAppEmployeeId, AdminAppEmployeeId) values ($parkingId, null, $managerId)";
        $addManagerParking_result = mysqli_query($connection, $addManagerParking_query);
        if(!$addManagerParking_result){
            echo "Error adding record '" . mysqli_error($connection) . "'";
        }
        else{
            echo "update";
        }
    }

    function getRoleId(){
        global $connection, $role, $employeeRoleTable;

        $getRoleId_query = "select id from ".$employeeRoleTable." where Role = '$role' limit 1";
        $getRoleId_result = mysqli_query($connection, $getRoleId_query);
        if(!$getRoleId_result){
            echo "Error retrieving record '" . mysqli_error($connection) . "'";
        }
        else{
            if(mysqli_num_rows($getRoleId_result)==1){
                $row = mysqli_fetch_assoc($getRoleId_result);
                return $row["id"];
            }
        }
    }

?>