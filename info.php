<?php
$con=mysqli_connect("localhost","root","4864361");

mysqli_set_charset($con,"utf8");
mysqli_select_db($con,'cap');

if (mysqli_connect_errno($con))
{
   echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$ID = $_POST['ID'];
$Password = $_POST['Password'];
$Name = $_POST['Name'];
$Department = $_POST['Department'];
$Rank = $_POST['Rank'];


$result = mysqli_query($con,"insert into info (ID,Password,Name,Department,Rank,AC) values ('$ID','$Password','$Name','$Department','$Rank',5)");

  if($result){
    echo '가입요청완료';
  }
  else{
    echo '회원가입실패';
  }
mysqli_close($con);
?>
