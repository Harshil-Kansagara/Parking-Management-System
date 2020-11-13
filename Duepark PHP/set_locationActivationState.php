<?php

    require "init.php";
    require "create_locationTable.php";

    $locationId = $_POST["LocationId"];
    $locationActiveState = $_POST["LocationActiveState"];

    $updateLocationActiveState_query="update ".$locationTable." set LocationActiveState=$locationActiveState where id=$locationId";

    $updateLocationActiveState_result = mysqli_query($connection, $updateLocationActiveState_query);

    if($updateLocationActiveState_result){
        if($locationActiveState == "false"){
            echo "Deactivate";
        }
        else{
            echo "Activate";
        }
    }
    else{
        echo "Error updating record '".mysqli_error($connection)."'";
    }

?>