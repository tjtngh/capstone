<?php
$con=mysqli_connect("localhost","root","4864361");

mysqli_set_charset($con,"utf8");
mysqli_select_db($con,'opentutorials');

if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$ID = $_POST['ID'];
$Password = $_POST['Password'];
$result = mysqli_query($con,"select * from info where ID='"$ID."' AND Password = '"$Password."'";);
$rows=mysql_num_rows($result);

  if($rows==0){
    echo "No Such User Found"
  }
  else {
    echo "User Found";
  }
mysqli_close($con);
?>
