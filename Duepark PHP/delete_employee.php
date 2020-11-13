<?php

    require "init.php";
    require "create_employeeTable.php";
    require "create_adminAppEmployeeTokenTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_admin_managerSaleEmployeeMappingTable.php";
    require "create_adminManagerEmployeeMappingTable.php";

    
    $employeeId = $_GET["EmployeeId"];
    $employeeRole = $_GET["EmployeeRole"];
    $count = 0;

    if(!empty($employeeId) && !empty($employeeRole))
    {
        if($employeeRole == "Admin"){
            deleteAdminDataFromAdminManagerEmployeeMapping();
            deleteAdminOrManagerDataFromAdminManagerSaleEmployeeMapping();
        }
        else if($employeeRole == "Manager"){
            deleteManagerDataFromAdminManagerEmployeeMapping();
            deleteAdminOrManagerDataFromAdminManagerSaleEmployeeMapping();
            deleteParkingEmployeeMappingData();
        }
        else if($employeeRole == "Sale"){
            deleteSaleDataFromAdminManagerSaleEmployeeMapping();
            deleteParkingEmployeeMappingData();
        }
        deleteAdminAppEmployeeTokenData();
        deleteEmployeeEntityRoleMappingData();
        deleteEmployeeData();

        if($count == 5 || $count == 6){
            echo "delete";
        }
    }

    // Admin data delete method
    function deleteAdminDataFromAdminManagerEmployeeMapping(){
        global $connection, $employeeId, $adminManagerEmployeeMappingTable, $count;
        $deleteAdminData_query = "delete from ".$adminManagerEmployeeMappingTable." where Admin = $employeeId";
        $deleteAdminData_result = mysqli_query($connection, $deleteAdminData_query);
        if(!$deleteAdminData_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
        }
        else{
            $count = $count + 1;
        }
    }

    function deleteAdminOrManagerDataFromAdminManagerSaleEmployeeMapping(){
        global $connection, $employeeId, $admin_managerSaleEmployeeMappingTable, $count;
        $deleteAdminData_query = "delete from ".$admin_managerSaleEmployeeMappingTable." where Admin_ManagerId = $employeeId";
        $deleteAdminData_result = mysqli_query($connection, $deleteAdminData_query);
        if(!$deleteAdminData_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
        }
        else{
            $count = $count + 1;
        }
    }
    // end region

    // Manager data delete method
    function deleteManagerDataFromAdminManagerEmployeeMapping(){
        global $connection, $employeeId, $adminManagerEmployeeMappingTable, $count;
        $deleteManagerData_query = "delete from ".$adminManagerEmployeeMappingTable." where ManagerId = $employeeId";
        $deleteMangerData_result = mysqli_query($connection, $deleteManagerData_query);
        if(!$deleteMangerData_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
        }
        else{
            $count = $count + 1;
        }
    }
    // end region


    // Sale data delete method
    function deleteSaleDataFromAdminManagerSaleEmployeeMapping(){
        global $connection, $employeeId, $admin_managerSaleEmployeeMappingTable, $count;
        $deleteSaleData_query = "delete from ".$admin_managerSaleEmployeeMappingTable." where SaleId = $employeeId";
        $deleteSaleData_result = mysqli_query($connection, $deleteSaleData_query);
        if(!$deleteSaleData_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
        }
        else{
            $count = $count + 1;
        }
    }
    // end region

    // Change here
    function deleteParkingEmployeeMappingData(){
        global $connection, $employeeId, $parkingEmployeeMappingTable, $count;
        $deleteParkingEmployeeData_query = "delete from ".$parkingEmployeeMappingTable." where AdminAppEmployeeId = $employeeId";
        $deleteParkingEmployeeData_result = mysqli_query($connection, $deleteParkingEmployeeData_query);
        if(!$deleteParkingEmployeeData_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
        }
        else{
            $count = $count + 1;
        }
    }

    function deleteAdminAppEmployeeTokenData(){
        global $connection, $employeeId, $adminAppEmployeeTokenTable, $count;
        $deleteEmployeeTokenData_query = "delete from ".$adminAppEmployeeTokenTable." where EmployeeId = $employeeId";
        $deleteEmployeeTokenData_result = mysqli_query($connection, $deleteEmployeeTokenData_query);
        if(!$deleteEmployeeTokenData_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
        }
        else{
            $count = $count + 1;
        }
    }

    function deleteEmployeeEntityRoleMappingData(){
        global $connection, $employeeId, $employeeEntityRoleMappingTable, $count;
        $deleteEmployeeRoleData_query = "delete from ".$employeeEntityRoleMappingTable." where EmployeeId = $employeeId";
        $deleteEmployeeRoleData_result = mysqli_query($connection, $deleteEmployeeRoleData_query);
        if(!$deleteEmployeeRoleData_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
        }
        else{
            $count = $count + 1;
        }
    }

    function deleteEmployeeData(){
        global $connection, $employeeTable, $employeeId, $count;
        $deleteEmployeeData_query = "delete from ".$employeeTable." where id = $employeeId";
        $deleteEmployeeData_result = mysqli_query($connection, $deleteEmployeeData_query);
        if(!$deleteEmployeeData_result){
            echo "Error deleting record '".mysqli_error($connection)."'";
        }
        else{
            $count = $count + 1;
        }
    }
?>