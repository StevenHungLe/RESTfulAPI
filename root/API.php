<?php	
/**
* 
* The main and single file of the RESTful API
* accepts and processes requests and maintains the underlying database
* 
*/
	
	// establish connection with the database
	$conn = new mysqli('localhost','nildb','nildb2016');
	
	IF ( $conn->connect_error)
	{
		die("can't connect:".$conn->connect_error);
	}
			
	// obtain user full request
	$request = $_GET['request'];
	
	// using strtok with delimiter "/", split the request into tokens
	// The first token is the target resource to be accessed
	$target_resource = strtok($request,"/");
	
	$request_body = file_get_contents('php://input');
	
	/**
	* 
	* Inspect the target_resource to determine what operation is to be done
	* call the appropriate function passing the appropriate parameters
	* 
	*/
	
	////////// CASE: GET method - the client is trying to retrieve some resources \\\\\\\\\\
	if ( $_SERVER['REQUEST_METHOD'] == "GET" )
	{
		// target resource: access_token target_resource - the client is trying to log in and retrieve an access token
		if ($target_resource == "access_token")
		{
			// get the needed parameters then call the login function
			$user_id = strtok("/");
			$password = strtok("/");
			login( $user_id , $password );
		}
		
		// target resource: players - the client is trying to access info of a player or the value of an assessment of that player
		if ( $target_resource == "players")
		{
			// get the needed parameters then call the appropriate function
			$player_id = strtok("/");
			$sub_target_resource = strtok("/");
			
			if ( $sub_target_resource == "assessments") // getting the value of an assessment
			{
				$assessment_name = strtok("/");
				$date = strtok("/");
				$access_token = getallheaders()['access_token'];
				
				if ( $date == "") // no date specified -> retrieve the most recent value
				{
					// check if this user can modify for this player
					if ( !is_authorized_for_player($access_token, $player_id)) 
					{
						http_response_code(401);
						die ("Unauthorized action");
					}
					getMostRecentAssessment($player_id,$assessment_name);
				}
				else // retrieve the value of the assessment taken closest but no later than the specified date
				{
					// admins only: check if this user is an admin
					if ( !is_admin($access_token))
					{
						http_response_code(401);
						die ("Unauthorized action");
					}
					
					
					else
					{
						$date_and_time = "";
						$time = strtok("/");
						if ( $time == "" ) // the client only specifies date but not time ( YYYY-MM-DD )
							// append time to the parameter; make it the end of the day
							$date_and_time = $date." 23:59:59";
						else
							// append time and date to conform to mysql date format
							$date_and_time = $date." ".$time;
						
						getAssessmentByDate($player_id, $assessment_name, $date_and_time);
					}
				}
			
			}
			else
			{
				// TODO: player retrieving code here
			}
			
		}
	}
	
	
	////////// CASE: POST method - the client is trying to create a new entry \\\\\\\\\\
	elseif( $_SERVER['REQUEST_METHOD'] == "POST" )
	{
		// Decode the Json-encoded request body into array of arguments
		$args = json_decode( file_get_contents('php://input'), true );
	
		// get the access_token from headers
		$access_token = getallheaders()['access_token'];
		
			
		// target resource: users target_resource - the client is trying to create a new user
		if( $target_resource == "users")
		{
			// call function is_admin to verify user's eligibility for this action
			if ( !is_admin( $access_token) )
			{
				http_response_code(401);
				die ("Unauthorized action");
			}
			createNewUser($args['user_id'],$args['password'],$args['is_admin']);
		}
		
		// target resource: orgs target_resource - the client is trying to create a new organization
		elseif( $target_resource == "orgs")
		{
			// call function is_admin to verify user's eligibility for this action
			if ( !is_admin( $access_token) )
			{
				http_response_code(401);
				die ("Unauthorized action");
			}
			createNewOrg($args['org_id'],$args['org_name']);
		}
		
		// target resource: associations target_resource - the client is trying to create a new user-organization association
		elseif( $target_resource == "associations")
		{
			// call function is_admin to verify user's eligibility for this action
			if ( !is_admin( $access_token) )
			{
				http_response_code(401);
				die ("Unauthorized action");
			}
			assocOrgWithUser($args['org_id'],$args['user_id']);
		}
		
		// target resource: players target_resource - the client is trying to create a new player or a new assessment
		elseif( $target_resource == "players")
		{
			$player_id = strtok("/");
			$sub_target_resource = strtok("/");
			if ( $sub_target_resource  == "assessments") // add a new assessment 
			{
				// call function is_authorized_for_player to verify user's eligibility for this action
				if ( !is_authorized_for_player( $access_token, $player_id) )
				{
					http_response_code(401);
					die ("Unauthorized action");
				}
				addNewAssessment($player_id, $args['assessment_name'], $args['value'], $args['date_and_time']);
			}
			else
			{
				// call function is_authorized_for_org to verify user's eligibility for this action
				if ( !is_authorized_for_org( $access_token, $args['org_id']) )
				{
					http_response_code(401);
					die ("Unauthorized action");
				}
				createNewPlayer($args['player_name'],$args['birthdate'],$args['gender'],$args['active'],$args['org_id']);	
			}
		}
		
		// target resource: undefined
		else
		{
			http_response_code(500);
		}
	}
	
	
	////////// CASE: PUT method - the client is trying to modify a file or an entry \\\\\\\\\\
	elseif( $_SERVER['REQUEST_METHOD'] == "PUT" )
	{

		// target resource: players - the user is trying to modify a player or append to a log file
		if( $target_resource == "players" )
		{ 
			// Decode the Json-encoded request body into array of arguments
			$args = json_decode( file_get_contents('php://input'), true );
		
			// get the access_token from headers
			$access_token = getallheaders()['access_token'];
			$date = getallheaders()['date'];
			
			$player_id = strtok("/");
			
			if ( strtok("/") == "logs" )
			{
				// call function is_authorized_for_player to verify user's eligibility for this action
				if ( !is_authorized_for_player( $access_token, $player_id) )
				{
					http_response_code(401);
					die ("Unauthorized action");
				}
				$game_name = strtok("/");
				add_log($player_id , $game_name, $date);
			}
			else
			{
				// call method to modify a player
			}
			
		}
		
		// target resource: users - the user is trying to change password
		elseif( $target_resource == "users" )
		{ 
			$user_id = strtok("/");
			$old_password = strtok("/");
			$new_password = strtok("/");

			change_password($user_id , $old_password, $new_password);
		}
			
		
		// target resource: undefined 
		else
		{
			http_response_code(500);
		}
	}
	
	
	////////// CASE: DELETE method - the client is trying to delete an entry \\\\\\\\
	elseif( $_SERVER['REQUEST_METHOD'] == "DELETE" )
	{	
	
		// get the access_token from headers
		$headers = getallheaders();
		$access_token = $headers['access_token'];
		
		// call function is_admin to verify user's eligibility for this action
		if ( !is_admin( $access_token) )
		{
			http_response_code(401);
			die ("Unauthorized action");
		}
		
		// target resource: users target_resource - the client is trying to delete a user
		if( $target_resource == "users")
		{
			$user_id = strtok("/");
			deleteUser($user_id);
		}
		
		// target resource: orgs target_resource - the client is trying to delete an organization
		elseif( $target_resource == "orgs")
		{
			$org_id = strtok("/");
			deleteOrg($org_id);
		}
		
		// target resource: associations target_resource - the client is trying to delete a user-organization association
		elseif( $target_resource == "associations")
		{
			$org_id = strtok("/");
			$user_id = strtok("/");
			deleteAssociation($org_id,$user_id);
		}
		
		// target resource: players target_resource - the client is trying to delete a player or an assessment
		elseif( $target_resource == "players")
		{
			$player_id = strtok("/");
			$sub_target_resource = strtok("/");
			
			if ( $sub_target_resource == "assessments") // delete an assessment
			{
				deleteAssessment($player_id,$headers['assessment_name'],$headers['value'],$headers['date_and_time']);
			}
			else // delete a player
			{
				deletePlayer($player_id);
			}
		}
		
		// target resource: access_token target_resource - the client is trying to log out
		elseif ( $target_resource == "access_token" )
		{
			logout( $access_token );
		}
		
		// target resource: undefined
		else
		{
			http_response_code(500);
		}
	}
	

/*************************************************************************************************************
* 
* GET funtions: process GET requests including...
* 
* login
* getMostRecentAssessment
* getAssessmentByDate
* 
**************************************************************************************************************/

// function: processes a log in request
function login($arg_user_id , $arg_password)
{
	global $conn;
	// retrieve the password of the requested username in the database
	$stmt = $conn->prepare("SELECT is_admin FROM nildb.users WHERE user_id=? AND password = SHA2(?,512)");
	$stmt->bind_param("ss",$arg_user_id, $arg_password);
	$stmt->bind_result($is_admin);
	$stmt->execute();
	
	// check the password and send result of the authentication
	if ($stmt->fetch())
	{
		// generate the access token for the user
		$access_token = uniqid($arg_user_id,true);
		
		$stmt->close();
		
		// create new entry in the database that maps access token to the user
		$stmt = $conn->prepare("INSERT INTO nildb.access_token(access_token,user_id,is_admin) VALUES (?,?,?)");
		$stmt->bind_param("sss", $access_token, $arg_user_id, $is_admin );

		// create mapping successful
		if ( $stmt->execute())
		{
			http_response_code(200); // OK status
			$js = array( "access_token" => $access_token );
			echo json_encode($js); // return the access token in the response
		}
		
	}
	
	// user_id or password not matched
	else
	{
		http_response_code(401);
		$js = array( "error_msg" => "AUTHENTICATION FAILED" );
		echo json_encode($js);
	}
	
}


// function: process a request to get the value of the most recent assessment...
// specified by the player id and the assessment name
function getMostRecentAssessment($player_id, $assessment_name)
{
	global $conn;
	
	$stmt = $conn->prepare("SELECT value FROM `nildb`.`assessments` WHERE player_id = ? AND assessment_name = ? AND date_and_time = 
	( 	SELECT MAX( date_and_time ) 
		FROM `nildb`.`assessments`
		WHERE player_id = ? AND assessment_name = ? ) ");
		
	$stmt->bind_param("ssss",$player_id,$assessment_name,$player_id,$assessment_name);
	$stmt->bind_result($value);
	$stmt->execute();
	//successful operation
	if ( $stmt->fetch())
	{
		http_response_code(200);
		$js = array( "value" => $value);
		echo json_encode($js);
	}
	//failed
	else
		http_response_code(500);
}


// function: process a request to get the value of the assessment...
// that taken soonest before the specified date
function getAssessmentByDate($player_id, $assessment_name, $date_and_time)
{
	global $conn;
	
	$stmt = $conn->prepare("SELECT value FROM `nildb`.`assessments` WHERE player_id = ? AND assessment_name = ? AND date_and_time = 
	( 	SELECT MAX( date_and_time ) 
		FROM `nildb`.`assessments`
		WHERE player_id = ? AND assessment_name = ? AND date_and_time < ? ) ");
		
	$stmt->bind_param("sssss",$player_id,$assessment_name,$player_id,$assessment_name,$date_and_time);
	$stmt->bind_result($value);
	$stmt->execute();
	//successful operation
	if ( $stmt->fetch())
	{
		http_response_code(200);
		$js = array( "value" => $value);
		echo json_encode($js);
	}
	//failed
	else
		http_response_code(500);
}



/*************************************************************************************************************
* 
* POST funtions: process POST requests including...
* 
* createNewUser
* createNewOrg
* assocOrgWithUser
* createNewPlayer
* addNewAssessment
* 
*************************************************************************************************************/



// function: processes a request to create a new user
function createNewUser($user_id, $password, $is_admin)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO nildb.Users(user_id,password,is_admin) VALUES (?,SHA2(?,512),?)");
	$stmt->bind_param("sss",$user_id,$password,$is_admin );
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to create new organization
function createNewOrg($org_id, $org_name)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO nildb.Organizations(org_id,org_name) VALUES (?,?)");
	$stmt->bind_param("ss",$org_id,$org_name);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
}


// function: processes a request to associate a user with an organization
function assocOrgWithUser($org_id, $usr_id)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO nildb.org_user(org_id,usr_id) VALUES (?,?)");
	$stmt->bind_param("ss",$org_id,$usr_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to create a new player
function createNewPlayer($player_name, $birthdate, $gender, $active, $org_id)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO nildb.players(player_name,birthdate,gender,active,org_id) VALUES (?,?,?,?,?)");
	$stmt->bind_param("sssss",$player_name,$birthdate,$gender,$active,$org_id);
	
	//successful operation
	if ( $stmt->execute())
	{
		http_response_code(200);
		$result = $conn->query("SELECT player_id FROM nildb.players WHERE player_name='{$player_name}' AND org_id = '{$org_id}'");
		$result = $result->fetch_assoc();
		$js = array( "player_id" => $result['player_id'] );
		echo json_encode($js);
	}
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to add a new assessment
function addNewAssessment($player_id, $name, $value, $date)
{
	global $conn;
	
	$stmt = $conn->prepare("INSERT INTO nildb.assessments(player_id,name,value,date) VALUES (?,?,?,?)");
	$stmt->bind_param("ssss",$player_id,$name,$value,$date);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}



/*************************************************************************************************************
* 
* PUT funtions: process PUT requests including...
* 
* add_log
* change_password
* 
*************************************************************************************************************/


// function: append new log to a specified log file
function add_log($player_id, $game_name, $date)
{
	global $request_body;
	$file_name = $player_id."-".$game_name.".txt";
	$log_file = fopen($file_name,"a");
	
	$log_content = $request_body;
	
	fwrite( $log_file, $date."\n");
	fwrite( $log_file, $log_content."\n\n");
	
	http_response_code(200);
}


// function: processes a request to change password
function change_password($user_id, $old_password, $new_password)
{
	global $conn;
	
	$stmt = $conn->prepare("SELECT is_admin FROM nildb.users WHERE user_id=? AND password = SHA2(?,512)");
	$stmt->bind_param("ss",$user_id, $old_password);
	$stmt->bind_result($is_admin);
	$stmt->execute();
	
	// password matched
	if ($stmt->fetch())
	{
		$stmt->close();
		$stmt = $conn->prepare("UPDATE nildb.Users SET password = SHA2(?,512) WHERE user_id = ?");
		$stmt->bind_param("ss",$new_password, $user_id);
		//successful change of password
		if ( $stmt->execute())
			http_response_code(200);
		
	}
	// user_id or password not matched
	else
	{
		http_response_code(401);
		die("Unauthorzied access");
	}	
}



/*************************************************************************************************************
* 
* DELETE funtions: process DELETE requests including...
* 
* logout
* deleteUser
* deleteOrg
* deleteAssociation
* deletePlayer
* deleteAssessment
* 
*************************************************************************************************************/



// function: processes a log out request
function logout( $access_token )
{
	global $conn;
	
	// ensure that the access token is present in the database
	$stmt = $conn->prepare('select user_id from nildb.access_token where access_token=?');
	$stmt->bind_param("s",$access_token);
	$stmt->bind_result($query_result);
	$stmt->execute();
	
	// valid target resource: access_token exists in the database 
	if ($stmt->fetch())
	{

		$stmt->close();

		// delete the entry associated with the access token from the database
		$stmt = $conn->prepare("DELETE FROM nildb.access_token WHERE access_token=?");
		$stmt->bind_param("s",$access_token);
		
		// delete operation successful = log out successful
		if( $stmt->execute() )
		{
			http_response_code(200);
		}
		
		// delete failed
		else
		{
			http_response_code(500);
			$js = array( "error_msg" => "log out failed" );
			echo json_encode($js);
		}

	}
	
	// invalid target resource: access_token does not exist in the database
	else
	{
		http_response_code(404);
		$js = array( "error_msg" => "access_token unrecognized" );
		echo json_encode($js);
	}

}


// function: processes a request to delete a user
function deleteUser($user_id)
{
	global $conn;
	
	$stmt = $conn->prepare("DELETE FROM nildb.Users WHERE user_id = ?");
	$stmt->bind_param("s",$user_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to delete a organization
function deleteOrg($org_id)
{
	global $conn;
	
	$stmt = $conn->prepare("DELETE FROM nildb.Organizations WHERE org_id = ?");
	$stmt->bind_param("s",$org_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
}



// function: processes a request to delete a org_user association
function deleteAssociation($org_id, $usr_id)
{
	global $conn;
	
	$stmt = $conn->prepare("DELETE FROM nildb.org_user WHERE org_id = ? AND usr_id = ?");
	$stmt->bind_param("ss",$org_id,$usr_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}


// function: processes a request to delete a player
function deletePlayer($player_id)
{
	global $conn;
	
	$stmt = $conn->prepare("DELETE FROM nildb.players WHERE player_id=?");
	$stmt->bind_param("s",$player_id);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
}


// function: processes a request to delete an assessment
function deleteAssessment($player_id, $name, $value, $date)
{
	global $conn;
	
	$stmt = $conn->prepare("DELETE FROM nildb.assessments WHERE player_id = ? AND name = ? AND value = ? AND date = ?");
	$stmt->bind_param("ssss",$player_id,$name,$value,$date);
	
	//successful operation
	if ( $stmt->execute())
		http_response_code(200);
	//failed
	else
		http_response_code(500);
		
}



/*************************************************************************************************************
* 
* authorization funtions: check user's authorization, includes:
* 
* is_admin
* is_authorzied_for_player
* is_authorzied_for_org
* 
*************************************************************************************************************/

//function: returns true if the given user is an admin, otherwise false
function is_admin ($access_token) 
{
	global $conn;
	
	$sql = "SELECT is_admin FROM nildb.Access_token WHERE access_token='{$access_token}'";
	$result = $conn->query($sql);

	if($result AND $result->num_rows > 0)
	{
		$result = $result->fetch_assoc();
		if( $result['is_admin'] == 1 ) 
			return true;
	}
	return false;
}


//function: returns true if the given user has the required authorization to modify the given player
// i.e. the user and the player is associated with the same organization, or the user is an admin
function is_authorized_for_player ($access_token,$player_id)
{
	global $conn;
	
	if( is_admin($access_token) ) 
		return true;
	else
	{
		$sql = "SELECT * FROM nildb.players NATURAL JOIN nildb.org_user NATURAL JOIN nildb.access_token WHERE player_id ={$player_id} AND access_token='{$access_token}'";
		$result = $conn->query($sql);

		if($result AND $result->num_rows > 0) 
			return true;
		
	}
	
	return false;
}



//function: returns true if the given user has the required authorization to modify the given organization
// i.e. the user is associated with the organization, or the user is an admin
function is_authorized_for_org ($access_token,$org_id)
{
	global $conn;
	
	if( is_admin($access_token)) 
		return true;
	else
	{
		$sql = "SELECT * FROM nildb.org_user NATURAL JOIN nildb.access_token WHERE org_id ='{$org_id}' AND access_token='{$access_token}'";
		$result = $conn->query($sql);

		if($result AND $result->num_rows > 0) 
			return true;
	}
	
	return false;
}
	
?>