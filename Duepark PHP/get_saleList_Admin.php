<?php

    require "init.php";
    require "create_employeeTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_admin_managerSaleEmployeeMappingTable.php";
    require "create_parkingEmployeeMappingTable.php";

    $parkingId = $_GET["ParkingId"];
    $entity = "AdminApp";
    $role = "Sale";
    $loggedInEmployeeRole = $_GET["LoggedInEmployeeRole"];
    $loggedInEmployeeId = $_GET["LoggedInEmployeeId"];

    if(!empty($parkingId) && !empty($loggedInEmployeeRole) && !empty($loggedInEmployeeId)){
        $saleList = array();
        $roleId = getRoleId();
        $entityId = getEntityId();
        if($loggedInEmployeeRole == "Manager"){
            $getAllSalesId_query = "select SaleId from ".$admin_managerSaleEmployeeMappingTable." where Admin_ManagerId = $loggedInEmployeeId";
            $getAllSalesId_result = mysqli_query($connection, $getAllSalesId_query);
            if(!$getAllSalesId_result){
                echo "Error retrieveing record '".mysqli_error($connection)."'";
            }
            else{
                if(mysqli_num_rows($getAllSalesId_result)>0){
                    while($row = mysqli_fetch_assoc($getAllSalesId_result)){
                        $isChecked = false;
                        $employeeId = $row["SaleId"];
                        $employeeName = getEmployeeName($employeeId);
                        $getAdminAppEmployeeId_query = "select AdminAppEmployeeId from ".$parkingEmployeeMappingTable." where ParkingId = $parkingId and AdminAppEmployeeId = $employeeId limit 1";
                        $getAdminAppEmployeeId_result = mysqli_query($connection, $getAdminAppEmployeeId_query);
                        if(!$getAdminAppEmployeeId_result){
                            echo "Error retrieving record '" . mysqli_error($connection) . "'";
                        }
                        else{
                            if(mysqli_num_rows($getAdminAppEmployeeId_result)==1){
                                $isChecked = true;
                            }
                        }
                        array_push($saleList, array("EmployeeId"=>$employeeId, "EmployeeName"=>$employeeName, "IsChecked"=>$isChecked));
                    }
                    echo json_encode(array("server_response"=>$saleList));
                }
                else {
                    echo "no";
                }      
            }
        }
        else
        {
            $getAllEmployeeId_query = "select EmployeeId from ".$employeeEntityRoleMappingTable." where RoleId = $roleId and EntityId = $entityId ";
            $getAllEmployeeId_result = mysqli_query($connection, $getAllEmployeeId_query);
            if(!$getAllEmployeeId_result){
                echo "Error retrieving record '" . mysqli_error($connection) . "'";
            }
            else{
                if(mysqli_num_rows($getAllEmployeeId_result)>0){
                    while($row = mysqli_fetch_assoc($getAllEmployeeId_result)){
                        $isChecked = false;
                        $employeeId = $row["EmployeeId"];
                        $employeeName = getEmployeeName($employeeId);
                        $getAdminAppEmployeeId_query = "select AdminAppEmployeeId from ".$parkingEmployeeMappingTable." where ParkingId = $parkingId and AdminAppEmployeeId = $employeeId limit 1";
                        $getAdminAppEmployeeId_result = mysqli_query($connection, $getAdminAppEmployeeId_query);
                        if(!$getAdminAppEmployeeId_result){
                            echo "Error retrieving record '" . mysqli_error($connection) . "'";
                        }
                        else{
                            if(mysqli_num_rows($getAdminAppEmployeeId_result)==1){
                                $isChecked = true;
                            }
                        }
                        array_push($saleList, array("EmployeeId"=>$employeeId, "EmployeeName"=>$employeeName, "IsChecked"=>$isChecked));
                    }
                    echo json_encode(array("server_response"=>$saleList));
                }
                else{
                    echo "no";
                }
            }
        }
    }

    function getEmployeeName($employeeId){
        global $connection, $employeeTable;
        $getEmployeeName_query = "select EmployeeName from ".$employeeTable." where id = $employeeId limit 1";
        $getEmployeeName_result = mysqli_query($connection, $getEmployeeName_query);
        if(!$getEmployeeName_result){
            echo "Error retrieving record '" . mysqli_error($connection) . "'";
        }
        else{
            if(mysqli_num_rows($getEmployeeName_result) == 1){
                $row = mysqli_fetch_assoc($getEmployeeName_result);
                return $row["EmployeeName"];
            }
        }
    }

    function getEntityId(){
        global $connection, $entity, $entityTable;
        $getEntityId_query = "select Id from ".$entityTable." where Entity = '$entity' limit 1";
        $getEntityId_result = mysqli_query($connection, $getEntityId_query);
        if(!$getEntityId_result){
            echo "Error retrieving record '" . mysqli_error($connection) . "'";
        }
        else{
            if(mysqli_num_rows($getEntityId_result)==1){
                $row = mysqli_fetch_assoc($getEntityId_result);
                return $row["Id"];
            }
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