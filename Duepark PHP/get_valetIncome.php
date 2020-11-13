<?php

    require "init.php";
    require "create_locationMonthlyPassMappingTable.php";
    require "create_monthlyPassTable.php";
    require "create_parkedVehicleTable.php";

    $locationId = $_GET["LocationId"];
    $date = $_GET["Date"];

    if(!empty($locationId) && !empty($date)){
        $parkedVehicleIncomeList = getParkedVehicleIncomeData($locationId);
        $monthlyPassIncomeList = getMonthlyPassIncomeData($locationId);
        $incomeData = array_merge($parkedVehicleIncomeList, $monthlyPassIncomeList);
        echo json_encode(array("server_response"=>$incomeData));
    }

    function getMonthlyPassIncomeData()
    {
        global $connection, $locationId, $locationMonthlyPassMappingTable, $date, $monthlyPassTable;
        $getMonthlyPassIncomeData = array();

        $getAllMonthlyPassId_query = "select MonthlyPassId from ".$locationMonthlyPassMappingTable." where LocationId = $locationId ";
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
                    $getMonthlyPassIncomeData_query = "select PayableAmount, PayableType, IssuedEmployeeId from ".$monthlyPassTable." where id = $monthlyPassId and IssuedDate = '$date'";
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
                            array_push($getMonthlyPassIncomeData, array("EmployeeId"=>$row["IssuedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                        }
                    }
                }
                return $getMonthlyPassIncomeData;
            }
            return $getMonthlyPassIncomeData;
        }
    }

    // Add ParkedEmployeeId, ReleasedEmployeeId
    function getParkedVehicleIncomeData($locationId){
        global $connection, $locationId, $date, $parkedVehicleTable;
        $getParkedVehicleIncomeData = array();
        $getParkedVehicleIncomeData_query = "select ParkedEmployeeId, PaidAmount, ParkedPaymentType, 
                                        ReleasedEmployeeId, ReleasedAmount, ReleasedPaymentType from " .$parkedVehicleTable. " where 
                                        LocationId = $locationId and ParkedDate = '$date'";
        $getParkedVehicleIncomeData_result = mysqli_query($connection, $getParkedVehicleIncomeData_query);
        if(!$getParkedVehicleIncomeData_result)
        {
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getParkedVehicleIncomeData_result)>0){
                while($row = mysqli_fetch_assoc($getParkedVehicleIncomeData_result)){
                    // CHeck here when released employee id is not null
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
                            array_push($getParkedVehicleIncomeData, array("EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
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
                                array_push($getParkedVehicleIncomeData, array("EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                            }
                            else{
                                $cashCount = "00";
                                $onlineCount = "00";
                                array_push($getParkedVehicleIncomeData, array("EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
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
                                array_push($getParkedVehicleIncomeData, array("EmployeeId"=>$row["ReleasedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                            }
                            else{
                                $cashCount = "00";
                                $onlineCount = "00";
                                array_push($getParkedVehicleIncomeData, array("EmployeeId"=>$row["ReleasedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
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
                            array_push($getParkedVehicleIncomeData, array("EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                        } 
                        else{
                            $cashCount = "00";
                            $onlineCount = "00";
                            array_push($getParkedVehicleIncomeData, array("EmployeeId"=>$row["ParkedEmployeeId"], "CashPayment"=>$cashCount, "OnlinePayment"=>$onlineCount));
                        }       
                    }
                }
                return $getParkedVehicleIncomeData;
            }
            return $getParkedVehicleIncomeData;
        }
    }

?>