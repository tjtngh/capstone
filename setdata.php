<?php
    // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호)
    $con=mysqli_connect("localhost","root","4864361");
    mysqli_set_charset($con,"utf8");
    mysqli_select_db($con,'cap');

    if (mysqli_connect_errno($con))
    {
       echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }
    $Name = $_POST['Name'];
    $Department = $_POST['Department'];
    $Rank = $_POST['Rank'];

$result = mysqli_query($con,"select * from info where Name='$Name' AND Department='$Department' AND Rank='$Rank'");
$row=mysqli_fetch_row($result);

  echo "$row[0]-$row[2]-$row[3]-$row[4]-$row[5]\n";

  mysqli_close($con);
?>
