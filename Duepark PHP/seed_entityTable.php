<?php
    require "init.php";

    $entityTable = "entitytable";

    $sql_query = "SHOW TABLES LIKE '".$entityTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 1){
        $sql_query = "SELECT * FROM ".$entityTable;
        $result = mysqli_query($connection,$sql_query);
        if(!mysqli_fetch_array($result)){
            addEntityData();
        }
    }
    else{
        $sql_query = "CREATE TABLE ".$entityTable."(
            Id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
            Entity VARCHAR(255) NOT NULL
        )";
        $result = mysqli_query($connection,$sql_query);
        if($result){
            addEntityData();   
        }else{
            echo "Table ".$entityTable. "not created '".mysqli_error($connection)."'";
        }
    }

    function addEntityData(){
        global $entityTable, $connection;
        $sql_query = "INSERT INTO ".$entityTable." (Entity) VALUES ('AdminApp'), ('ConsumerApp')";

        if(!mysqli_query($connection,$sql_query)){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
    }

?>