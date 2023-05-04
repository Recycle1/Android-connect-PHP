<?php
    if(is_array($_GET)&&count($_GET)>0){
        if(isset($_GET['n'])){
            $n=$_GET['n'];
        }
    }
    //根据传过来的数据$n可作为关键字，查询数据库的信息，这里就不演示数据库交互过程了
    $content=array();
    $content[0]=array("a1"=>"网络测试数据1","a2"=>1,"a3"=>1.5);
    $content[1]=array("a1"=>"网络测试数据2","a2"=>2,"a3"=>2.5);
	echo json_encode($content);
?>