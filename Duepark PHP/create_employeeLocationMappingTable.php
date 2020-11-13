<?php

    require "init.php";
    //require "create_employeeTable.php";
    require "create_locationTable.php";

    $employeeLocationMappingTable = "employeelocationmapping";

    $sql_query = "SHOW TABLES LIKE '".$employeeLocationMappingTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createEmployeeLocationMappingTable();
    }

    function createEmployeeLocationMappingTable(){
        global $employeeLocationMappingTable, $connection;
        $sql_query = "create table ".$employeeLocationMappingTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            EmployeeId int(200) NOT NULL,
            LocationId int(200) NOT NULL,
            FOREIGN KEY (EmployeeId) references employee(id),
            FOREIGN KEY (LocationId) references location(id)
        )";

        if(!mysqli_query($connection,$sql_query)){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>