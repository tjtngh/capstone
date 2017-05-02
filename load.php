<?php
    // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호)
    $con=mysqli_connect("localhost","root","4864361");
    mysqli_set_charset($con,"utf8");
    mysqli_select_db($con,'cap');

    if (mysqli_connect_errno($con))
    {
       echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }
    $ID = $_POST['ID'];
    $Password = $_POST['Password'];

$result = mysqli_query($con,"select * from info where ID='$ID' AND Password = '$Password'");
$row=mysqli_fetch_row($result);

if($row[0]==$ID AND $row[1]==$Password){
  echo '로그인성공';
}
else{
  echo '아이디, 비밀번호를 확인해주세요';
}
  // echo "$row[0]\n$row[1]\n$row[2]"";
  mysqli_close($con);
?>
