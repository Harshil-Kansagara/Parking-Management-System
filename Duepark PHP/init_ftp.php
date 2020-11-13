<?php

    $ftp_server = "127.0.0.1";
    $ftp_username = "harshil";
    $ftp_userpass = "H@rshil24";
    $ftp_conn = ftp_connect($ftp_server) or die("Could not connect to $ftp_server");
    $login = ftp_login($ftp_conn, $ftp_username, $ftp_userpass);

    if ((!$ftp_conn) || (!$login)) {
        echo 'FTP connection has failed! Attempted to connect to '. $host. ' for user '.$user.'.';
    }

?>