<?php
    require "init.php";
    require "init_ftp.php";
    require "create_employeeTable.php";
    //require "seed_roleTable.php";
    //require "seed_entityTable.php";
    require "create_notificationTable.php";
    require "create_employeeEntityRoleMappingTable.php";
    require "create_parkingEmployeeMappingTable.php";
    require "create_adminManagerEmployeeMappingTable.php";
    require "create_admin_managerValetEmployeeMappingTable.php";
    require "create_admin_managerSaleEmployeeMappingTable.php";
    require "create_employeeLocationMappingTable.php";
    require "create_adminAppEmployeeTokenTable.php";
    
    $employeeName = $_POST["EmployeeName"];
    $employeeMobileNumber = $_POST["EmployeeMobileNumber"];
    $employeePassword = $_POST["EmployeePassword"];
    $employeeRole = $_POST["EmployeeRole"];
    $employeeEntity = $_POST["EmployeeEntity"];
    
    $isEmployeeNewPasswordCreated = 0;
    if($employeeEntity == "SuperAdmin" && $employeeEntity == "ConsumerApp"){
        $isEmployeeNewPasswordCreated = 1;
    }

    if(isset($_POST["LoggedInEmployeeId"]))
    {
        $loggedInEmployeeId = $_POST["LoggedInEmployeeId"];
    }
    else{
        $loggedInEmployeeId = null;
    }

    if(isset($_POST["EmployeeState"]))
    {
        $employeeState = $_POST["EmployeeState"];
    }
    else
    {
        $employeeState = null;
    }

     if(isset($_POST["ParkingId"]))
     {
        $parkingId = $_POST["ParkingId"];
    }
    else{
        $parkingId = null;
    }

    if(isset($_POST["EmployeeCity"]))
    {
        $employeeCity = $_POST["EmployeeCity"];
    }
    else
    {
        $employeeCity = null;
    }
    
    if(isset($_POST["EmployeePhoneNumber"])){
        $employeePhoneNumber = $_POST["EmployeePhoneNumber"];
    }
    else{
        $employeePhoneNumber = null;
    }

    if(isset($_POST["EmployeeEmailId"]))
    {
        $employeeEmailId = $_POST["EmployeeEmailId"];
    }
    else{
        $employeeEmailId = null;
    }
    
    if(isset($_POST["EmployeeAdharNumber"])){
        $employeeAdharNumber = $_POST["EmployeeAdharNumber"];
    }
    else{
        $employeeAdharNumber = null;
    }

    if(isset($_POST["EmployeeProfilePic"]))
    {
        $employeeProfilePic = $_POST["EmployeeProfilePic"];
    }
    else{
        $employeeProfilePic = null;
    }
    
    if(isset($_POST["LocationId"])){
        $locationId = $_POST["LocationId"];
    }
    else{
        $locationId = null;
    }
    
    if(isset($_POST["VehicleType"])){
        $vehicleType = $_POST["VehicleType"];
    }
    else{
        $vehicleType = null;
    }

    if(isset($_POST["EmployeeActiveState"])){
        $employeeActiveState = $_POST["EmployeeActiveState"];
    }
    else{
        $employeeActiveState = null;
    }

    $entityId = getEntityId();
    $employeeId = 0;
    
    if($entityId != 0)
    {
        fetchAllEmployeeFromEmployeeEntityRoleMappingTable();
    }
    else
    {
        echo "NoEntityFound";    
    }

    function getEntityId(){
        global $connection, $entityTable, $employeeEntity;
        $entityId_query = "select id from ".$entityTable." where Entity = '$employeeEntity'"; 
        $entityId_result = mysqli_query($connection,$entityId_query);
        if(!$entityId_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
            return 0;
        } 
        else
        {
            $row = mysqli_fetch_assoc($entityId_result);
            return $row['id'];
        }
    }

    function fetchAllEmployeeFromEmployeeEntityRoleMappingTable(){
        global $connection, $employeeEntityRoleMappingTable, $entityId, $employeeMobileNumber, $employeeAdharNumber, $employeeEmailId, $employeeTable;

        $employeeList_query = "select EmployeeId from ".$employeeEntityRoleMappingTable." where EntityId = $entityId";
        $employeeList_result = mysqli_query($connection, $employeeList_query);
        
        if(!$employeeList_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else
        {
            if($employeeList_result->num_rows > 0)
            {                
                $isExist = false;
                $fetchEmployeeId = 0;
                while($row = mysqli_fetch_assoc($employeeList_result)){
                    $fetchEmployeeId = $row['EmployeeId'];
                    $employee_query = "select EmployeeMobileNumber, EmployeeAdharNumber, EmployeeEmailId from ".$employeeTable." where id = $fetchEmployeeId";
                    $employee_result = mysqli_query($connection, $employee_query);
    
                    if(!$employee_result){
                        echo "Error creating new record '".mysqli_error($connection)."'";
                    }
                    else
                    {
                        $employee_row = mysqli_fetch_assoc($employee_result);
                        if($employeeMobileNumber == $employee_row['EmployeeMobileNumber']){
                            $isExist = true;
                            break;
                        }
                        if($employeeAdharNumber != null && $employee_row['EmployeeAdharNumber'] != null)
                        {
                            if($employeeAdharNumber == $employee_row['EmployeeAdharNumber']){
                                $isExist = true;
                                break;
                            }
                        }
                        if($employeeEmailId != null && $employee_row['EmployeeEmailId'] != null)
                        {
                            if($employeeEmailId == $employee_row['EmployeeEmailId']){
                                $isExist = true;
                                break;
                            }
                        }
                    }
                }
                if($isExist){
                    echo "exist";
                }
                else{
                    addEmployee();
                }
            }
            else{
                addEmployee();
            }
        }
    }

    function addEmployee(){
        global $connection, $employeeTable, $employeeId, $employeeName, $employeeMobileNumber, $employeePassword, $employeeEmailId, $employeeEntity, $parkingId, $employeeRole, $locationId,
            $isEmployeeNewPasswordCreated, $employeeState, $employeeCity, $employeePhoneNumber, $employeeAdharNumber, $employeeProfilePic, $vehicleType, $employeeActiveState;

        if($employeeProfilePic != "null"){
            $adharNumber = str_replace(' ','',$employeeAdharNumber);
            $addEmployee_query = "insert into ".$employeeTable." (EmployeeName, EmployeeSate, EmployeeCity, EmployeeMobileNumber, EmployeePhoneNumber, 
            EmployeeEmailId, EmployeeAdharNumber, EmployeePassword, EmployeeProfilePic, VehicleType, IsEmployeeNewPasswordCreated, EmployeeActiveState) values ('$employeeName',
            '$employeeState', '$employeeCity', '$employeeMobileNumber', '$employeePhoneNumber','$employeeEmailId', '$employeeAdharNumber', '$employeePassword', 
            '$adharNumber', '$vehicleType', $isEmployeeNewPasswordCreated, $employeeActiveState)";
        }
        else
        {
            $addEmployee_query = "insert into ".$employeeTable." (EmployeeName, EmployeeSate, EmployeeCity, EmployeeMobileNumber, EmployeePhoneNumber, 
            EmployeeEmailId, EmployeeAdharNumber, EmployeePassword, EmployeeProfilePic, VehicleType, IsEmployeeNewPasswordCreated, EmployeeActiveState) values ('$employeeName',
            '$employeeState', '$employeeCity', '$employeeMobileNumber', '$employeePhoneNumber','$employeeEmailId', '$employeeAdharNumber', '$employeePassword', 
            null, '$vehicleType', $isEmployeeNewPasswordCreated, $employeeActiveState)";
        }
        
        $addEmployee_result = mysqli_query($connection,$addEmployee_query);
        if(!$addEmployee_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else{
            $employeeId = mysqli_insert_id($connection);
            
            addEmployeeEntityRoleMapping();

            if($employeeRole == "Manager"){
                addAdminManagerEmployeeMapping($employeeId);
            }

            if($employeeRole == "Valet"){
                add_Admin_ManagerValetEmployeeMapping($employeeId);
            }

            if($employeeRole == "Sale"){
                add_Admin_ManagerSaleEmployeeMapping($employeeId);
            }

            if($employeeEntity == "ConsumerApp" && $parkingId != null){
                addParkingEmployeeMappingData($employeeId);
            }

            if($locationId != "0"){
                addLocationEmployeeMappingData($employeeId);
            }
            
            //Add profile pic
            if($employeeProfilePic != "null"){
                addProfilePic();
            }
        }
    }

    function addEmployeeEntityRoleMapping(){
        global $connection, $employeeEntityRoleMappingTable, $employeeId, $entityId, $employeeRole, $employeeEntity;
        $roleId = getRoleId();

        if($employeeRole == "SuperAdmin" && $employeeEntity == "ConsumerApp")
        {
            addNotification();
            sendNotification();
        }

        $fetchLastGeneratedEmployeeIdLList_query = "select GeneratedEmployeeId from ".$employeeEntityRoleMappingTable." where EntityId = $entityId and RoleId = $roleId";
        $fetchLastGeneratedEmployeeIdList_result = mysqli_query($connection, $fetchLastGeneratedEmployeeIdLList_query);
        if(!$fetchLastGeneratedEmployeeIdList_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else{
            $totalRow = $fetchLastGeneratedEmployeeIdList_result->num_rows;
            $generatedEmployeeId = 0;
            $newGeneratedEmployeeId = 1;
            if($totalRow > 0)
            {
                while($row =  mysqli_fetch_assoc($fetchLastGeneratedEmployeeIdList_result)){
                    $generatedEmployeeId = $row["GeneratedEmployeeId"];
                }
                $newGeneratedEmployeeId = $newGeneratedEmployeeId + $generatedEmployeeId;
            }
            
            $add_employeeEntityRoleMapping_query = "insert into ".$employeeEntityRoleMappingTable." (EmployeeId, EntityId, RoleId, 
            GeneratedEmployeeId) values ($employeeId, $entityId, $roleId, $newGeneratedEmployeeId)";

            if(!mysqli_query($connection, $add_employeeEntityRoleMapping_query)){
                echo "Error creating new record '".mysqli_error($connection)."'";
            }
            else{
                echo trim($employeeId);
            }
        }
    }
    
    function addNotification(){
        global $connection, $notificationTable, $employeeId;
        
        $addNotification_query = "insert into consumernotification (EmployeeId, IsActivated) values ($employeeId, 0)";
        $addNotification_result = mysqli_query($connection, $addNotification_query);
        if(!$addNotification_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
    }

    function addLocationEmployeeMappingData($employeeId){
        global $connection, $employeeLocationMappingTable, $locationId;
        $addEmployeeLocationMapping_query = "insert into ".$employeeLocationMappingTable." (EmployeeId, LocationId)
                        values ($employeeId, $locationId)";
        $addEmployeeLocationMapping_result = mysqli_query($connection, $addEmployeeLocationMapping_query);
        if(!$addEmployeeLocationMapping_result){
            echo "Error creating record '".mysqli_error($connection)."'";
        }
    }

    function addProfilePic(){
        global $ftp_conn, $employeeAdharNumber, $employeeProfilePic;
        $adharNumber = str_replace(' ','',$employeeAdharNumber);
        $ImagePath = "profilePic/".$adharNumber.".png";
        if(ftp_chdir($ftp_conn, "duepark/"))
        {
            if(!is_dir("profilePic"))
            {
                if(ftp_mkdir($ftp_conn, "profilePic"))
                {
                    $profilePic = base64_decode($employeeProfilePic);
                    file_put_contents($ImagePath,$profilePic);
                }
            }
            else
            {
                $profilePic = base64_decode($employeeProfilePic);
                file_put_contents($ImagePath,$profilePic);
            }
        }
        ftp_close($ftp_conn);
    }

    function getRoleId()
    {
        global $connection, $employeeRoleTable, $employeeRole;

        $roleId_query = "select id from ".$employeeRoleTable." where Role = '$employeeRole'";
        $roleId_result = mysqli_query($connection,$roleId_query);
        if(!$roleId_result){
            echo "Error retrieving record '".mysqli_error($connection)."'";
            //return;
        } 
        else
        {
            $row = mysqli_fetch_assoc($roleId_result);
            return $row['id'];
        }        
    }
    
    function addParkingEmployeeMappingData($employeeId){
        global $connection, $parkingEmployeeMappingTable, $parkingId;
        $addParkingEmployeeMapping_query = "insert into ".$parkingEmployeeMappingTable." (ParkingId, ConsumerAppEmployeeId, AdminAppEmployeeId)
                        values ($parkingId, $employeeId, null)";
        $addParkingEmployeeMapping_result = mysqli_query($connection, $addParkingEmployeeMapping_query);
        if(!$addParkingEmployeeMapping_result){
            echo "Error creating record '".mysqli_error($connection)."'";
        }
    }
    
    function addAdminManagerEmployeeMapping($employeeId){
        global $connection, $adminManagerEmployeeMappingTable, $loggedInEmployeeId;
        $addAdminManagerEmployeeMapping_query = "insert into ".$adminManagerEmployeeMappingTable." (AdminId, ManagerId) values ($loggedInEmployeeId, $employeeId)";
        $addAdminManagerEmployeeMapping_result = mysqli_query($connection, $addAdminManagerEmployeeMapping_query);
        if(!$addAdminManagerEmployeeMapping_query){
            "Error creating record '".mysqli_error($connection)."'";
        }
    }

    function add_Admin_ManagerValetEmployeeMapping($employeeId){
        global $connection, $admin_managerValetEmployeeMappingTable, $loggedInEmployeeId;
        $addAdmin_ManagerValetEmployeeMapping_query = "insert into ".$admin_managerValetEmployeeMappingTable." (Admin_ManagerId, ValetId) values ($loggedInEmployeeId, $employeeId)";
        $addAdmin_ManagerValetEmployeeMapping_result = mysqli_query($connection, $addAdmin_ManagerValetEmployeeMapping_query);
        if(!$addAdmin_ManagerValetEmployeeMapping_result){
            "Error creating record '".mysqli_error($connection)."'";
        }
    }

    function add_Admin_ManagerSaleEmployeeMapping($employeeId){
        global $connection, $admin_managerSaleEmployeeMappingTable, $loggedInEmployeeId;
        $addAdmin_ManagerSaleEmployeeMapping_query = "insert into ".$admin_managerSaleEmployeeMappingTable." (Admin_ManagerId, SaleId) values ($loggedInEmployeeId, $employeeId)";
        $addAdmin_ManagerSaleEmployeeMapping_result = mysqli_query($connection, $addAdmin_ManagerSaleEmployeeMapping_query);
        if(!$addAdmin_ManagerSaleEmployeeMapping_result){
            "Error creating record '".mysqli_error($connection)."'";
        }
    }
    
    function sendNotification(){
        global $connection, $adminAppEmployeeTokenTable ;
        $registrationTokens = array();
        $getAllEmployeeToken_query = "select Token from ".$adminAppEmployeeTokenTable;
        $getAllEmployeeToken_result = mysqli_query($connection, $getAllEmployeeToken_query);
        if(!$getAllEmployeeToken_query){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else{
            while($row = mysqli_fetch_assoc($getAllEmployeeToken_result)){
                array_push($registrationTokens, $row['Token']);
            }

            $msg = array(
                    'body' 	=> 'New Parking Added..',
                    'title'	=> 'Parking Added'
                );

            $url = "https://fcm.googleapis.com/fcm/send";
            $fields = array(
                'registration_ids' => $registrationTokens,
                'data' => $msg,
                'priority' => "high"
            );
            $headers = array(
                'Authorization: key=' .API_ACCESS_KEY,
                'Content-Type: application/json'
            );

            $ch = curl_init();
            curl_setopt( $ch,CURLOPT_URL, $url );
            curl_setopt( $ch,CURLOPT_POST, true );
            curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
            curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
            curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
            curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);
            curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
            $result = curl_exec($ch);
            
            if($result === FALSE){
                die('Curl Failed '.curl_error($ch));
            }
            curl_close($ch);
        }
    }
?>