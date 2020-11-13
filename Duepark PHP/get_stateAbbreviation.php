<?php

    require "init.php";
    require "seed_stateAbbreviation.php";

    $stateName = $_GET["state"];

    $sql_query = "select StateAbbreviation from ".$stateTable." where StateName = '".$stateName."'";

    $result = mysqli_query($connection,$sql_query);

    $response = array();

    if(mysqli_num_rows($result)==1){
        while($row =  mysqli_fetch_array($result)){
            array_push($response, array('StateAbbreviation'=>$row["StateAbbreviation"]));
        }
    }else{
        echo json_encode("Could not get data: ".$mysqli_error($connection));   
    }

    mysqli_close($connection);

    echo json_encode(array('server_response'=>$response));

?>