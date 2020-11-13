<?php

    require "init.php";
    //require "create_employeeTable.php";
    define ("API_ACCESS_KEY", "AAAAa3YmA0o:APA91bG42crbEC8sIVcZtyEsFCSI4_FeitNwiVBJctoYyDUH_Dmqdmgzsvqk-ArTNrvWSIB_vdsGCUg1-nB40QUscABRIJll0M1OLAnN7rD2N9XN8fO1ICcScRPpEN3f9U9uz0h6j9zv");

    $adminAppEmployeeTokenTable = "adminappemployeetoken";

    $sql_query = "SHOW TABLES LIKE '".$adminAppEmployeeTokenTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createAdminAppEmployeeToken();
    }

    function createAdminAppEmployeeToken(){
        global $adminAppEmployeeTokenTable, $connection;
        $sql_query = "create table ".$adminAppEmployeeTokenTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            EmployeeId int(200) NOT NULL,
            Token varchar(255) NOT NULL,
            FOREIGN KEY (EmployeeId) references employee(id)
        )";

        if(!mysqli_query($connection,$sql_query)){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>