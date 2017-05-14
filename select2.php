<?php
    // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호)
    $con=mysqli_connect("localhost","root","4864361");
    mysqli_set_charset($con,"utf8");
    mysqli_select_db($con,'cap');

    if (mysqli_connect_errno($con))
    {
       echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }


  $result = mysqli_query($con,"select * from signup");
  $row=mysqli_fetch_row($result);
if(result){
  echo "$row[0]<br>\n";
while($con=mysqli_fetch_assoc($result))
{
  echo "$con[ID]<br>\n";
}
}
else {
  echo "실패";
}
  // echo "$row[0]\n$row[1]\n$row[2]";
  mysqli_close($con);
?>
