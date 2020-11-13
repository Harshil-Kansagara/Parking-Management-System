<?php

    require "init.php";
    // require "create_parkingTable.php";
    // require "create_employeeTable.php";

    $parkingEmployeeMappingTable = "parkingemployeemapping";

    $sql_query = "SHOW TABLES LIKE '".$parkingEmployeeMappingTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createParkingEmployeeMapping();
    }

    function createParkingEmployeeMapping(){
        global $parkingEmployeeMappingTable, $connection;
        $sql_query = "create table ".$parkingEmployeeMappingTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            ParkingId int(200) NOT NULL,
            ConsumerAppEmployeeId int(200) NULL,
            AdminAppEmployeeId int(200) NULL,
            FOREIGN KEY (ParkingId) references parking(id),
            FOREIGN KEY (ConsumerAppEmployeeId) references employee(id),
            FOREIGN KEY (AdminAppEmployeeId) references employee(id)
        )";

        if(!mysqli_query($connection,$sql_query)){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>