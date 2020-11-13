<?php

    require "init.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_employeeEntityRoleMappingTable.php";

    $parkingId = $_POST["ParkingId"];
    $saleIds = $_POST["SaleIds"];
    $role = $_POST["Role"];

    $roleId = getRoleId($role);
    if($saleIds != "null"){
        $saleIdList = explode(",",$saleIds);
    }
    else {
        $saleIdList = [];
    }

    $i=0;
    $count = count($saleIdList);

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
                            if($saleIdList[$i] == $row['AdminAppEmployeeId'])
                            {
                                $checked = false;
                                break;
                            }
                        }
                    }
                    mysqli_data_seek($getAllAdminAppEmployeeId_result, 0);
                    if($checked)
                    {
                        addSalePartnerData($saleIdList[$i]);
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
                            if($saleIdList[$i] == $row['AdminAppEmployeeId'])
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
                            deleteSalePartnerData($row['AdminAppEmployeeId']);
                        }
                    }
                    
                }
            }
            else
            {
                while($row = mysqli_fetch_array($getAllAdminAppEmployeeId_result))
                {
                    if(!empty($row["AdminAppEmployeeId"])){
                        deleteSalePartnerData($row['AdminAppEmployeeId']);
                    }
                }   
            }
        }
        else
        {
            while($i<$count)
            {
                addSalePartnerData($saleIdList[$i]);
                $i++;
            }
        }
    }

    function deleteSalePartnerData($saleEmployeeId)
    {
        global $connection, $employeeEntityRoleMappingTable, $roleId, $parkingEmployeeMappingTable, $parkingId;
        $getEmployeeId_query = "select EmployeeId from ".$employeeEntityRoleMappingTable." where EmployeeId = $saleEmployeeId and RoleId = $roleId limit 1";
        $getEmployeeId_result = mysqli_query($connection, $getEmployeeId_query);
        if(!$getEmployeeId_result){
            echo "Error retrieving record '" . mysqli_error($connection) . "'";
        }
        else{
            if(mysqli_num_rows($getEmployeeId_result) == 1){
                $deleteEmployeeFromParking_query = "delete from ".$parkingEmployeeMappingTable." where AdminAppEmployeeId = $saleEmployeeId and ParkingId = $parkingId";
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

    function addSalePartnerData($saleId)
    {
        global $connection, $parkingEmployeeMappingTable, $parkingId;
        $addSaleParking_query = "insert into ".$parkingEmployeeMappingTable." (ParkingId, ConsumerAppEmployeeId, AdminAppEmployeeId) values ($parkingId, null, $saleId)";
        $addSaleParking_result = mysqli_query($connection, $addSaleParking_query);
        if(!$addSaleParking_result){
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