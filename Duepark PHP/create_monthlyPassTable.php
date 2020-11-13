<?php

    require "init.php";
    
    $monthlyPassTable = "monthlypass";

    $sql_query = "show tables like '$monthlyPassTable'";

    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createMonthlyPassTable();
    }

    function createMonthlyPassTable(){
        global $monthlyPassTable, $connection;

        $sql_query = "create table ".$monthlyPassTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            PassUserName varchar(255) NOT NULL,
            PassUserMobileNumber varchar(255) NOT NULL,
            PassDuration varchar(255) NOT NULL,
            PayableAmount varchar(255) NOT NULL,
            PayableType varchar(255) NOT NULL,
            IssuedDate varchar(255) NOT NULL,
            ExpiryDate varchar(255) NOT NULL,
            IssuedTime varchar(255) NOT NULL,
            IssuedEmployeeId int(200) NOT NULL,
            IssuedBy varchar(255) NOT NULL,
            ActiveMonthlyPassState boolean NOT NULL,
            FOREIGN KEY (IssuedEmployeeId) references employee(id))";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>