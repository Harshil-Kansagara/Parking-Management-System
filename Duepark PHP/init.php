<?php

    $db_name = "duepark";
    $mysql_user = "root";
    $mysql_pass = "";
    $server_name = "localhost";

    //Create Connection
    $connection = mysqli_connect($server_name, $mysql_user, $mysql_pass, $db_name);

    if(!$connection){
        die("Connection failed: " .mysqli_connect_error($connection));
    }

?>