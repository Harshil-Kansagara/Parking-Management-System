<?php

    require "init.php";
    require "create_employeeTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_employeeLocationMappingTable.php";

    $locationId = $_GET["LocationId"];
    $role = $_GET["EmployeeRole"];
    $roleId = getRoleId($role);
    $entityId = getEntityId();
    $response = array();

    $getAllEmployeeId_query = "select EmployeeId from ".$employeeLocationMappingTable." where LocationId = $locationId ";
        $getAllEmployeeId_result = mysqli_query($connection, $getAllEmployeeId_query);
        if(!$getAllEmployeeId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getAllEmployeeId_result)>0){
                while($row = mysqli_fetch_assoc($getAllEmployeeId_result)){
                    $employeeId = $row['EmployeeId'];
                    $generatedData = getGeneratedEmployeeAndRole($employeeId);
                    if(!empty($generatedData["GeneratedEmployeeId"])){
                        $employeeData = getEmployeeData($employeeId);
                        $array_result = array_merge($employeeData, $generatedData);
                        array_push($response, $array_result);
                    }
                }
            }
        }
    echo json_encode(array('server_response'=>$response));

    function getEmployeeData($employeeId){
        global $connection, $employeeTable;
        $employee_query = "select id, EmployeeName, EmployeeActiveState from ".$employeeTable." where id = $employeeId limit 1";
        $employee_reult = mysqli_query($connection, $employee_query);
        if(!$employee_reult){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($employee_reult);
            $employeeData = array("id"=>$row["id"], "EmployeeName"=>$row["EmployeeName"], "EmployeeActiveState"=>$row["EmployeeActiveState"]);
            return $employeeData;
        }
    }

    function getGeneratedEmployeeAndRole($employeeId){
        global $connection, $employeeEntityRoleMappingTable, $roleId, $role;
        $getGeneratedId_RoleId_query = "select GeneratedEmployeeId from ".$employeeEntityRoleMappingTable." where EmployeeId = $employeeId and RoleId = $roleId";
        $getGeneratedId_RoleId_result = mysqli_query($connection, $getGeneratedId_RoleId_query);
        if(!$getGeneratedId_RoleId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getGeneratedId_RoleId_result)>0){
                $row = mysqli_fetch_assoc($getGeneratedId_RoleId_result);
                $generatedEmployeeId = $row["GeneratedEmployeeId"];
                $generatedData = array("GeneratedEmployeeId"=>$generatedEmployeeId, "Role"=>$role);
                return $generatedData;
            }
        }
    }

    function getRoleName($roleId){
        global $connection, $employeeRoleTable;
        $getRoleName_query = "select Role from ".$employeeRoleTable." where id = $roleId limit 1";
        $getRoleName_result = mysqli_query($connection, $getRoleName_query);
        if(!$getRoleName_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($getRoleName_result);
            return $row['Role'];
        }
    }

    function getRoleId($role)
    {
        global $connection, $employeeRoleTable;

        $roleId_query = "select id from ".$employeeRoleTable." where Role = '$role'";
        $roleId_result = mysqli_query($connection,$roleId_query);
        if(!$roleId_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
            return;
        } 
        else
        {
            $row = mysqli_fetch_assoc($roleId_result);
            return $row['id'];
        }        
    }

    function getEntityId(){
        global $connection, $entityTable;
        $entityId_query = "select id from ".$entityTable." where Entity = 'ConsumerApp' limit 1";
        $entityId_result = mysqli_query($connection, $entityId_query);
        if(!$entityId_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
            return;
        } 
        else
        {
            $row = mysqli_fetch_assoc($entityId_result);
            return $row['id'];
        }       
    }
?>