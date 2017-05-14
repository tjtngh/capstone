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


$result = mysqli_query($con,"update info set Password='$Password' where ID='$ID'");

if($result){
  echo '수정완료';
}
else{
  echo '수정실패';
}
  mysqli_close($con);
?>
