<?php

    require "init.php";
    require "create_parkingTable.php";
    // require "create_locationTable.php";

    $parkingLocationMappingTable = "parkinglocationmapping";

    $sql_query = "SHOW TABLES LIKE '".$parkingLocationMappingTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createParkingLocationMappingTable();
    }

    function createParkingLocationMappingTable(){
        global $parkingLocationMappingTable, $connection;

        $sql_query = "create table ".$parkingLocationMappingTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            ParkingId int(200) NOT NULL,
            LocationId int(200) NOT NULL,
            FOREIGN KEY (ParkingId) references parking(id),
            FOREIGN KEY (LocationId) references location(id) 
        )";

        if(!mysqli_query($connection,$sql_query)){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>