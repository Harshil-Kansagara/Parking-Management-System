<?php

    require "init.php";
    require "create_locationTable.php";

    $locationDetailTable = "locationdetail";

    $sql_query = "show tables like '$locationDetailTable'";

    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createLocationDetailTable();
    }

    function createLocationDetailTable(){
        global $locationDetailTable, $connection;

        $sql_query = "create table ".$locationDetailTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            LocationId int(200) NOT NULL,
            LocationOpenTime varchar(255) NULL,
            LocationCloseTime varchar(255) NULL,
            CarChargeType Varchar(255) NULL, 
            CarFixHour varchar(255) NULL,
            CarFixHourRate varchar(255) NULL,
            CarChargesOption varchar(255) NULL,
            CarChargesOptionRate varchar(255) NULL,
            CarCapacity varchar(255) NULL,
            BikeChargeType Varchar(255) NULL, 
            BikeFixHour varchar(255) NULL,
            BikeFixHourRate varchar(255) NULL,
            BikeChargesOption varchar(255) NULL,
            BikeChargesOptionRate varchar(255) NULL, 
            BikeCapacity varchar(255) NULL,
            CarMonthlyPassRate varchar(255) NULL, 
            BikeMonthlyPassRate varchar(255) NULL,
            FOREIGN KEY (LocationId) references location(id))";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>