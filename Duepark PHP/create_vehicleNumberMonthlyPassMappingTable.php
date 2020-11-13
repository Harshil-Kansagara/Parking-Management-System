<?php

    require "init.php";
    require "create_monthlyPassTable.php";

    $vehicleNumberMonthlyPassMappingTable = "vehiclenumbermonthlypassmapping";

    $sql_query = "show tables like '$vehicleNumberMonthlyPassMappingTable'";

    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createVehicleNumberMonthlyPassMappingTable();
    }

    function createVehicleNumberMonthlyPassMappingTable(){
        global $vehicleNumberMonthlyPassMappingTable, $connection;

        $sql_query = "create table ".$vehicleNumberMonthlyPassMappingTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            MonthlyPassId int(200) NOT NULL,
            VehicleType varchar(255) NOT NULL,
            VehicleNumber varchar(255) NOT NULL,
            FOREIGN KEY (MonthlyPassId) references monthlypass(id))";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>