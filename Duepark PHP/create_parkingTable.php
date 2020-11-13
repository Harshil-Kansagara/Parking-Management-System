<?php
    require "init.php";
    
    $parkingTable = 'parking';

    $sql_query="SHOW TABLES LIKE '".$parkingTable."'";
    
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createParkingTable(); 
    }

    function createParkingTable(){
        global $parkingTable, $connection;

        $sql_query = "CREATE TABLE ".$parkingTable. "(
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            ParkingId int(255) NOT NULL,
            ParkingAcronym Varchar(255) NOT NULL,
            ParkingName Varchar(255) NOT NULL,
            ParkingType Varchar(255) NOT NULL,
            ParkingAddress Varchar(255) NOT NULL,
            ParkingCity Varchar(255) NOT NULL,
            ParkingDate Varchar(255) NOT NULL,
            ParkingTime Varchar(255) NOT NULL,
            ParkingActiveState boolean NOT NULL
        )";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }
?>