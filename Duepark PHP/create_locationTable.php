<?php
    require "init.php";

    $locationTable = "location";

    $sql_query = "show tables like '$locationTable'";

    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createTable();
    }

    function createTable(){
        global $locationTable, $connection;

        $sql_query = "create table ".$locationTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            GeneratedLocationId varchar(255) NOT NULL,
            LocationName varchar(255) NOT NULL,
            LocationAddress varchar(255) NOT NULL,
            LocationType Varchar(255) NOT NULL, 
            LocationDate varchar(255) NOT NULL,
            LocationTime varchar(255) NOT NULL,
            LocationActiveState boolean NOT NULL)";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }
?>