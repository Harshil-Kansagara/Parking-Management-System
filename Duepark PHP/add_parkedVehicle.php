<?php

    require "init.php";
    //require "init_ftp.php";
    require "create_parkedVehicleTable.php";

    $locationId = $_POST["LocationId"];
    $monthlyPassId = $_POST["MonthlyPassId"];
    $generatedParkedVehicleId = $_POST["GeneratedParkedVehicleId"];
    $fullName = $_POST["FullName"];
    $mobileNumber = $_POST["MobileNumber"];
    $vehicleType = $_POST["VehicleType"];
    $vehicleNumber = strtolower($_POST["VehicleNumber"]);
    $paidAmount = $_POST["PaidAmount"];
    $parkedPaymentType = $_POST["ParkedPaymentType"];
    $parkedTimeRate = $_POST["ParkedTimeRate"];
    $isPayLater = $_POST["IsPayLater"];
    $isParkingFree = $_POST["IsParkingFree"];
    $parkedBy = $_POST["ParkedBy"];
    $parkedTime = $_POST["ParkedTime"];
    $parkedDate = $_POST["ParkedDate"];
    $parkingId = $_POST["ParkingId"];
    $vehicleNumberPic = $_POST["VehicleNumberPic"];
    $isParkedVehicleReleased = $_POST["IsParkedVehicleReleased"];
    $parkedEmployeeId = $_POST["ParkedEmployeeId"];
    $prevParkedVehicleId = $_POST["PrevParkedVehicleId"];
    $penaltyCharges = $_POST["PenaltyCharges"];

    // Add Parked EMployeeId field
    //if(!checkVehicleIsParkedAlready()){
        if($monthlyPassId == "null")
        {
            $addParkedVehicle_query = "insert into ".$parkedVehicleTable." (ParkingId, LocationId, GeneratedParkedVehicleId, 
                                        FullName, MobileNumber, VehicleType, VehicleNumber, PaidAmount, ParkedPaymentType, PaymentTimeRate, IsPayLater,
                                        IsParkingFree, ParkedEmployeeId, ParkedBy, ParkedTime, ParkedDate, VehicleNumberPic, IsParkedVehicleReleased) values ($parkingId, $locationId, $generatedParkedVehicleId,
                                        '$fullName', '$mobileNumber', '$vehicleType', '$vehicleNumber', '$paidAmount', '$parkedPaymentType',
                                        '$parkedTimeRate', $isPayLater, $isParkingFree, $parkedEmployeeId, '$parkedBy', '$parkedTime', '$parkedDate', '$vehicleNumber', $isParkedVehicleReleased)";
        }
        else
        {
            $addParkedVehicle_query = "insert into ".$parkedVehicleTable." (ParkingId, LocationId, MonthlyPassId, GeneratedParkedVehicleId, FullName, 
                                        MobileNumber, VehicleType, VehicleNumber, PaidAmount, ParkedEmployeeId, ParkedBy, ParkedTime, ParkedDate, VehicleNumberPic, IsParkedVehicleReleased) values 
                                        ($parkingId, $locationId, $monthlyPassId, $generatedParkedVehicleId,'$fullName', '$mobileNumber', '$vehicleType', '$vehicleNumber', '00',
                                        $parkedEmployeeId, '$parkedBy', '$parkedTime', '$parkedDate', '$vehicleNumber', $isParkedVehicleReleased)";   
        }
        $addParkedVehicle_result = mysqli_query($connection, $addParkedVehicle_query);

        if(!$addParkedVehicle_result){
            echo "Error creating record '".mysqli_error($connection)."'";
        }
        else{
            if($prevParkedVehicleId != "0"){
                $query = "update ".$parkedVehicleTable." set PenaltyCharges = $penaltyCharges, IsParkedVehicleReleased = true where id = $prevParkedVehicleId";
                $result = mysqli_query($connection, $query);
                if(!$result){
                    echo "Error updating record '" . mysqli_error($connection) . "'";
                }
            }

            //addParkedVehiclePic();
            $last_parkedVehicleId = mysqli_insert_id($connection);
            echo $last_parkedVehicleId;
        }
    // }
    // else{
    //     echo 0;
    // }

    function checkVehicleIsParkedAlready(){
        global $connection, $vehicleNumber, $parkedVehicleTable;
        $checkVehicleAlreadyParked_query = "select * from ".$parkedVehicleTable." where VehicleNumber = '$vehicleNumber' and IsParkedVehicleReleased=0 limit 1";
        $checkVehicleAlreadyParked_result = mysqli_query($connection, $checkVehicleAlreadyParked_query);
        if(!$checkVehicleAlreadyParked_result){
            echo "Error retrieving record '" . mysqli_error($connection) . "'";
        }
        else{
            if(mysqli_num_rows($checkVehicleAlreadyParked_result)==1){
                return true;
            }
            return false;
        }

    }

    function addParkedVehiclePic(){
        global $ftp_conn, $vehicleNumberPic, $vehicleNumber;
        $imagePath = "vehicleNumberPic/".$vehicleNumber.".png";
        if(ftp_chdir($ftp_conn, "duepark/"))
        {
            $numberPic = base64_decode($vehicleNumberPic);
            if(!is_dir("vehicleNumberPic"))
            {
                if(ftp_mkdir($ftp_conn, "vehicleNumberPic"))
                {
                    file_put_contents($imagePath,$numberPic);
                }
            }
            else
            {
                file_put_contents($imagePath,$numberPic);
            }
        }
        ftp_close($ftp_conn);
    }
?>