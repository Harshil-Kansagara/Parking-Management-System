<?php

    require "init.php";
    //require "create_employeeTable.php";
    
    $admin_managerValetEmployeeMappingTable = "admin_managervaletemployeemapping";

    $sql_query = "show tables like '".$admin_managerValetEmployeeMappingTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createAdmin_ManagerValetEmployeeMappingTable(); 
    }

    function createAdmin_ManagerValetEmployeeMappingTable(){
        global $connection, $admin_managerValetEmployeeMappingTable;

        $sql_query = "create table ".$admin_managerValetEmployeeMappingTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            Admin_ManagerId int(200) NOT NULL,
            ValetId int(200) NOT NULL,
            FOREIGN KEY (Admin_ManagerId) references employee(id),
            FOREIGN KEY (ValetId) references employee(id) 
        )";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>