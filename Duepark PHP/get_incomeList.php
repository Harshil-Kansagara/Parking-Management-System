<?php

// Here add by default name of table so change it as needed.

    require "init.php";
    require "create_locationParkingMappingTable.php";

    $parkingId = $_GET["ParkingId"];
    $date = $_GET["Date"];
    $parkedVehicleIncomeList = array();
    $monthlyPassIncomeList = array();
    $incomeData = array();
    $allLocationIncomeList = array();

    if (!empty($parkingId) && !empty($date)) {
        $getAllLocations_query = "select LocationId from " . $parkingLocationMappingTable . " where ParkingId = $parkingId";
        $getAllLocations_result = mysqli_query($connection, $getAllLocations_query);

        if (!$getAllLocations_result) {
            echo "Error retrieving record '" . mysqli_error($connection) . "'";
        } else {
            if (mysqli_num_rows($getAllLocations_result) > 0) {
                while ($row = mysqli_fetch_assoc($getAllLocations_result)) {
                    array_push($allLocationIncomeList, getLocationIncomeData($row['LocationId']));
                }
            }
            echo json_encode(array("server_response"=>$allLocationIncomeList));
        }
    }

    // Single Location income data
    function getLocationIncomeData($locationId)
    {
        global $connection;
        $getLocationData_query = "select LocationName, GeneratedLocationId from location where id = $locationId limit 1";
        $getLocationData_result = mysqli_query($connection, $getLocationData_query);
        if (!$getLocationData_result) {
            echo "Error retrieving record '" . mysqli_error($connection) . "'";
        } else {
            if (mysqli_num_rows($getLocationData_result) == 1) {
                $row = mysqli_fetch_assoc($getLocationData_result);
                $parkingData = getParkingData();
                $locationData = array_merge($parkingData, array("LocationId" => $locationId, "GeneratedLocationId"=>$row["GeneratedLocationId"], "LocationName" => $row["LocationName"]));
                $parkedVehicleIncomeList = getParkedVehicleIncomeData($locationId);
                $monthlyPassIncomeList = getMonthlyPassIncomeData($locationId);
                $incomeData = array_merge($parkedVehicleIncomeList, $monthlyPassIncomeList);
                
                array_push($locationData, $incomeData);
                
                return $locationData;
            }
        }
    }

    function getMonthlyPassIncomeData($locationId)
    {
        global $connection, $date;
        $getMonthlyPassIncomeData = array();

        $getAllMonthlyPassId_query = "select MonthlyPassId from locationmonthlypassmapping where LocationId = $locationId ";
        $getAllMonthlyPassId_result = mysqli_query($connection, $getAllMonthlyPassId_query);

        if (!$getAllMonthlyPassId_result) {
            echo "Error retrieving record '" . mysqli_error($connection) . "'";
        } else 
        {
            if (mysqli_num_rows($getAllMonthlyPassId_result) > 0) 
            {
                while ($row = mysqli_fetch_assoc($getAllMonthlyPassId_result)) 
                {
                    $monthlyPassId = $row['MonthlyPassId'];
                    $getMonthlyPassIncomeData_query = "select PayableAmount, PayableType, IssuedEmployeeId, IssuedBy from monthlypass where id = $monthlyPassId and IssuedDate='$date'";
                    $getMonthlyPassIncomeData_result = mysqli_query($connection, $getMonthlyPassIncomeData_query);
                    if (!$getMonthlyPassIncomeData_result) {
                        echo "Error retrieving record '" . mysqli_error($connection) . "'";
                    } else 
                    {
                        if (mysqli_num_rows($getMonthlyPassIncomeData_result) == 1) {
                            $cashCount = "00";
                            $onlineCount = "00";
                            $row = mysqli_fetch_assoc($getMonthlyPassIncomeData_result);
                            if($row['PayableAmount'] != "00"){
                                if($row["PayableType"] == "Cash" ){
                                    $cashCount = $row["PayableAmount"];
                                }
                                else if($row["PayableType"]== "Online"){
                                    $onlineCount = $row["PayableAmount"];
                                }
                            }
                            array_push($getMonthlyPassIncomeData, array("EmployeeName"=>$row["IssuedBy"], "EmployeeId"=>$row["IssuedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                        }
                    }
                }
                return $getMonthlyPassIncomeData;
            }
            return $getMonthlyPassIncomeData;
        }
    }

    function getParkedVehicleIncomeData($locationId){
        global $connection, $date;
        $getParkedVehicleIncomeData = array();
        $getParkedVehicleIncomeData_query = "select PaidAmount, ParkedPaymentType, ParkedBy, ParkedEmployeeId, ReleasedEmployeeId,
                             ReleasedAmount, ReleasedBy, ReleasedPaymentType from parkedvehicle where LocationId = $locationId and ParkedDate = '$date'";
        $getParkedVehicleIncomeData_result = mysqli_query($connection, $getParkedVehicleIncomeData_query);
        if(!$getParkedVehicleIncomeData_result)
        {
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getParkedVehicleIncomeData_result)>0){
                while($row = mysqli_fetch_assoc($getParkedVehicleIncomeData_result)){
                    if(!empty($row["ReleasedEmployeeId"]))
                    {
                        if($row["ParkedEmployeeId"] == $row["ReleasedEmployeeId"]){
                            $cashCount = "00";
                            $onlineCount = "00";
                            if($row["PaidAmount"] != 00){
                                if($row["ParkedPaymentType"]=="Cash"){
                                    $cashCount = $row["PaidAmount"];
                                }
                                else if($row["ParkedPaymentType"] == "Online"){
                                    $onlineCount = $row["PaidAmount"];
                                }
                            }
                            if($row["ReleasedAmount"] != 00 && $row["ReleasedAmount"] != null){
                                if($row["ReleasedPaymentType"]=="Cash"){
                                    $cashCount = $row["ReleasedAmount"];
                                }
                                else if($row["ReleasedPaymentType"]=="Online"){
                                    $onlineCount = $row["ReleasedAmount"];
                                }
                            }
                            array_push($getParkedVehicleIncomeData, array("EmployeeName"=>$row["ParkedBy"], "EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                        }
                        else
                        {
                            $cashCount = "00";
                            $onlineCount = "00";    
                            if($row["PaidAmount"] != 00){
                                if($row["ParkedPaymentType"]=="Cash"){
                                    $cashCount = $row["PaidAmount"];
                                    $onlineCount = "00";
                                }
                                else if($row["ParkedPaymentType"] == "Online"){
                                    $cashCount = "00";
                                    $onlineCount = $row["PaidAmount"];
                                }
                                array_push($getParkedVehicleIncomeData, array("EmployeeName"=>$row["ParkedBy"], "EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                            }
                            else{
                                $cashCount = "00";
                                $onlineCount = "00";
                                array_push($getParkedVehicleIncomeData, array("EmployeeName"=>$row["ParkedBy"], "EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                            }
                            if($row["ReleasedAmount"] != 00 && $row["ReleasedAmount"] != null){
                                // $cashCount = "00";
                                // $onlineCount = "00";
                                if($row["ReleasedPaymentType"]=="Cash"){
                                    $cashCount = $row["ReleasedAmount"];
                                    $onlineCount = "00";
                                }
                                else if($row["ReleasedPaymentType"]=="Online"){
                                    $cashCount = "00";
                                    $onlineCount = $row["ReleasedAmount"];
                                }
                                array_push($getParkedVehicleIncomeData, array("EmployeeName"=>$row["ReleasedBy"], "EmployeeId"=>$row["ReleasedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                            }
                            else{
                                $cashCount = "00";
                                $onlineCount = "00";
                                array_push($getParkedVehicleIncomeData, array("EmployeeName"=>$row["ReleasedBy"], "EmployeeId"=>$row["ReleasedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                            }
                        }
                    }
                    else{
                        $cashCount = "00";
                        $onlineCount = "00";    
                        if($row["PaidAmount"] != 00){
                            if($row["ParkedPaymentType"]=="Cash"){
                                $cashCount = $row["PaidAmount"];
                                $onlineCount = "00";
                            }
                            else if($row["ParkedPaymentType"] == "Online"){
                                $cashCount = "00";
                                $onlineCount = $row["PaidAmount"];
                            }
                            array_push($getParkedVehicleIncomeData, array("EmployeeName"=>$row["ParkedBy"], "EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                        } 
                        else{
                            $cashCount = "00";
                            $onlineCount = "00";
                            array_push($getParkedVehicleIncomeData, array("EmployeeName"=>$row["ParkedBy"], "EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                        }
                    }
                }
                return $getParkedVehicleIncomeData;
            }
            return $getParkedVehicleIncomeData;
        }
    }

    function getParkingData(){
        global $connection, $parkingId;
        $getParkingData_query = "select ParkingId, ParkingAcronym from parking where id = $parkingId limit 1";
        $getParkingData_result = mysqli_query($connection, $getParkingData_query);
        if(!$getParkingData_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            $row = mysqli_fetch_assoc($getParkingData_result);
            $parkingData = array("GeneratedParkingId"=>$row["ParkingId"], "ParkingAcronym"=>$row["ParkingAcronym"]);
            return $parkingData;
        }

    }
?>