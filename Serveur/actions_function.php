<?php
	function add_beacon($bdd, $values, $files = null)
	{
		$x = $values['x'];
		$y = $values['y'];
		$posMX = $values['posMX'];
		$posMY = $values['posMY'];
		$minorId = $values['minorId'];
		$majorId = $values['majorId'];
		$MAC = $values['MAC'];
		$id_plan = $values['id_plan'];
		
		$result = $bdd->prepare('INSERT INTO beacon (x, y, posMX, posMY, minorId, majorId, MAC, id_plan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)');
		if($result->execute(array($x, $y, $posMX, $posMY, $minorId, $majorId, $MAC, $id_plan)))
		{
			$flag['code'] = 1;
		}
		else
		{
			$flag['code'] = -1;
		}

		return $flag;
	}
	
	function get_beacon($bdd, $values, $files = null)
	{
		$sql = "SELECT * FROM beacon WHERE id_plan = ?";
		
		$result = $bdd->prepare($sql);
		$result->execute(array($_REQUEST['id_plan']));
		
		$elements = $result->fetchAll(PDO::FETCH_ASSOC);
		$plans = array();
		
		foreach($elements as $e)
		{
			$plans[$e['id']] = $e;
		}
		
		return $plans;
	}
	
	function add_plan($bdd, $values, $files = null)
	{
		$nom = 'upload_plan/'.$files['image']['name'];
		$width = $values['width'];
		$height = $values['height'];
		$widthReal = $values['largM'];
		$heightReal = $values['longM'];
		
		if(!empty($files['image']))
		{		
			$resultat = move_uploaded_file($files['image']['tmp_name'],$nom);
			
			$result = $bdd->prepare('INSERT INTO plan (img, width, height, longM, largM) VALUES (?, ?, ?, ?, ?)');
			if($result->execute(array($nom, $width, $height, $widthReal, $heightReal)))
			{
				$flag['code'] = $bdd->lastInsertId();
			}
		}
		else
		{
			$flag['code'] = -1;
		}
		
		return $flag;
	}
	
	function get_plan($bdd, $values, $files = null)
	{
		$sql = "SELECT * FROM plan";
	
		$result = $bdd->prepare($sql);
		$result->execute();
		
		$elements = $result->fetchAll(PDO::FETCH_ASSOC);
		$plans = array();
		
		foreach($elements as $e)
		{
			$plans[$e['id']] = $e;
		}
		
		return $plans;
	}
	
	function get_actual_plan($bdd, $values, $files = null)
	{
		$sql = "SELECT * FROM beacon WHERE majorId = ? and minorId = ?";
		
		$result = $bdd->prepare($sql);
		$result->execute(array($values['majorId'], $values['minorId']));
		
		$elements = $result->fetch(PDO::FETCH_ASSOC);
		
		$sql = "SELECT * FROM plan WHERE id = ?";
		
		$result = $bdd->prepare($sql);
		$result->execute(array($elements['id_plan']));
		
		$elements = $result->fetch(PDO::FETCH_ASSOC);
		
		return $elements;
	}
	
	function get_all_user($bdd, $values, $files = null)
	{
		$sql = "SELECT * FROM user WHERE id_plan = ?";
		
		$result = $bdd->prepare($sql);
		$result->execute(array($values['id_plan']));
		
		$elements = $result->fetchAll(PDO::FETCH_ASSOC);
		$users = array();
		
		foreach($elements as $e)
		{
			$users[$e['id']] = $e;
		}
		
		return $users;
	}
	
	function insert_user($bdd, $values, $files = null)
	{
		$sql = "INSERT INTO user (nom, posX, posY, lastUpdate, id_plan) VALUES(?, ?, ?, NOW(), ?)";
		
		$result = $bdd->prepare($sql);
		
		if($result->execute(array($values['nom'], $values['posX'], $values['posY'], $values['id_plan'])))
		{
			$flag['code'] = $bdd->lastInsertId();
		}
		else
		{
			$flag['code'] = -1;
		}
		
		return $flag;
	}
	
	function update_user($bdd, $values, $files = null)
	{
		$sql = "UPDATE user SET posX = ?, posY = ?, lastUpdate = NOW(), id_plan = ? WHERE id = ?";
		$result = $bdd->prepare($sql);
		
		$result->execute(array($values['posX'], $values['posY'], $values['id_plan'], $values['id']));
		
		$flag['code'] = $values['id'];
		
		return $flag;
	}
	
	function delete_user($bdd, $values, $files = null)
	{
		$sql = "DELETE FROM user WHERE id = ?";
		$result = $bdd->prepare($sql);
		$result->execute(array($values['id']));
	}
	
	function clean_user_table($bdd)
	{
		$sql = "DELETE FROM user WHERE lastUpdate < NOW() - INTERVAL 1 MINUTE";
		$result = $bdd->prepare($sql);
		$result->execute();
	}
	
	$bdd = new PDO('mysql:host=localhost;dbname=bd_plan_scanning;charset=utf8', 'root', '');
		
	clean_user_table($bdd);
	
	if(count($_REQUEST) > 0)
	{
		$function = $_REQUEST['function'];
		$result = $function($bdd, $_REQUEST, $_FILES);
		print(json_encode($result));
	}
	
	$bdd = null;
?>