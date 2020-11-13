<?php
    require __DIR__.'/includes/vendor/fpdf/fpdf/src/Fpdf/Fpdf.php';
    require "create_parkedVehicleTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_employeeTable.php";

    use Fpdf\Fpdf;

    $fromDate = $_GET["FromDate"];
    $endDate = $_GET["EndDate"];
    $parkingId = $_GET["ParkingId"];
    $employeeId = $_GET["HistoryIssuerEmployeeId"];
    $parkingName = null;
    $parkingOwnerName = null;
    $parkingAddress = null;
    $generatedParkingId = null;
    $statementDate = null;
    $historyIssuerName = null;
    $historyIssuerRole = null;
    $totalEarning = 00;
    $parkedVehicleList = array();

    class myPDF extends FPDF{
        function header(){
            $this -> Image('duepark.png', 130, 6);
            $this -> SetFont('Times', 'B', 20);
            $this -> Cell(376, 10, 'Duepark Technology Pvt Ltd', 0, 0, 'C');
            $this -> Ln(20);
            
        }

        function footer(){
            $this -> SetY(-15);
            $this -> SetFont('Arial', '', 8);
            $this -> Cell(0,10,'Page '.$this->PageNo().'/{nb}', 0, 0, 'C');
        }

        function parkingDetail(){
            global $parkingName, $generatedParkingId, $parkingOwnerName, $historyIssuerName, $historyIssuerRole, $statementDate, $parkingAddress, $totalEarning;
            $this -> SetFont('Times', 'B', 14);
            $this -> Cell(10, 10, $parkingName, 0, 0, 'L');
            $this -> Ln();
            $this -> SetFont('Times', '', 13);
            $this -> Cell(10, 7, 'Owner Name: '.$parkingOwnerName, 0, 0, 'L');
            $this -> Ln();
            $this -> Cell(10, 7, $generatedParkingId, 0, 0, 'L');
            $this -> Ln();
            $this -> Cell(10, 7, $parkingAddress, 0, 0, 'L');
            $this -> Ln();
            $this -> Cell(10, 7, 'Statement Date: '.$statementDate, 0, 0, 'L');
            $this -> Ln();
            $this -> Cell(10, 7, $historyIssuerRole." : ".$historyIssuerName, 0,0, 'L');
            $this -> Ln();
            $this -> Cell(10,7, 'Total Earning : '.$totalEarning, 0,0, 'L');
            $this -> Ln(20);
        }

        function headerTable(){
            $this->SetFont('Times', 'B', 10);
            $this->Cell(10,10, 'S.No', 1,0,'C');
            $this->Cell(35,10, 'Parked Date & Time', 1,0,'C');
            $this->Cell(35,10, 'Release Date & Time', 1,0,'C');
            $this->Cell(25,10, 'Vehicle Type', 1,0,'C');
            $this->Cell(28,10, 'Vehicle Number', 1,0,'C');
            $this->Cell(28,10, 'Contact Number', 1,0,'C');
            $this->Cell(40,10, 'Cash Payment', 1,0,'C');
            $this->Cell(40,10, 'Online Payment', 1,0,'C');
            $this->Cell(25,10, 'Total Payment', 1,0,'C');
            //$this->Cell(23,10, 'Parked Hour', 1,0,'C');
            $this->Cell(38,10, 'Parked By', 1,0,'C');
            $this->Cell(38,10, 'Released By', 1,0,'C');
            $this->Ln();
        }
        
        function viewTable(){
            global $parkedVehicleList;
            $this->SetFont('Times', '', 10);
            for($i=0;$i< count($parkedVehicleList);$i++){
                foreach($parkedVehicleList[$i] as $key => $value){
                    if($key == "S.No"){
                        $this->Cell(10,10, $value, 1,0,'C');
                    }
                    else if($key == "ParkedDate_Time"){
                        $this->Cell(35,10, $value, 1,0,'C');
                    }
                    else if($key == "ReleasedDate_Time"){
                        $this->Cell(35,10, $value, 1,0,'C');
                    }
                    else if($key == "VehicleType"){
                        $this->Cell(25,10, $value, 1,0,'C');
                    }
                    else if($key == "VehicleNumber"){
                        $this->Cell(28,10, $value, 1,0,'C');
                    }
                    else if($key == "ContactNumber"){
                        $this->Cell(28,10, $value, 1,0,'C');
                    }
                    else if($key == "CashPayment"){
                        $this->Cell(40,10, $value, 1, 0,'C'); 
                    }
                    else if($key == "OnlinePayment"){
                        $this->Cell(40,10, $value, 1,0,'C');
                    }
                    else if($key == "TotalParkedVehiclePayment"){
                        $this->Cell(25,10, $value, 1,0,'C');
                    }
                    // else if($key == "VehicleType"){
                    //     $this->Cell(23,10, 'Parked Hour', 1,0,'C');
                    // }
                    else if($key == "ParkedBy"){
                        $this->Cell(38,10, $value, 1,0,'C');
                    }
                    else if($key == "ReleasedBy"){
                        $this->Cell(38,10, $value, 1,0,'C');
                    }
                }
                $this->Ln();
            }
        }
    }

    if(!empty($fromDate) && !empty($endDate) && !empty($parkingId) && !empty($employeeId)){
        $statementDate = date("d F, Y", strtotime($fromDate))." to ".date("d F, Y", strtotime($endDate));
        getParkingData();
        getParkingOwnerName();
        getHistoryIssuerDetail();
        getParkedVehicleList();
        
        $pdf = new myPDF();
        $pdf->AliasNbPages();
        $pdf->AddPage('L','Legal',0);
        $pdf->parkingDetail();
        $pdf->headerTable();
        $pdf->viewTable();
        $pdf->Output('', 'duepark_parkedVehicleHistory.pdf', '');
    }

    function getParkedVehicleList(){
        global $connection, $parkingId, $fromDate, $endDate, $parkedVehicleTable, $parkedVehicleList, $totalEarning;

        $getParkedVehicleList_query = "select * from ".$parkedVehicleTable." where ParkingId = $parkingId and (ParkedDate between '$fromDate' and '$endDate')";
        $getParkedVehicleList_result = mysqli_query($connection, $getParkedVehicleList_query);
        if(!$getParkedVehicleList_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getParkedVehicleList_result)>0){
                $count = 0;
                while($row = mysqli_fetch_assoc($getParkedVehicleList_result)){
                    $cashPayment = null;
                    $onlinePayment = null;
                    $totalParkedVehicleEarning = 00;
                    // if(!empty($row["ReleasedTime"])){
                    //     $parkedHour = round(abs(strtotime($row["ReleasedTime"])-strtotime($row["ParkedTime"]))/3600,2);
                    // }
                    $parkedDate_Time = $row["ParkedDate"]." ".$row["ParkedTime"];
                    if(empty($row["ReleasedDate"])){
                        $releasedDate_Time = "N/A";
                    }
                    else{
                        $releasedDate_Time = $row["ReleasedDate"]." ".$row["ReleasedTime"];
                    }
                    if(empty($row["MonthlyPassId"])){
                        if($row["IsParkingFree"]=="1"){
                            if($row["ParkedPaymentType"]=="null"){
                                $cashPayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"];
                                $onlinePayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"];
                                $totalParkedVehicleEarning = $totalParkedVehicleEarning + $row["PaidAmount"];
                            }
                            if($row["ReleasedPaymentType"]== "null"){
                                $cashPayment = $cashPayment."\n Rs.".$row["ReleasedAmount"]." ".$row["ReleasedBy"];
                                $onlinePayment = $onlinePayment."\n Rs.".$row["ReleasedAmount"]." ".$row["ReleasedBy"];
                                $totalParkedVehicleEarning = $totalParkedVehicleEarning + $row["ReleasedAmount"];
                            }
                        }
                        else{
                            if($row["IsPayLater"]=="1"){
                                if(!empty($row["ReleasedPaymentType"])){
                                    if($row["ReleasedPaymentType"]=="Cash"){
                                        $cashPayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"]."\n Rs.".$row["ReleasedAmount"]." ".$row["ReleasedBy"];
                                    }
                                    else{
                                        $onlinePayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"]."\n Rs.".$row["ReleasedAmount"]." ".$row["ReleasedBy"];
                                    }
                                    $totalParkedVehicleEarning = $totalParkedVehicleEarning + $row["ReleasedAmount"] + $row["PaidAmount"];
                                }
                                else{
                                    $cashPayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"];
                                    $onlinePayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"];
                                    $totalParkedVehicleEarning = $totalParkedVehicleEarning + $row["PaidAmount"];
                                }
                            }
                            else{
                                if(!empty($row["ParkedPaymentType"])){
                                    if($row["ParkedPaymentType"]=="Cash"){
                                        $cashPayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"];
                                        $onlinePayment = "Rs.00 ".$row["ParkedBy"];
                                    }
                                    else{
                                        $onlinePayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"];
                                        $cashPayment = "Rs.00 ".$row["ParkedBy"];
                                    }
                                    $totalParkedVehicleEarning = $totalParkedVehicleEarning + $row["PaidAmount"];
                                }
                                if(!empty($row["ReleasedPaymentType"])){
                                    if($row["ReleasedPaymentType"]=="Cash"){
                                        $cashPayment = $cashPayment."\n Rs.".$row["ReleasedAmount"]." ".$row["ReleasedBy"];
                                    }
                                    else{
                                        $onlinePayment = $onlinePayment."\n Rs.".$row["ReleasedAmount"]." ".$row["ReleasedBy"];
                                    }
                                    $totalParkedVehicleEarning = $totalParkedVehicleEarning + $row["ReleasedAmount"];
                                }
                            }
                        }
                    }
                    else{
                        $cashPayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"];
                        $onlinePayment = "Rs.".$row["PaidAmount"]." ".$row["ParkedBy"];
                        if(!empty($row["ReleasedBy"])){
                            $cashPayment = $cashPayment."\n Rs.".$row["ReleasedAmount"]." ".$row["ReleasedBy"];
                            $onlinePayment = $onlinePayment."\n Rs.".$row["ReleasedAmount"]." ".$row["ReleasedBy"]; 
                        }
                        $totalParkedVehicleEarning = $totalParkedVehicleEarning + $row["ReleasedAmount"] + $row["PaidAmount"];
                    }
                    
                    if(empty($row["ReleasedBy"])){
                        $releasedBy = "N/A";
                    }
                    else{
                        $releasedBy = $row["ReleasedBy"];
                    }
                    $totalEarning = $totalEarning + $totalParkedVehicleEarning;
                    $count = $count + 1;
                    array_push($parkedVehicleList, array("S.No"=>$count,"ParkedDate_Time"=>$parkedDate_Time, "ReleasedDate_Time"=>$releasedDate_Time, 
                            "VehicleType"=>$row["VehicleType"], "VehicleNumber"=>strtoupper($row["VehicleNumber"]), "ContactNumber"=>$row["MobileNumber"],
                            "CashPayment"=>$cashPayment, "OnlinePayment"=>$onlinePayment, "TotalParkedVehiclePayment"=>"Rs.".$totalParkedVehicleEarning, 
                            "ParkedBy"=>$row["ParkedBy"], "ReleasedBy"=>$releasedBy));
                }
            }
        }
    }

    function getHistoryIssuerDetail(){
        global $connection, $historyIssuerName, $historyIssuerRole, $employeeEntityRoleMappingTable, $employeeTable, $employeeId;
        $getHistoryIssuerName_query = "select EmployeeName from ".$employeeTable." where id = $employeeId limit 1";
        $getHistoryIssuerName_result = mysqli_query($connection, $getHistoryIssuerName_query);
        if(!$getHistoryIssuerName_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getHistoryIssuerName_result) == 1){
                $row = mysqli_fetch_assoc($getHistoryIssuerName_result);
                $historyIssuerName = $row["EmployeeName"];
            }
        }

        $getHistoryIssuerRoleId_query = "select RoleId from ".$employeeEntityRoleMappingTable." where EmployeeId = $employeeId limit 1";
        $getHistoryIssuerRoleId_result = mysqli_query($connection, $getHistoryIssuerRoleId_query);
        if(!$getHistoryIssuerRoleId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getHistoryIssuerRoleId_result) == 1){
                $row = mysqli_fetch_assoc($getHistoryIssuerRoleId_result);
                $employeeRoleId = $row["RoleId"];
                if($employeeRoleId == 1){
                    $historyIssuerRole = "SuperAdmin";
                }
                else if($employeeRoleId == 2){
                    $historyIssuerRole = "Admin";
                }
                else if($employeeRoleId == 3){
                    $historyIssuerRole = "Manager";
                }
                else if($employeeRoleId == 4){
                    $historyIssuerRole = "Valet";
                }
                else if($employeeRoleId == 5){
                    $historyIssuerRole == "Sale";
                }
            }
        }
    }

    function getParkingOwnerName(){
        global $connection, $parkingOwnerName, $employeeEntityRoleMappingTable, $parkingEmployeeMappingTable, $parkingId, $employeeTable;
        $parkingOwnerId = null;
        $roleId = getRoleId();
        $getAllEmployee_query = "select ConsumerAppEmployeeId from ".$parkingEmployeeMappingTable." where ParkingId = $parkingId";
        $getAllEmployee_result = mysqli_query($connection, $getAllEmployee_query);
        if(!$getAllEmployee_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getAllEmployee_result)>0){
                while($row = mysqli_fetch_assoc($getAllEmployee_result)){
                    $consumerAppEmployeeId = $row["ConsumerAppEmployeeId"];
                    if(!empty($consumerAppEmployeeId)){
                        $employeeIsOwner_query = "select * from ".$employeeEntityRoleMappingTable." where EmployeeId = $consumerAppEmployeeId and RoleId = $roleId limit 1";
                        $employeeIsOwner_result = mysqli_query($connection, $employeeIsOwner_query);
                        if(!$employeeIsOwner_result){
                            echo "Error retrieving record '".mysqli_error($connection)."'";
                        }
                        else{
                            if(mysqli_num_rows($employeeIsOwner_result) == 1){
                                $parkingOwnerId = $consumerAppEmployeeId;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(!empty($parkingOwnerId)){
            $getParkingOwnerName_query = "select EmployeeName from ".$employeeTable." where id = $parkingOwnerId limit 1";
            $getParkingOwnerName_result = mysqli_query($connection, $getParkingOwnerName_query);
            if(!$getParkingOwnerName_result){
                echo "Error retrieving record '".mysqli_error($connection)."'";
            }
            else{
                if(mysqli_num_rows($getParkingOwnerName_result) == 1){
                    $row = mysqli_fetch_assoc($getParkingOwnerName_result);
                    $parkingOwnerName = $row["EmployeeName"];
                }
            }
        }
    }

    function getParkingData(){
        global $connection, $parkingName, $generatedParkingId, $parkingId, $parkingTable, $parkingId, $parkingAddress;
        $query = "Select ParkingId, ParkingAcronym, ParkingAddress, ParkingName from ".$parkingTable." where id = $parkingId limit 1";
        $result = mysqli_query($connection, $query);
        if(!$result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($result) == 1){
                $row = mysqli_fetch_assoc($result);
                $parkingName = $row["ParkingName"];
                $parkingAddress = $row["ParkingAddress"];
                $generatedParkingId = $row["ParkingAcronym"].sprintf("%03d", $row["ParkingId"]);
            }
        }
    }

    function getRoleId(){
        global $connection, $employeeRoleTable;

        $roleId_query = "select id from ".$employeeRoleTable." where Role = 'SuperAdmin'";
        $roleId_result = mysqli_query($connection,$roleId_query);
        if(!$roleId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        } 
        else
        {
            $row = mysqli_fetch_assoc($roleId_result);
            return $row['id'];
        }
    }
?>