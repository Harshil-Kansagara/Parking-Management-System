<?php

    require "init.php";
    require "create_parkingTable.php";
    require "create_parkingEmployeeMappingTable.php";

    $employeeId = $_GET["EmployeeId"];
    $employeeRole = $_GET["EmployeeRole"];

    $response = array();

    if($employeeRole == "Manager" || $employeeRole == "Sale"){
        $getAllParking_query = "select ParkingId from ".$parkingEmployeeMappingTable." where AdminAppEmployeeId = $employeeId";
        $getAllParking_result = mysqli_query($connection, $getAllParking_query);
        if(!$getAllParking_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getAllParking_result)>0){
                while($row=mysqli_fetch_assoc($getAllParking_result)){
                    $parkingId = $row["ParkingId"];
                    $sql_query = "select id, ParkingId, ParkingAcronym, ParkingName, ParkingActiveState from ".$parkingTable." where ParkingActiveState=true and id = $parkingId limit 1";
                    $sql_result = mysqli_query($connection, $sql_query);
                    if(!$sql_result){
                        echo "Error retrieving record '".mysqli_error($connection)."'";
                    }
                    else{
                        if(mysqli_num_rows($sql_result)==1){
                            $row1 = mysqli_fetch_assoc($sql_result);
                            array_push($response, array('id'=>$row1['id'], 'ParkingId'=>$row1['ParkingId'], 'ParkingAcronym'=>$row1['ParkingAcronym'], 'ParkingName'=>$row1['ParkingName'], 'ParkingActiveState'=>$row1['ParkingActiveState']));
                        }
                    }
                }
            }
        }
        // $sql_query = "select id, ParkingId, ParkingAcronym, ParkingName, ParkingActiveState from ".$parkingTable." where ParkingActiveState='yes' and UserId=".$id;
        // if($designation == "Admin"){
        //     $query = "select ParkingId from AdminParking where AdminId=$id";
        // }
        // else if($designation == "Manager"){
        //     $query = "select ParkingId from ManagerParking where ManagerId=$id";
        // }
        // else if($designation == "Sale"){
        //     $query = "select ParkingId from SaleParking where SaleId=$id";
        // }
        // $sql_result = mysqli_query($connection, $query);
        // $result = mysqli_query($connection, $sql_query);

        // if(mysqli_num_rows($result)>0)
        // {    
        //     while($row = mysqli_fetch_array($result))
        //     {
        //         array_push($response,array('id'=>$row['id'], 'ParkingId'=>$row['ParkingId'], 'Acronym'=>$row['Acronym'], 'ParkingName'=>$row['ParkingName'], 'ParkingActiveState'=>$row['ParkingActiveState']));
        //     }
        // }
        // if(mysqli_num_rows($sql_result)>0)
        // {
        //     while($row = mysqli_fetch_array($sql_result))
        //     {
        //         $parking_query = "select id, ParkingId, Acronym, ParkingName, ParkingActiveState from ".$tablename." where ParkingActiveState='yes' and id=".$row['ParkingId'];
        //         $parking_result = mysqli_query($connection, $parking_query);
        //         if(mysqli_num_rows($parking_result) == 1)
        //         {
        //             while($row = mysqli_fetch_array($parking_result))
        //             {
        //                 array_push($response, array('id'=>$row['id'], 'ParkingId'=>$row['ParkingId'], 'Acronym'=>$row['Acronym'], 'ParkingName'=>$row['ParkingName'], 'ParkingActiveState'=>$row['ParkingActiveState']));
        //             }
        //         }
        //         else{
        //             echo "Could not get data: ".$mysqli_error($connection);
        //         }
        //     }
        // }
    }
    else
    {
        $sql_query = "select id, ParkingId, ParkingAcronym, ParkingName, ParkingActiveState from ".$parkingTable." where ParkingActiveState=true";
        $sql_result = mysqli_query($connection, $sql_query);
    
        if(!$sql_result)
        {
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {   if((mysqli_num_rows($sql_result)>0))
            {
                while($row = mysqli_fetch_array($sql_result))
                {
                    array_push($response, array('id'=>$row['id'], 'ParkingId'=>$row['ParkingId'], 'ParkingAcronym'=>$row['ParkingAcronym'], 'ParkingName'=>$row['ParkingName'], 'ParkingActiveState'=>$row['ParkingActiveState']));
                }
            }
        }
    }

    echo json_encode(array('server_response'=>$response));

?>