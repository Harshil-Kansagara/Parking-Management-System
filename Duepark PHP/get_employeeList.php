<?php

    require "init.php";
    require "create_employeeTable.php";
    //require "seed_entityTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_adminManagerEmployeeMappingTable.php";
    require "create_admin_managerValetEmployeeMappingTable.php";
    require "create_admin_managerSaleEmployeeMappingTable.php";
    require "create_employeeLocationMappingTable.php";
    //require "seed_roleTable.php";

    $loggedInEmployeeId = $_GET["LoggedInEmployeeId"];
    $employeeRole = $_GET["Role"];
    $loggedInEmployeeRole = $_GET["LoggedInEmployeeRole"];
    $employeeActiveState = $_GET["EmployeeActiveState"];
    
    if(isset($_GET["ParkingId"])){
        $parkingId = $_GET["ParkingId"];
    }
    else{
        $parkingId = null;
    }

    $roleId = getRoleId();
    $response = array();

    if($parkingId !=null){
        $entityId = getEntityId("ConsumerApp");
        if($loggedInEmployeeRole == "Manager"){
            $getLocationId_query = "select LocationId from ".$employeeLocationMappingTable." where EmployeeId=$loggedInEmployeeId limit 1";
            $getLocationId_result = mysqli_query($connection, $getLocationId_query);
            if(!$getLocationId_result){
                echo "Error retrieving record '".mysqli_error($connection)."'";
            }
            else{
                if(mysqli_num_rows($getLocationId_result)==1){
                    $row = mysqli_fetch_assoc($getLocationId_result);
                    $locationId = $row['LocationId'];
                    $getAllEmployeeForLocation_query = "select EmployeeId from ".$employeeLocationMappingTable." where LocationId=$locationId";
                    $getAllEmployeeForLocation_result = mysqli_query($connection, $getAllEmployeeForLocation_query);
                    if(!$getAllEmployeeForLocation_result){
                        echo "Error retrieving record '".mysqli_error($connection)."'";
                    }
                    else{
                        if(mysqli_num_rows($getAllEmployeeForLocation_result)>0){
                            while($row = mysqli_fetch_assoc($getAllEmployeeForLocation_result)){
                                $employeeId = $row['EmployeeId'];
                                $getEmployee_query = "select EmployeeId, GeneratedEmployeeId from ".$employeeEntityRoleMappingTable." where EmployeeId=$employeeId and EntityId=$entityId
                                        and RoleId = $roleId limit 1";
                                $getEmployee_result = mysqli_query($connection, $getEmployee_query);
                                if(!$getEmployee_result){
                                    echo "Error retrieving record '".mysqli_error($connection)."'";
                                }
                                else{
                                    if(mysqli_num_rows($getEmployee_result) == 1){
                                        $row = mysqli_fetch_assoc($getEmployee_result);
                                        $generatedId = $row["GeneratedEmployeeId"];
                                        $employeeId = $row["EmployeeId"];
                                        $employeeData = getEmployeeData($employeeId);
                                        if(count($employeeData)>0){
                                            $employeeData = array_merge($employeeData, array("GeneratedEmployeeId"=>$generatedId, "Role"=>$employeeRole));
                                            array_push($response, $employeeData);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else{
            $getConsumerAppEmployeeIdByParkingId_query = "select ConsumerAppEmployeeId from ".$parkingEmployeeMappingTable." where ParkingId = $parkingId";
            $getConsumerAppEmployeeIdByParkingId_result = mysqli_query($connection, $getConsumerAppEmployeeIdByParkingId_query);
            if(!$getConsumerAppEmployeeIdByParkingId_result){
                echo "Error retrieving record '".mysqli_error($connection)."'";
            } 
            else{
                if(mysqli_num_rows($getConsumerAppEmployeeIdByParkingId_result)>0)
                {
                    while($row = mysqli_fetch_assoc($getConsumerAppEmployeeIdByParkingId_result))
                    {
                        $consumerAppEmployeeId = $row["ConsumerAppEmployeeId"];
                        $getEmployee_query = "select EmployeeId, GeneratedEmployeeId from ".$employeeEntityRoleMappingTable." where EmployeeId=$consumerAppEmployeeId and EntityId=$entityId
                                        and RoleId = $roleId limit 1";
                        $getEmployee_result = mysqli_query($connection, $getEmployee_query);
                        if(!$getEmployee_result){
                            echo "Error retrieving record '".mysqli_error($connection)."'";
                        }
                        else{
                            if(mysqli_num_rows($getEmployee_result) == 1){
                                $row = mysqli_fetch_assoc($getEmployee_result);
                                $generatedId = $row["GeneratedEmployeeId"];
                                $employeeId = $row["EmployeeId"];
                                if($loggedInEmployeeRole != "SuperAdmin" && $loggedInEmployeeRole != "Admin"){
                                    /*if($role == "Manager"){
                                        $employeeId = getManagerId($employeeId);
                                    }*/
                                    /*if($role == "Valet"){
                                        $employeeId = getValetId($employeeId);
                                    }*/
                                    if($role == "Sale"){
                                        $employeeId = getSaleId($employeeId);
                                    }
                                }
                               $employeeData = getEmployeeData($employeeId);
                               if(count($employeeData)>0){
                                    $employeeData = array_merge($employeeData, array("GeneratedEmployeeId"=>$generatedId, "Role"=>$employeeRole));
                                    array_push($response, $employeeData);
                               }
                            }
                        }
                    }
                }
            }
        }
    }
    else{
        $entityId = getEntityId('AdminApp');
        if($loggedInEmployeeRole=="Manager"){
            $getAllSalesId_query = "select SaleId from ".$admin_managerSaleEmployeeMappingTable." where Admin_ManagerId = $loggedInEmployeeId";
            $getAllSalesId_result = mysqli_query($connection, $getAllSalesId_query);
            if(!$getAllSalesId_result){
                echo "Error retrieveing record '".mysqli_error($connection)."'";
            }
            else{
                if(mysqli_num_rows($getAllSalesId_result)>0){
                    while($row = mysqli_fetch_assoc($getAllSalesId_result)){
                        $employeeId = $row["SaleId"];
                        $generatedEmployeeId = getGeneratedEmployeeId($employeeId);
                        $employeeData = getEmployeeData($employeeId);
                        $employeeData = array_merge($employeeData, array("GeneratedEmployeeId"=>$generatedEmployeeId, "Role"=>$employeeRole));
                        array_push($response, $employeeData);
                    }
                }        
            }
        }
        else{
            $getEmployeeIds_query = "select EmployeeId, GeneratedEmployeeId from ".$employeeEntityRoleMappingTable." where EntityId=$entityId
                                and RoleId = $roleId";
            $getEmployeeIds_result = mysqli_query($connection, $getEmployeeIds_query);
            if(!$getEmployeeIds_result){
                echo "Error retrieveing record '".mysqli_error($connection)."'";
            }
            else{
                if(mysqli_num_rows($getEmployeeIds_result)>0){
                    while($row = mysqli_fetch_assoc($getEmployeeIds_result)){
                        $employeeId = $row["EmployeeId"];
                        $generatedEmployeeId = $row["GeneratedEmployeeId"];
                        if($loggedInEmployeeRole != "SuperAdmin" && $loggedInEmployeeRole != "Admin"){
                            if($role == "Manager"){
                                $employeeId = getManagerId($employeeId);
                            }
                            else if($role == "Valet"){
                                $employeeId = getValetId($employeeId);
                            }
                            else if($role == "Sale"){
                                $employeeId = getSaleId($employeeId);
                            }
                        }
                        $employeeData = getEmployeeData($employeeId);
                        $employeeData = array_merge($employeeData, array("GeneratedEmployeeId"=>$generatedEmployeeId, "Role"=>$employeeRole));
                        array_push($response, $employeeData);
                    }
                }
            }
        }
    }
    echo json_encode(array('server_response'=>$response));

    function getGeneratedEmployeeId($employeeId){
        global $employeeEntityRoleMappingTable, $connection;
        $getEmployeeIds_query = "select GeneratedEmployeeId from ".$employeeEntityRoleMappingTable." where EmployeeId = $employeeId limit 1";
        $getEmployeeIds_result = mysqli_query($connection, $getEmployeeIds_query);
        if(!$getEmployeeIds_result){
            echo "Error retrieveing record '".mysqli_error($connection)."'";
        }
        else {
            if (mysqli_num_rows($getEmployeeIds_result) == 1) {
                $row = mysqli_fetch_assoc($getEmployeeIds_result);
                return $row["GeneratedEmployeeId"];
            }
        }
    }

    function getEmployeeData($employeeId){
        global $connection, $employeeTable, $employeeActiveState;
        $employee_query = "select id, EmployeeName, EmployeeActiveState from ".$employeeTable." where id = $employeeId and EmployeeActiveState = $employeeActiveState limit 1";
        $employee_result = mysqli_query($connection, $employee_query);
        if(!$employee_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $employeeData = array();
            if(mysqli_num_rows($employee_result)==1){
                $row = mysqli_fetch_assoc($employee_result);
                $employeeData = array("id"=>$row["id"], "EmployeeName"=>$row["EmployeeName"], "EmployeeActiveState"=>$row["EmployeeActiveState"]);
            }
            return $employeeData;
        }
    }

    function getManagerId($managerId){
        global $connection, $adminManagerEmployeeMappingTable, $loggedInEmployeeId;
        $getManagerId_query = "select ManagerId from ".$adminManagerEmployeeMappingTable." where AdminId = $loggedInEmployeeId and ManagerId = $managerId limit 1";
        $getManagerId_result = mysqli_query($connection, $getManagerId_query);
        if(!$getManagerId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getManagerId_result) == 1){
                $row = mysqli_fetch_assoc($getManagerId_result);
                return $row["ManagerId"];
            }
        }
    }

    function getValetId($valetId){
        global $connection, $loggedInEmployeeId, $admin_managerValetEmployeeMappingTable;
        $getValetId_query = "select ValetId from ".$admin_managerValetEmployeeMappingTable." where Admin_ManagerId = $loggedInEmployeeId and ValetId = $valetId limit 1";
        $getValetId_result = mysqli_query($connection, $getValetId_query);
        if(!$getValetId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getValetId_result) == 1){
                $row = mysqli_fetch_assoc($getValetId_result);
                return $row["ValetId"];
            }
        }
    }

    function getSaleId($saleId){
        global $connection, $loggedInEmployeeId, $admin_managerSaleEmployeeMappingTable;
        $getSaleId_query = "select SaleId from ".$admin_managerSaleEmployeeMappingTable." where Admin_ManagerId = $loggedInEmployeeId and SaleId = $saleId limit 1";
        $getSaleId_result = mysqli_query($connection, $getSaleId_query);
        if(!$getSaleId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getSaleId_result) == 1){
                $row = mysqli_fetch_assoc($getSaleId_result);
                return $row["SaleId"];
            }
        }
    }

    function getEntityId($entity){
        global $connection;
        $getEntityId_query = "select id from entitytable where Entity='$entity' limit 1";
        $getEntityId_result = mysqli_query($connection, $getEntityId_query);
        if(!$getEntityId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $row = mysqli_fetch_assoc($getEntityId_result);
            return $row["id"];
        }
    }

    function getRoleId()
    {
        global $connection, $employeeRoleTable, $employeeRole;
        $roleId_query = "select id from ".$employeeRoleTable." where Role = '$employeeRole'";
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

?>