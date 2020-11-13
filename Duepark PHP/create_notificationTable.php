<?php

    require "init.php";
    //require "create_employeeTable.php";

    $consumerNotificationTable = "consumernotification";

    $sql_query = "SHOW TABLES LIKE '".$consumerNotificationTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createNotification();
    }

    function createNotification(){
        global $consumerNotificationTable, $connection;
        $sql_query = "create table ".$consumerNotificationTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            EmployeeId int(200) NOT NULL,
            IsActivated boolean NOT NULL,
            FOREIGN KEY (EmployeeId) references employee(id)
        )";

        if(!mysqli_query($connection,$sql_query)){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>