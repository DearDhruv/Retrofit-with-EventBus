<?php

class ImageListResults {
    public $id = "";
    public $img = "";
	public $name = "";
}

$dir         = "uploads/";
$actual_link = "http://$_SERVER[HTTP_HOST]/api/uploads/";

$message = 'Failed';
$status  = 0;

if (is_dir($dir)) {
    if ($dh = opendir($dir)) {
        
        $img_arr = array();
        $id_count = 0;
        
		while (($file = readdir($dh)) !== false) {
            
            if ($file != '.' && $file != '..') {
                
                $results = new ImageListResults();
                
                $results->id   = $id_count;
                $results->img  = $actual_link . $file . "";
                $results->name = $file;
                
                array_push($img_arr, $results);
                $id_count++;
            }
            
        }
		
		$message = 'Success';
		$status  = 1;
        closedir($dh);
    }
    
}

$response = array(
    'message' => $message,
    'status' => $status,
    'image_list' => $img_arr
);

header('Content-type:application/json;charset=utf-8');
echo json_encode($response);
?>
