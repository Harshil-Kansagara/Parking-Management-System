<?php
    require "init.php";

    $employeeRoleTable = "employeerole";

    $sql_query = "SHOW TABLES LIKE '".$employeeRoleTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 1){
        $sql_query = "SELECT * FROM ".$employeeRoleTable;
        $result = mysqli_query($connection,$sql_query);
        if(!mysqli_fetch_array($result)){
            addRoleData();
        }
    }
    else{
        $sql_query = "CREATE TABLE ".$employeeRoleTable."(
            id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
            Role VARCHAR(255) NOT NULL
        )";
        $result = mysqli_query($connection,$sql_query);
        if($result){
            addRoleData();   
        }else{
            echo "Table ".$employeeRoleTable. "not created '".mysqli_error($connection)."'";
        }
    }

    function addRoleData(){
        global $employeeRoleTable, $connection;
        $sql_query = "INSERT INTO ".$employeeRoleTable." (Role) VALUES ('SuperAdmin'), ('Admin'), ('Manager'), ('Valet'), ('Sale')";

        if(!mysqli_query($connection,$sql_query)){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
    }
?>  