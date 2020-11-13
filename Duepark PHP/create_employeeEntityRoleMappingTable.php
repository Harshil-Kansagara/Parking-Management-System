<?php

    require "init.php";
    //require "create_employeeTable.php";
    require "seed_entityTable.php";
    require "seed_roleTable.php";

    $employeeEntityRoleMappingTable = "employeeentityrolemapping";

    $sql_query = "SHOW TABLES LIKE '".$employeeEntityRoleMappingTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createEmployeeEntityRoleMappingTable();
    }

    function createEmployeeEntityRoleMappingTable(){
        global $employeeEntityRoleMappingTable, $connection;

        $sql_query = "create table ".$employeeEntityRoleMappingTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            EmployeeId int(200) NOT NULL,
            EntityId int(200) NOT NULL,
            RoleId int (200) NOT NULL,
            GeneratedEmployeeId int(200) NOT NULL,
            FOREIGN KEY (EmployeeId) references employee(id),
            FOREIGN KEY (EntityId) references entitytable(id),
            FOREIGN KEY (RoleId) references employeerole(id) 
        )";

        if(!mysqli_query($connection,$sql_query)){
            echo " Table not created '".mysqli_error($connection)."'";
        }
        else
        {
            addSuperAdmin_AdminApp();
        }
    }

    function addSuperAdmin_AdminApp()
    {
        global $connection, $employeeEntityRoleMappingTable, $employeeTable, $employeeRoleTable, $entityTable;

        $password = getPassword();
        $employeeId = 0; $roleId=0; $entityId = 0;

        $sql_query = "insert into employee (EmployeeName, EmployeeSate, EmployeeCity, EmployeeMobileNumber, EmployeePhoneNumber, 
        EmployeeEmailId, EmployeeAdharNumber, EmployeePassword, EmployeeProfilePic, IsEmployeeNewPasswordCreated, EmployeeActiveState) values ('Harshil Kansagara',
        null, null, '7621074037', null,'harshil@gmail.com', '1234 5678 9012', '$password', null, true, true)";
        
        if(!mysqli_query($connection,$sql_query)){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else
        {
            $employeeId = mysqli_insert_id($connection);
            
            $roleId_query = "select id from ".$employeeRoleTable." where Role = 'SuperAdmin'";
            $roleId_result = mysqli_query($connection,$roleId_query);
            if(!$roleId_result){
                echo "Error creating new record '".mysqli_error($connection)."'";
            } 
            else
            {
                $row = mysqli_fetch_assoc($roleId_result);
                $roleId = $row['id'];
            }

            $entityId_query = "select id from ".$entityTable." where Entity = 'AdminApp'"; 
            $entityId_result = mysqli_query($connection,$entityId_query);
            if(!$entityId_result){
                echo "Error creating new record '".mysqli_error($connection)."'";
            } 
            else
            {
                $row = mysqli_fetch_assoc($entityId_result);
                $entityId = $row['id'];
            }
            
            if($entityId != 0 && $roleId != 0)
            {
                $add_employeeEntityRoleMapping_query = "insert into ".$employeeEntityRoleMappingTable." (EmployeeId, EntityId, RoleId, 
                GeneratedEmployeeId) values ($employeeId, $entityId, $roleId, 1)";

                if(!mysqli_query($connection, $add_employeeEntityRoleMapping_query)){
                    echo "Error creating new record '".mysqli_error($connection)."'";
                }
            }
        }
    }

    function getPassword(){
        $alphabet = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890';
        $pass = array();
        $alphaLength = strlen($alphabet) - 1;
        for ($i = 0; $i < 8; $i++) {
            $n = rand(0, $alphaLength);
            $pass[] = $alphabet[$n];
        }
        return implode($pass);
    }


?>