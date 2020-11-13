<?php

    require "init.php";
    //require "create_employeeTable.php";
    
    $admin_managerSaleEmployeeMappingTable = "admin_managersaleemployeemapping";

    $sql_query = "show tables like '".$admin_managerSaleEmployeeMappingTable."'";
    $result = mysqli_query($connection,$sql_query);

    if($result->num_rows == 0){
        createAdmin_ManagerSaleEmployeeMappingTable(); 
    }

    function createAdmin_ManagerSaleEmployeeMappingTable(){
        global $connection, $admin_managerSaleEmployeeMappingTable;

        $sql_query = "create table ".$admin_managerSaleEmployeeMappingTable." (
            id int(200) NOT NULL AUTO_INCREMENT PRIMARY KEY,
            Admin_ManagerId int(200) NOT NULL,
            SaleId int(200) NOT NULL,
            FOREIGN KEY (Admin_ManagerId) references employee(id),
            FOREIGN KEY (SaleId) references employee(id) 
        )";

        $result = mysqli_query($connection,$sql_query);

        if(!$result){
            echo " Table not created '".mysqli_error($connection)."'";
        }
    }

?>