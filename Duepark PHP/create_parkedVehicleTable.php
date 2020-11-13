<?php

    require "init.php";
    require "create_locationTable.php";
    require "create_parkingTable.php";
    //require "create_monthlyPassTable.php";

    $parkedVehicleTable = "parkedvehicle";

    $sql_query="SHOW TABLES LIKE '".$parkedVehicleTable."'";
    
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createParkedVehicleTable(); 
    }

    function createParkedVehicleTable(){
        global $parkedVehicleTable, $connection;

        $sql_query = "CREATE TABLE ".$parkedVehicleTable. "(
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            ParkingId int(200) NOT NULL,
            LocationId int(200) NOT NULL,
            MonthlyPassId int(200) NULL,
            GeneratedParkedVehicleId int(200) NULL,
            FullName Varchar(255) NOT NULL,
            MobileNumber Varchar(255) NOT NULL,
            VehicleType Varchar(255) NOT NULL,
            VehicleNumber Varchar(255) NOT NULL,
            PaidAmount varchar(255) NULL,
            ParkedPaymentType varchar(255) NULL,
            PaymentTimeRate varchar(255) NULL,
            IsPayLater boolean NULL,
            IsParkingFree boolean NULL,
            ParkedEmployeeId int(200) NOT NULL,
            ParkedBy Varchar(255) NOT NULL,
            ParkedTime Varchar(255) NOT NULL,
            ParkedDate Varchar(255) NOT NULL,
            ReleasedAmount varchar(255) NULL, 
            ReleasedTime Varchar(255) NULL,
            ReleasedDate varchar(255) NULL,
            ReleasedEmployeeId int(200) NULL,
            ReleasedBy varchar(255) NULL,
            ReleasedPaymentType varchar(255) NULL,
            VehicleNumberPic varchar(255) NOT NULL,
            IsParkedVehicleReleased boolean NOT NULL,
            PenaltyCharges int(200) NULL,
            FOREIGN KEY (ParkingId) references parking(id),
            FOREIGN KEY (LocationId) references location(id),
            FOREIGN KEY (MonthlyPassId) references monthlypass(id),
            FOREIGN KEY (ParkedEmployeeId) references employee(id),
            FOREIGN KEY (ReleasedEmployeeId) references employee(id)
        )";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }
?>