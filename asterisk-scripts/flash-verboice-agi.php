#!/usr/bin/php
<?php   

include "HttpClient.class.php";

$callback_url = "http://127.0.0.1:3001/products";

# Don't let script run for more than 1 min
        set_time_limit(60);

#Turn off output buffering
        ob_implicit_flush(false);

#Turn off error reporting

#        error_reporting(0);

# create file handles if needed

        if (!defined('STDIN'))
        {
                define('STDIN', fopen('php://stdin', 'r'));
        }
        if (!defined('STDOUT'))
        {
                define('STDOUT', fopen('php://stdout', 'w'));
        }
        if (!defined('STDERR'))
        {
                define('STDERR', fopen('php://stderr', 'w'));
        }

# retrieve all AGI variables from Asterisk
        while (!feof(STDIN))
        {
                $temp = trim(fgets(STDIN,4096));
                if (($temp == "") || ($temp == "\n"))
                {
                        break;
                }
                $s = split(":",$temp);
                $name = str_replace("agi_","",$s[0]);
                $agi[$name] = trim($s[1]);
        }
# print all AGI variables for debugging purposes
        foreach($agi as $key=>$value)
        {
                fwrite(STDERR,"-- $key = $value\n");
                fflush(STDERR);
        }

	
$callerid = strlen($agi['callerid']) == 9 ? "".$agi['callerid'] : "000".$agi['callerid'];	
#$callerid = '540923923';
	
if(($response = HttpClient::quickGet($callback_url."?callerid=".$callerid)) < 0) {

	echo "failed";

} else {
	print_r($intellivr);	
}

?>
