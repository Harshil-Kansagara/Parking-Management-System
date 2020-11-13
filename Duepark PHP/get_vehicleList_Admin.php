<?php

    require "init.php";
    require "create_parkedVehicleTable.php";

    $vehicleType = $_GET["VehicleType"];

    if(!empty($vehicleType)){
        $vehicleList = array();
        $getAllParkedVehicleList_query = "select id, VehicleNumber, PaidAmount, ParkedTime, ParkedDate, ReleasedAmount, ReleasedTime from ".$parkedVehicleTable." where VehicleType = '$vehicleType'";
        $getAllParkedVehicleList_result = mysqli_query($connection, $getAllParkedVehicleList_query);
        if(!$getAllParkedVehicleList_result)
        {
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getAllParkedVehicleList_result)>0)
            {
                while($row = mysqli_fetch_assoc($getAllParkedVehicleList_result))
                {
                    $totalAmount = 0;
                    $totalTime = "0";
                    if(!empty($row["ReleasedTime"])){
                        $totalTime = $row["ParkedTime"]." - ".$row["ReleasedTime"];
                    }
                    else{
                        $totalTime = $row["ParkedTime"];
                    }

                    if(!empty($row["ReleasedAmount"])){
                        $totalAmount = $row["PaidAmount"] + $row["ReleasedAmount"];
                    }
                    else{
                        $totalAmount = (int)$row["PaidAmount"];
                    }
                    $data = strtoupper($row["VehicleNumber"]);
                    $insertion = "-";
                    $firstIndex = 4;
                    $data1 = substr_replace($data, $insertion, $firstIndex, 0);
                    $secIndex = 7;
                    $vehicleNumber = substr_replace($data1, $insertion, $secIndex, 0);
                    array_push($vehicleList, array("ParkedVehicleId"=>$row["id"], "VehicleNumber"=>$vehicleNumber, "VehicleType"=>$vehicleType, "TotalTime"=>$totalTime, "TotalAmount"=>$totalAmount, "Date"=>$row["ParkedDate"]));
                }
                echo json_encode(array("server_response"=>$vehicleList));
            }
        }
    }

?>