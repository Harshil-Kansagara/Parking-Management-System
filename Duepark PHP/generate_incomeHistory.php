<?php

    require __DIR__.'/includes/vendor/fpdf/fpdf/src/Fpdf/Fpdf.php';
    //require "create_monthlyPassTable.php";
    require "create_vehicleNumberMonthlyPassMappingTable.php";
    require "create_parkedVehicleTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_admin_managerValetEmployeeMappingTable.php";
    require "create_employeeAttendanceTable.php";
    //require "create_employeeTable.php";
    //require "create_locationParkingMappingTable.php";

    use Fpdf\Fpdf;

    $fromDate = $_GET["FromDate"];
    $endDate = $_GET["EndDate"];
    $parkingId = $_GET["ParkingId"];
    $historyIssuerEmployeeId = $_GET["HistoryIssuerEmployeeId"];
    $parkingName = null;
    $parkingOwnerName = null;
    $parkingAddress = null;
    $generatedParkingId = null;
    $statementDate = null;
    $historyIssuerName = getEmployeeName($historyIssuerEmployeeId);
    $historyIssuerRole = getEmployeeRole($historyIssuerEmployeeId);;
    $sortEmployeesIncomeData = array();
    $totalEarning = 00;
    $serialCounter = 0;

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
            $this->Cell(30,10, 'Date', 1,0,'C');
            $this->Cell(40,10, 'Employee', 1,0,'C');
            $this->Cell(28,10, 'Login Time', 1,0,'C');
            $this->Cell(28,10, 'Logout Time', 1,0,'C');
            $this->Cell(25,10, 'Worked Hour', 1,0,'C');
            $this->Cell(25,10, 'Car Quantiy', 1,0,'C');
            $this->Cell(25,10, 'Bike Quantity', 1,0,'C');
            $this->Cell(40,10, 'Online Payment', 1,0,'C');
            $this->Cell(40,10, 'Cash Payment', 1,0,'C');
            $this->Cell(28,10, 'Total Day Earn', 1,0,'C');
            $this->Ln();
        }
        
        function viewTable(){
            global $sortEmployeesIncomeData;
            $this->SetFont('Times','',10);
            for($i=0;$i<count($sortEmployeesIncomeData);$i++){
                foreach($sortEmployeesIncomeData[$i] as $key => $value){
                    if($key == "S.No"){
                        $this->Cell(10,10, $value, 1,0,'C');
                    }
                    else if($key == "Date"){
                        $this->Cell(30,10, $value, 1,0,'C');
                    }
                    else if($key == "EmployeeName"){
                        $this->Cell(40,10, $value, 1,0,'C');
                    }
                    else if($key == "LoginTime"){
                        $this->Cell(28,10, $value, 1,0,'C');
                    }
                    else if($key == "LogoutTime"){
                        $this->Cell(28,10, $value, 1,0,'C');
                    }
                    else if($key == "WorkingHour"){
                        $this->Cell(25,10, $value, 1,0,'C');
                    }
                    else if($key == "CarQuantity"){
                        $this->Cell(25,10, $value, 1,0,'C');
                    }
                    else if($key == "BikeQuantity"){
                        $this->Cell(25,10, $value, 1,0,'C');
                    }
                    else if($key == "OnlinePayment"){
                        $this->Cell(40,10, $value, 1,0,'C');
                    }
                    else if($key == "CashPayment"){
                        $this->Cell(40,10, $value, 1,0,'C');
                    }
                    else if($key == "TotalDayEarn"){
                        $this->Cell(28,10, $value, 1,0,'C');
                    }
                }
                $this->Ln();
            }
        }
    }

    if(!empty($fromDate) && !empty($endDate) && !empty($parkingId) && !empty($historyIssuerEmployeeId)){
        $statementDate = date("d F, Y", strtotime($fromDate))." to ".date("d F, Y", strtotime($endDate));
        getParkingData();
        getParkingOwnerName();
        getEmployeeList();

        $pdf = new myPDF();
        $pdf->AliasNbPages();
        $pdf->AddPage('L','Legal',0);
        $pdf->parkingDetail();
        $pdf->headerTable();
        $pdf->viewTable();
        $pdf->Output('', 'duepark_incomeHistory.pdf', '');          
    }

    function getEmployeeList(){
        global $connection, $historyIssuerEmployeeId, $admin_managerValetEmployeeMappingTable, $historyIssuerRole, $employeesIncomeData;
        $valetsIncomeData = array();
        $employeesIncomeData = array();
        if($historyIssuerEmployeeId!=0){
            if($historyIssuerRole == "Valet"){
                $employeesIncomeData = getEmployeeIncomeData($historyIssuerEmployeeId);
            }
            else if($historyIssuerRole == "Manager"){
                $managerIncomeData = getEmployeeIncomeData($historyIssuerEmployeeId);
                $getEmployeeIdList_query = "select ValetId from ".$admin_managerValetEmployeeMappingTable." where Admin_ManagerId=$historyIssuerEmployeeId";
                $getEmployeeIdList_result = mysqli_query($connection, $getEmployeeIdList_query);
                if(!$getEmployeeIdList_result){
                    echo "Error retrieving record '".mysqli_error($connection)."'";    
                }
                else{
                    if(mysqli_num_rows($getEmployeeIdList_result)>0){
                        while($row = mysqli_fetch_assoc($getEmployeeIdList_result)){
                            $employeeId = $row["ValetId"];
                            $valetsIncomeData = array_merge($valetsIncomeData,getEmployeeIncomeData($employeeId));
                        }
                    }
                    $employeesIncomeData = array_merge($managerIncomeData, $valetsIncomeData);
                }
            }
            else {
                $employeesIncomeData = getAllEmployeeAssociatedWithParkingIncomeData();
            }
        }
        else{
            $employeesIncomeData = getAllEmployeeAssociatedWithParkingIncomeData();
        }

        sortFunction($employeesIncomeData);
        //echo json_encode($employeesIncomeData);
    }

    function getDateList(){
        global $fromDate, $endDate;
        $dateList = array();
        $variable1 = strtotime($fromDate);
        $variable2 = strtotime($endDate);

        for ($currentDate = $variable1; $currentDate <= $variable2;  
                                $currentDate += (86400)) 
        {                               
            $store = date('d-m-Y', $currentDate); 
            $dateList[] = $store; 
        }
        return $dateList;
    }

    function sortFunction($employeesIncomeData){
        global $sortEmployeesIncomeData;
        $dateList = getDateList();
        for($i=0;$i<count($dateList);$i++){
            for($j=0;$j<count($employeesIncomeData);$j++){
                foreach($employeesIncomeData[$j] as $key => $value){
                    if($key == "Date"){
                        if($value == $dateList[$i]){
                            array_push($sortEmployeesIncomeData, $employeesIncomeData[$j]);
                            break;
                        }
                    }
                }
            }
        }
        //echo json_encode($sortEmployeesIncomeData);
    }

    function getAllEmployeeAssociatedWithParkingIncomeData(){
        $allEmployeeIncomeData = array();
        global $connection, $parkingId, $parkingEmployeeMappingTable;
        $getAllEmployeeList_query = "select ConsumerAppEmployeeId from ".$parkingEmployeeMappingTable." where ParkingId = $parkingId";
        $getAllEmployeeList_result = mysqli_query($connection, $getAllEmployeeList_query);
        if(!$getAllEmployeeList_result){
            echo "Error retrieving record '".mysqli_error($connection)."'"; 
        }
        else{
            if(mysqli_num_rows($getAllEmployeeList_result)>0){
                while($row = mysqli_fetch_assoc($getAllEmployeeList_result)){
                    if(!empty($row["ConsumerAppEmployeeId"])){
                        $incomeData = getEmployeeIncomeData($row["ConsumerAppEmployeeId"]);
                        if(count($incomeData)>0){
                            $allEmployeeIncomeData=array_merge($allEmployeeIncomeData, $incomeData);
                        }
                    }
                }
                return $allEmployeeIncomeData;
            }
            //echo json_encode($allEmployeeIncomeData);
        }

    }

    function getEmployeeIncomeData($employeeId){
        global $connection, $parkedVehicleTable, $serialCounter, $fromDate, $endDate, $totalEarning, $parkingId, $historyIssuerRole, $historyIssuerEmployeeId;
        $employeeIncomeData = array();
        $employeeName = getEmployeeName($employeeId);
        $employeeRole = getEmployeeRole($employeeId);
        if($employeeId != $historyIssuerEmployeeId){    
            if($historyIssuerRole == "Admin"){
                if($employeeRole == "SuperAdmin" || $employeeRole == "Admin"){
                    return array();
                }
            }
        }

        if($employeeRole == "Admin"){
            $employeeName = $employeeName."(A)";
        }
        else if($employeeRole == "Manager")
        {
            $employeeName = $employeeName."(M)";
        }
        else if($employeeRole == "Valet"){
            $employeeName = $employeeName."(V)";
        }
        else{
            $employeeName = $employeeName."(SA)";
        }

        $getParkedVehiclePaymentDetail_query = "select VehicleType, PaidAmount, ParkedPaymentType, ParkedDate, ReleasedDate, ParkedEmployeeId, ReleasedEmployeeId,
                        ReleasedAmount, ReleasedPaymentType from ".$parkedVehicleTable." where ParkingId = $parkingId and (ParkedDate between '$fromDate' and '$endDate')";
        $getParkedVehiclePaymentDetail_result = mysqli_query($connection, $getParkedVehiclePaymentDetail_query);
        if(!$getParkedVehiclePaymentDetail_result){
            echo "Error retrieving record '".mysqli_error($connection)."'"; 
        }
        else{
            if(mysqli_num_rows($getParkedVehiclePaymentDetail_result)>0){
                $count = 0;
                $lastParkedDate = null;
                while($row = mysqli_fetch_assoc($getParkedVehiclePaymentDetail_result)){
                    $parkedDate = $row["ParkedDate"];
                    $monthlyPassData = getMonthlyPassEmployeeIncomeDate($parkedDate, $employeeId);
                    $onlinePayment = $monthlyPassData["OnlinePayment"];
                    $cashPayment = $monthlyPassData["CashPayment"];
                    $carQuantity = $monthlyPassData["CarQuantity"];
                    $bikeQunatity = $monthlyPassData["BikeQuantity"];
                    $totalDayEarn = 00;
                    $count = $count + 1;
                    if($parkedDate!=$lastParkedDate || empty($lastParkedDate)){
                        mysqli_data_seek($getParkedVehiclePaymentDetail_result, 0);
                        while($row1 = mysqli_fetch_assoc($getParkedVehiclePaymentDetail_result)){
                            $isVehicleAdded = false;
                            if($employeeId == $row1["ParkedEmployeeId"]){
                                if($parkedDate == $row1["ParkedDate"]){
                                    if(!empty($row1["ParkedPaymentType"]) && $row1["ParkedPaymentType"]!="null"){
                                        if($row1["ParkedPaymentType"] == "Cash"){
                                            $cashPayment = $cashPayment + $row1["PaidAmount"];
                                        }
                                        else if($row1["ParkedPaymentType"] == "Online"){
                                            $onlinePayment = $onlinePayment + $row1["PaidAmount"];
                                        }
                                    }    
                                    if($row1["VehicleType"]=="Car"){
                                        $carQuantity = $carQuantity + 1;
                                        $isVehicleAdded = true;
                                    }
                                    else{
                                        $bikeQunatity = $bikeQunatity + 1;
                                        $isVehicleAdded = true;
                                    }
                                }
                            }
                            if(!empty($row1["ReleasedEmployeeId"]) && $employeeId == $row1["ReleasedEmployeeId"]){
                                if($parkedDate == $row1["ReleasedDate"]){
                                    if(!empty($row1["ReleasedPaymentType"])){
                                        if($row1["ReleasedPaymentType"]== "Cash"){
                                            $cashPayment = $cashPayment + $row1["ReleasedAmount"];
                                        }
                                        else if($row1["ReleasedPaymentType"] == "Online"){
                                            $onlinePayment = $onlinePayment + $row1["ReleasedAmount"];
                                        }
                                    }
                                    if(!$isVehicleAdded){
                                        if($row1["VehicleType"]=="Car"){
                                            $carQuantity = $carQuantity + 1;
                                        }
                                        else{
                                            $bikeQunatity = $bikeQunatity + 1;
                                        }
                                    }
                                }
                            }
                            
                            $lastParkedDate = $parkedDate;
                            $totalDayEarn = $cashPayment + $onlinePayment;
                        }
                        $totalEarning = $totalEarning + $totalDayEarn;
                        $serialCounter = $serialCounter + 1;
                        $employeeAttendanceData = getEmployeeAttendance($employeeId, $parkedDate);
                        mysqli_data_seek($getParkedVehiclePaymentDetail_result, $count);
                        if($employeeRole == "Admin" || $employeeRole == "SuperAdmin"){
                            $loggedInTime = "N/A";
                            $loggedOutTime = "N/A";
                            $workingHour = "N/A";
                        }
                        else{
                            if(!empty($employeeAttendanceData["LoggedInTime"])){
                                $loggedInTime = $employeeAttendanceData["LoggedInTime"];
                            }
                            
                            if(!empty($employeeAttendanceData["LoggedOutTime"])){
                                if(date("d-m-Y") == $employeeAttendanceData["LoggedOutDate"]){
                                    $loggedOutTime = $employeeAttendanceData["LoggedOutTime"];
                                    $workingHour = gmdate("H:i", strtotime($loggedOutTime)-strtotime($loggedInTime));
                                }
                                else{
                                    $loggedOutTime = "N/A";
                                    $workingHour = "N/A";
                                }
                            }
                        }
                        array_push($employeeIncomeData, array("S.No"=>$serialCounter,"Date"=>$parkedDate,"EmployeeName"=>$employeeName, "LoginTime"=>$loggedInTime, "LogoutTime"=>$loggedOutTime,"WorkingHour"=>$workingHour,
                        "CarQuantity"=>$carQuantity, "BikeQuantity"=>$bikeQunatity, "OnlinePayment"=>$onlinePayment, "CashPayment"=>$cashPayment, "TotalDayEarn"=>$totalDayEarn));
                    }
                }
            }
            return $employeeIncomeData;
            //echo json_encode($employeeIncomeData);
        }
    }

    function getEmployeeAttendance($employeeId, $date){
        global $connection, $employeeAttendanceTable;
        $getEmployeeAttendance_query = "select LoggedInTime, LoggedOutDate, LoggedOutTime from ".$employeeAttendanceTable." where EmployeeId = $employeeId and LoggedInDate = '$date' ";
        $getEmployeeAttendance_result = mysqli_query($connection, $getEmployeeAttendance_query);
        if(!$getEmployeeAttendance_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getEmployeeAttendance_result)>0){
                while($row = mysqli_fetch_assoc($getEmployeeAttendance_result)){
                    return array("LoggedInTime"=>$row["LoggedInTime"], "LoggedOutDate"=>$row["LoggedOutDate"], "LoggedOutTime"=>$row["LoggedOutDate"]);
                }
            }
            else{
                return array();
            }
        }
    }

    function getMonthlyPassEmployeeIncomeDate($date, $employeeId){
        global $connection, $monthlyPassTable, $vehicleNumberMonthlyPassMappingTable;
        $getMonthlyPassList_query = "select id, PayableAmount, PayableType from ".$monthlyPassTable." where IssuedEmployeeId = $employeeId and IssuedDate = '$date'";
        $getMonthlyPassList_result = mysqli_query($connection, $getMonthlyPassList_query);
        if(!$getMonthlyPassList_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            $onlinePayment = 00;
            $cashPayment = 00;
            $carQuantity = 00;
            $bikeQunatity = 00;
            if(mysqli_num_rows($getMonthlyPassList_result)>0){
                while($row = mysqli_fetch_assoc($getMonthlyPassList_result)){
                    if($row["PayableType"]=="Cash"){
                        $cashPayment = $cashPayment + $row["PayableAmount"];
                    }
                    else{
                        $onlinePayment = $onlinePayment + $row["PayableAmount"];
                    }
                    $monthlyPassId = $row["id"];
                    $getVehicleType_query = "select VehicleType from ".$vehicleNumberMonthlyPassMappingTable." where MonthlyPassId = $monthlyPassId limit 1";
                    $getVehicleType_result = mysqli_query($connection, $getVehicleType_query);
                    if(!$getVehicleType_result){
                        echo "Error retrieving record '".mysqli_error($connection)."'";
                    }
                    else
                    {
                        if(mysqli_num_rows($getVehicleType_result)==1){
                            $row = mysqli_fetch_assoc($getVehicleType_result);
                            if($row["VehicleType"]=="Car"){
                                $carQuantity = $carQuantity + 1;
                            }
                            else{
                                $bikeQunatity = $bikeQunatity + 1;
                            }
                        }
                    }
                }
            }
            return array("CarQuantity"=>$carQuantity, "BikeQuantity"=>$bikeQunatity, "OnlinePayment"=>$onlinePayment, "CashPayment"=>$cashPayment);
        }
    }

    function getEmployeeName($employeeId){
        global $connection, $employeeTable;
        $getEmployeeName_query = "select EmployeeName from ".$employeeTable." where id = $employeeId limit 1";
        $getEmployeeName_result = mysqli_query($connection, $getEmployeeName_query);
        if(!$getEmployeeName_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else
        {
            if(mysqli_num_rows($getEmployeeName_result) == 1){
                $row = mysqli_fetch_assoc($getEmployeeName_result);
                return $row["EmployeeName"];
            }
        }
    }

    function getEmployeeRole($employeeId){
        global $connection, $employeeRoleTable, $employeeEntityRoleMappingTable;

        $getEmployeeRoleId_query = "select RoleId from ".$employeeEntityRoleMappingTable." where EmployeeId=$employeeId limit 1";
        $getEmployeeRoleId_result = mysqli_query($connection, $getEmployeeRoleId_query);
        if(!$getEmployeeRoleId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getEmployeeRoleId_result)==1){
                $row = mysqli_fetch_assoc($getEmployeeRoleId_result);
                $roleId = $row["RoleId"];
                $getRoleName_query = "select Role from ".$employeeRoleTable." where id = $roleId limit 1";
                $getRoleName_result = mysqli_query($connection, $getRoleName_query);
                if(!$getRoleName_result){
                    echo "Error retrieving record '".mysqli_error($connection)."'";
                }
                else{
                    if(mysqli_num_rows($getRoleName_result) == 1){
                        $row1= mysqli_fetch_assoc($getRoleName_result);
                        return $row1["Role"];
                    }
                }
            }
        }
    }

    function getParkingOwnerName(){
        global $connection, $parkingOwnerName, $employeeEntityRoleMappingTable, $parkingEmployeeMappingTable, $parkingId;
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

        $parkingOwnerName = getEmployeeName($parkingOwnerId);
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