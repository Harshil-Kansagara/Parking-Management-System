<?php

    //$ftp_server = "ftps://waws-prod-dm1-153.ftp.azurewebsites.windows.net/site/wwwroot";
    $ftp_server = "ftps://waws-prod-dm1-153.ftp.azurewebsites.windows.net/site/wwwroot";
    $ftp_username = "duepark\$harshil";
    $ftp_userpass = "Harshil$24";
    $ftp_conn = ftp_connect($ftp_server) or die("Could not connect to $ftp_server");
    ftp_pasv($ftp_conn, true);
    $login = ftp_login($ftp_conn, $ftp_username, $ftp_userpass);

    if ((!$ftp_conn) || (!$login)) {
        echo 'FTP connection has failed! Attempted to connect to '. $ftp_server. ' for user '.$ftp_username.'.';
    }
    else{
        echo "Connected";
    }

?>