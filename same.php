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

$result1 = mysqli_query($con,"select ID from info where ID='$ID'");
$result2 = mysqli_query($con,"select ID from signup where ID='$ID'");
$row1=mysqli_fetch_row($result1);
$row2=mysqli_fetch_row($result2);

if($row1[0]==$ID||$row2[0]){
  echo '아이디가 중복됩니다.';
}
else{
  echo '사용 가능합니다.';
}
  // echo "$row[0]\n$row[1]\n$row[2]"";
  mysqli_close($con);
?>
