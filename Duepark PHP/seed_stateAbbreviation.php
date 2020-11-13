<?php

    require "init.php";

    $stateTable = "state";

    $sql_query = "SHOW TABLES LIKE '".$stateTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 1){
        $sql_query = "SELECT * FROM ".$stateTable;
        $result = mysqli_query($connection,$sql_query);
        if(!mysqli_fetch_array($result)){
            addData();
        }
    }
    else{
        $sql_query = "CREATE TABLE ".$stateTable."(
            id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
            StateName VARCHAR(255) NOT NULL,
            StateAbbreviation VARCHAR(255) NOT NULL
        )";
        $result = mysqli_query($connection,$sql_query);
        if($result){
            addData();   
        }else{
            echo " Table ".$stateTable. "not created '".mysqli_error($connection)."'";
        }
    }

    function addData(){
        global $stateTable, $connection;

        $sql_query = "INSERT INTO ".$stateTable." (StateName, StateAbbreviation) 
            VALUES ('Andhra Pradesh','AP'),('Arunachal Pradesh','AR'),('Assam','AS'),('Bihar','BR'),
            ('Chhattisgarh','CG'),('Chandigarh','CH'),('Daman and Diu','DD'),('Dadra and Nagar Haveli','DH'),('Delhi','DL'),
            ('Goa','GA'),('Gujarat','GJ'),('Himachal Pradesh','HP'),('Haryana','HR'),
            ('Jammu and Kashmir','JK'),('Jharkhand','JH'),('Karnataka','KA'),('Kerala','KL'),('Lakshadweep','LD'),
            ('Maharashtra','MH'),('Meghalaya','ML'),('Manipur','MN'),('Madhya Pradesh','MP'),('Mizoram','MZ'),
            ('Nagaland','NL'),('Orissa','OR'),('Punjab','PB'),('Rajasthan','RJ'),('Sikkim','SK'),('Tamil Nadu','TN'),
            ('Tripura','TR'),('Uttarakhand','UK'),('Uttar Pradesh','UP'),('West Bengal','WB')";
            
        if(!mysqli_query($connection,$sql_query)){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
    }

?>