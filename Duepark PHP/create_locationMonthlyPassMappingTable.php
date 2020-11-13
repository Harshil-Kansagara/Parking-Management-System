<?php

    require "init.php";
    //require "create_locationTable.php";
    //require "create_monthlyPassTable.php";

    $locationMonthlyPassMappingTable = "locationmonthlypassmapping";

    $sql_query = "show tables like '$locationMonthlyPassMappingTable'";

    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createLocationMonthlyPassMappingTable();
    }

    function createLocationMonthlyPassMappingTable(){
        global $locationMonthlyPassMappingTable, $connection;

        $sql_query = "create table ".$locationMonthlyPassMappingTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            LocationId int(200) NOT NULL,
            ParkingId int(200) NOT NULL,
            MonthlyPassId int(200) NOT NULL,
            GeneratedMonthlyPassId int(200) NOT NULL,
            GeneratedLocationId varchar(255) NOT NULL,
            FOREIGN KEY (LocationId) references location(id),
            FOREIGN KEY (ParkingId) references parking(id),
            FOREIGN KEY (MonthlyPassId) references monthlypass(id))";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>