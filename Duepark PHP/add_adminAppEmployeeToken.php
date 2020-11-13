<?php

    require "init.php";
    require __DIR__.'/includes/vendor/autoload.php';
    require "create_employeeTable.php";
    require "create_adminAppEmployeeTokenTable.php";
    require "create_notificationTable.php";
    //define ("API_ACCESS_KEY", "AAAAa3YmA0o:APA91bG42crbEC8sIVcZtyEsFCSI4_FeitNwiVBJctoYyDUH_Dmqdmgzsvqk-ArTNrvWSIB_vdsGCUg1-nB40QUscABRIJll0M1OLAnN7rD2N9XN8fO1ICcScRPpEN3f9U9uz0h6j9zv");
    
    $employeeId = $_POST['EmployeeId'];
    $employeeToken = $_POST['EmployeeToken'];

    if($employeeId != null && $employeeToken != null){
        $addToken_query = "insert into ".$adminAppEmployeeTokenTable." (EmployeeId, Token) values ($employeeId, '$employeeToken')";
        $addToken_result = mysqli_query($connection, $addToken_query);
        if(!$addToken_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else{
            checkAllParkingApprovalStatus();
        }
    }

    function checkAllParkingApprovalStatus()
    {
        global $consumerNotificationTable, $connection;
        $getAllUnApprovedParking_query = "select * from ".$consumerNotificationTable." where IsActivated = 0";
        $getAllUnApprovedParking_result = mysqli_query($connection, $getAllUnApprovedParking_query);
        if(!$getAllUnApprovedParking_result){
            echo "Error creating new record '".mysqli_error($connection)."'";
        }
        else{
            if(mysqli_num_rows($getAllUnApprovedParking_result)>0){
                sendNotification();
            }
        }
    }

    function sendNotification(){
        global $employeeToken;
        $msg = array(
                'body' 	=> 'There are pending parking need to approved..',
    		    'title'	=> 'Parking Approval'
            );

        $url = "https://fcm.googleapis.com/fcm/send";
        $fields = array(
            'to' => $employeeToken,
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
?>