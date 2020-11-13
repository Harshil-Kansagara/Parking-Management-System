<?php

    require "init.php";
    //require "create_employeeTable.php";

    $adminManagerEmployeeMappingTable = "adminmanageremployeemapping";

    $sql_query = "show tables like '".$adminManagerEmployeeMappingTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createAdminManagerEmployeeMappingTable(); 
    }

    function createAdminManagerEmployeeMappingTable(){
        global $connection, $adminManagerEmployeeMappingTable;

        $sql_query = "create table ".$adminManagerEmployeeMappingTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            AdminId int(200) NOT NULL,
            ManagerId int(200) NOT NULL,
            FOREIGN KEY (AdminId) references employee(id),
            FOREIGN KEY (ManagerId) references employee(id) 
        )";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>