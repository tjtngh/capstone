<?php
    // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호)
    $con=mysqli_connect("localhost","root","4864361");
    mysqli_set_charset($con,"utf8");
    mysqli_select_db($con,'cap');

    if (mysqli_connect_errno($con))
    {
       echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }
    $all = "";
    $Name = $_POST['Name'];
    $str = strcmp($Name,$all);
if(!$str){
  $result = mysqli_query($con,"select Name,Department,Rank from info");
  $row=mysqli_fetch_row($result);
}
else{
$result = mysqli_query($con,"select Name,Department,Rank from info where Name='$Name'");
$row=mysqli_fetch_row($result);
}


if($row[0]==$Name&&$Name!=null){
  echo "$row[0]-$row[1]-$row[2]<br>\n";
while($con=mysqli_fetch_assoc($result))
{
  echo "$con[Name]-$con[Department]-$con[Rank]<br>\n";
}
}
else if(!$str)
{
  echo "$row[0]-$row[1]-$row[2]<br>\n";
while($con=mysqli_fetch_assoc($result))
{
  echo "$con[Name]-$con[Department]-$con[Rank]<br>\n";
}
}
else{
  echo '아이디를 확인해주세요';
}
  // echo "$row[0]\n$row[1]\n$row[2]";
  mysqli_close($con);
?>
