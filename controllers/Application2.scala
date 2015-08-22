package controllers

import anorm._
import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import java.io._
import play.api.data._
import play.api.data.Forms._
import java.sql._
import scala.io._



object Application2 extends Controller {

  def base_path = "D:\\data\\igbo\\scenarios2\\"

  def index = Action {
    //Ok(views.html.index("Your new application is ready."))
    var outString = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("select * from Scenario")
      while (rs.next()) {
        //outString += rs.getString("testkey")
        outString += rs.getString("scenario") + "\n"
      } 
    } finally {
      conn.close()
    }
    Ok(outString)

  }


  def t1 = Action {
  	DB.withConnection { implicit c =>
	  val result: Boolean = SQL("Select 1").execute()    
	}
	//import java.io._
	val pw = new PrintWriter(new File("F:\\data\\igbo\\scenarios\\hello.txt" ))
	pw.write("Hello, world")
	pw.close

	Ok("OK")

  }

  /**
   * Returns a dropdown with Scenarios (forward=type 1)
   *
   */

  def db_list_scenarios = Action {
  	var outString = "<select id=\"filename\" class=\"form-control\">"
  	val conn = DB.getConnection()
  	try {
  		val stmt = conn.createStatement
	    val rs = stmt.executeQuery("select * from Scenario where Object_id=1 order by data_start desc")
	    while (rs.next()) {
	        //outString += rs.getString("testkey")
	       outString += "<option value=\"" + rs.getString("Scenario_id") + "\">" + rs.getString("scenario").replaceAll("\\s+$", "") + " " + rs.getString("simulation") +" " + rs.getString("data_start") + " - " + rs.getString("data_stop") +  "</option>\n"
	    } 
  	} finally {
  	conn.close()
  }
  outString += "</select>"
  Ok(outString)

}



val scenarioqueryForm = Form(
		tuple(
			"start_date" -> text,
			"end_date"	 -> text,
			"description" -> text,
			"base_plan"   -> text,
			"object_id"   -> text,
			"apply_query" -> text
			)
		)

/**
   * Returns a dropdown with Scenarios based on the scenarioquery
   *
   */

  def db_query_scenarios = Action { implicit request =>
  	val (start_date, end_date, description, base_plan, object_id, apply_query) = scenarioqueryForm.bindFromRequest.get
  	var query_conditions = ""
  	var query = " where "
  	if( object_id > "" )  query = query + s" Object_id='$object_id' "
  	if( start_date > "" ) query = query + s" and data_start>= '$start_date' "
  	if( end_date > ""   ) query = query + s" and data_stop<= '$end_date' "
  	if( description > "") query = query + s" and scenario like '%$description%' "
  	if( base_plan > ""  )  query = query + s" and base_plan='$base_plan' "
  	

  	if( apply_query=="1") query_conditions = query

  	println(s"Query: [$query_conditions]")

  	var outString = "<select id=\"filename\" class=\"form-control\">"
  	val conn = DB.getConnection()
  	try {
  		val stmt = conn.createStatement
	    val rs = stmt.executeQuery(s"select * from Scenario $query_conditions order by data_start desc")
	    while (rs.next()) {
	        //outString += rs.getString("testkey")
	       outString += "<option value=\"" + rs.getString("Scenario_id") + "\">" + rs.getString("scenario").replaceAll("\\s+$", "") + " " + rs.getString("simulation") +" " + rs.getString("data_start") + " - " + rs.getString("data_stop") +  "</option>\n"
	    } 
  	} finally {
  	conn.close()
  }
  outString += "</select>"
  Ok(outString)

  //Ok(query_conditions)

}




/**
   * Returns a dropdown with Scenarios (forward=type 1)
   *
   */

  def db_list_scenarios_long = Action {
  	var outString = "<select id=\"filename\" class=\"form-control\">"
  	val conn = DB.getConnection()
  	try {
  		val stmt = conn.createStatement
	    val rs = stmt.executeQuery("select * from Scenario where Object_id=3 order by data_start desc")
	    while (rs.next()) {
	        //outString += rs.getString("testkey")
	       outString += "<option value=\"" + rs.getString("Scenario_id") + "\">" + rs.getString("scenario").replaceAll("\\s+$", "") + " " + rs.getString("simulation") +" " + rs.getString("data_start") + " - " + rs.getString("data_stop") +  "</option>\n"
	    } 
  	} finally {
  	conn.close()
  }
  outString += "</select>"
  Ok(outString)

}

/**
* Returns a dropdown with Scenarios (reverse=type 2)
*
*/


def db_list_scenarios_rev = Action {
	var outString = "<select id=\"filename\" class=\"form-control\">"
	val conn = DB.getConnection()
	try {
		val stmt = conn.createStatement
    val rs = stmt.executeQuery("select * from Scenario where Object_id=2 order by data_start desc")
    while (rs.next()) {
        //outString += rs.getString("testkey")
       outString += "<option value=\"" + rs.getString("Scenario_id") + "\">" + rs.getString("scenario").replaceAll("\\s+$", "") + " " + rs.getString("simulation") +" " + rs.getString("data_start") + " - " + rs.getString("data_stop") +  "</option>\n"
    } 
	} finally {
	conn.close()
}
outString += "</select>"
Ok(outString)

}

def db_list_init_scenarios = Action {
  	var outString = "<select id=\"filename\" name=\"filename\" class=\"form-control\">"
  	val conn = DB.getConnection()
  	try {
  		val stmt = conn.createStatement
	    val rs = stmt.executeQuery("select * from Scenario order by data_start desc")
	    while (rs.next()) {
	        //outString += rs.getString("testkey")
	       outString += "<option value=\"" + rs.getString("Scenario_id") + "\">" + rs.getString("scenario").replaceAll("\\s+$", "") + " " + rs.getString("simulation") +" " + rs.getString("data_start") + " - " + rs.getString("data_stop") +  "</option>\n"
	    } 
  	} finally {
  	conn.close()
  }
  outString += "</select>"
  Ok(outString)

}

//Get Nominations
def db_list_nominations = Action {
	val nodes = List("14","15","18","5","6","56","11","10","9","53","1","2","67","68","73","75","77","101","102","103","104","105","113","114","115","110","108","109","155","170","212","269","163","167","180","264","118","119","120","121","122","201","202","204","205","207","208","266","253")
	var outString = "data\t"
	nodes.foreach(n => { outString += n + "\t"; })
	outString += "\n"
	val conn = DB.getConnection()
	try {
  		val stmt = conn.createStatement
	    val rs = stmt.executeQuery("select * from Nomination order by data asc")
	    while (rs.next()) {
	    	outString += rs.getString("data") + "\t"
	    	nodes.foreach(n => { outString += rs.getString("no_" + n) + "\t"; })
	    	outString += "\n"
	    } 
	  	} finally {
	  	conn.close()
	  }

	Ok(outString)
}

//Get UCGR
def db_list_ucgr = Action {
	var outString = "id_ucgr\tobject_id\tfield\tucgr\n"
	val conn = DB.getConnection()
	try {
  		val stmt = conn.createStatement
	    val rs = stmt.executeQuery("select * from UCGR order by id_ucgr asc")
	    while (rs.next()) {
	    	outString += rs.getString("id_ucgr") + "\t"
	    	outString += rs.getString("object_id") + "\t"
	    	outString += rs.getString("field") + "\t"
	    	outString += rs.getString("ucgr") + "\n"
	    } 
	  	} finally {
	  	conn.close()
	  }

	Ok(outString)
}

//Load a scenario
//Trim - spaces string.replaceAll("\\s+$", "");

	def db_load_scenario(id: String) = Action {
		var outString = "Network_id\tScenario_id\tObject_id\tData_type\tTime_h\tNo_pipe\tCondLimit\tIndex_Node1\tIndex_Node2\tNode1\tNode2\tValve	Compressor pressure out\tCompressor Rate\tRog\tRod\tPressure_N1\tPressure_N2\tFlow_g\tFlow_cd\tTemperature\tLpk_g\tLpk_cd\tC1_g\tC2_g\tC3_g\tiC4_g\tnC4_g\tiC5_g\tnC5_g\tC6plus_g\tCO2_g\tN2_g\tH2S_g\tC1_cd\tC2_cd\tC3_cd\tiC4_cd\tnC4_cd\tiC5_cd\tnC5_cd\tC6plus_cd\tCO2_cd\tN2_cd\tH2S_cd\tH2O_cd\tGHV\n"
	  	//val fields = List("Scenario_id", "Object_id", "Data_type", "Time_h", "No_pipe", "CondLimit", "Index_Node1");
	  	//"Index_Node2", "Node1", "Node2", "Valve", "'Compressor pressure out'", "'Compressor Rate'", "Rog", "Rod", "Pressure_N1", "Pressure_N2", "Flow_g",
	  	//"Flow_cd", "Temperature", "Lpk_g", "Lpk_cd", "C1_g", "C2_g", "C3_g", "iC4_g", "nC4_g", "iC5_g", "nC5_g", "C6plus_g", "CO2_g", "N2_g", "H2S_g", "C1_cd",
	  	//"C2_cd", "C3_cd", "iC4_cd", "nC4_cd", "iC5_cd", "nC5_cd", "C6plus_cd", "CO2_cd", "N2_cd", "H2S_cd", "H2O_cd", "GHV")

		//Coonect to DB and retrieve the Scenario Name
		var scenario_name = ""
		val conn = DB.getConnection()
	  	try {
	  		val stmt = conn.createStatement
		    val rs = stmt.executeQuery(s"SELECT * from Scenario where Scenario_id='$id'")
		    while (rs.next()) {		       
		       scenario_name = rs.getString("scenario")
		    } 
	  	} finally {
	  	//Do not close the connection yet
		}


		outString += "\t\t99\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "Scenario Description" +"\n"
		var query = s"""SELECT 
		  cast(Network_id as varchar) + '\t' 
		+ cast(Scenario_id as varchar) + '\t' 
		+ cast(Object_id as varchar) + '\t' 
		+ cast(Data_type as varchar) + '\t' 
		+ CONVERT(CHAR(11),[Time_h],101) + CONVERT(CHAR( 5),[Time_h],114) + '\t'
		+ cast(No_pipe as varchar) + '\t'
		+ isnull(cast(CondLimit as varchar),'') + '\t'
		+ isnull(cast(Index_Node1 as varchar),'') + '\t'
		+ isnull(cast(Index_Node2 as varchar),'') + '\t'
		+ rtrim(isnull(cast(Node1 as varchar),'')) + '\t'
		+ rtrim(isnull(cast(Node2 as varchar),'')) + '\t'
		+ isnull(cast(Valve as varchar),'') + '\t'
		+ isnull(cast([Compressor pressure out] as varchar),'') + '\t'
		+ isnull(cast([Compressor Rate] as varchar),'') + '\t'
		+ isnull(cast([Rog] as varchar),'') + '\t'
		+ isnull(cast([Rod] as varchar),'') + '\t'
		+ isnull(cast([Pressure_N1] as varchar),'') + '\t'
		+ isnull(cast([Pressure_N2] as varchar),'') + '\t'
		+ isnull(cast([Flow_g] as varchar),'') + '\t'
		+ isnull(cast([Flow_cd] as varchar),'') + '\t'
		+ isnull(cast([Temperature] as varchar),'') + '\t'
		+ isnull(cast([Lpk_g] as varchar),'') + '\t'
		+ isnull(cast([Lpk_cd] as varchar),'') + '\t'
		+ isnull(cast([C1_g] as varchar),'') + '\t'
		+ isnull(cast([C2_g] as varchar),'') + '\t'
		+ isnull(cast([C3_g] as varchar),'') + '\t'
		+ isnull(cast([iC4_g] as varchar),'') + '\t'
		+ isnull(cast([nC4_g] as varchar),'') + '\t'
		+ isnull(cast([iC5_g] as varchar),'') + '\t'
		+ isnull(cast([nC5_g] as varchar),'') + '\t'
		+ isnull(cast([C6plus_g] as varchar),'') + '\t'
		+ isnull(cast([CO2_g] as varchar),'') + '\t'
		+ isnull(cast([N2_g] as varchar),'') + '\t'
		+ isnull(cast([H2S_g] as varchar),'') + '\t'
		+ isnull(cast([C1_cd] as varchar),'') + '\t'
		+ isnull(cast([C2_cd] as varchar),'') + '\t'
		+ isnull(cast([C3_cd] as varchar),'') + '\t'
		+ isnull(cast([iC4_cd] as varchar),'') + '\t'
		+ isnull(cast([nC4_cd] as varchar),'') + '\t'
		+ isnull(cast([iC5_cd] as varchar),'') + '\t'
		+ isnull(cast([nC5_cd] as varchar),'') + '\t'
		+ isnull(cast([C6plus_cd] as varchar),'') + '\t'
		+ isnull(cast([CO2_cd] as varchar),'') + '\t'
		+ isnull(cast([N2_cd] as varchar),'') + '\t'
		+ isnull(cast([H2S_cd] as varchar),'') + '\t'
		+ isnull(cast([H2O_cd] as varchar),'') + '\t'
		+ isnull(cast([GHV] as varchar),'')
		 as text from Data_scenario where Scenario_id='$id'
		 order by Data_type asc, Time_h asc, No_pipe asc"""
		
	  	try {
	  		val stmt = conn.createStatement
		    val rs = stmt.executeQuery(query)
		    while (rs.next()) {		       
		       var s = ""
		       outString += rs.getString("text")       
		       outString += "\n"
		    } 
	  	} finally {
	  	conn.close()
		}
		  //Write Scenario file
		  	val pw = new PrintWriter(new File(base_path + id + ".txt" ))
			pw.write(outString)
			pw.close
		  //Write Query to file
		  	//val qw = new PrintWriter(new File("F:\\data\\igbo\\scenarios2\\" + id + ".query" ))
			//qw.write(query)
			//qw.close
		  //outString += ""
		  Ok(scenario_name)
	}


	val businessrulesForm = Form(
		tuple(
			"start_date" -> text,
			"end_date"	 -> text
			)
		)

	def db_load_business_rules = Action { implicit request =>
		val (start_date, end_date) = businessrulesForm.bindFromRequest.get
		val conn = DB.getConnection()
		//var outString = "date_time\tobject_id\tnomination\n"
		var outString   = ""
		try {
				val stmt = conn.createStatement
				//replace main w dbo.etapa1
				//val query = s"""EXEC dbo.etapa1 @startDate='$start_date', @endDate='$end_date'"""
				val query = s"""EXEC main '$start_date','$end_date'"""
				val rs = stmt.executeUpdate(query)
				val query2 = s"""Select * from availability_daily_mod where date_time>='$start_date' and date_time<='$end_date' 
				"""
				val rs2 = stmt.executeQuery(query2)
				while (rs2.next()) {
			       val date_time  = rs2.getString("date_time")
			       val object_id  = rs2.getString("object_id")
			       val availability = rs2.getString("availability_daily")
			       outString  += s"$date_time\t$object_id\t$availability\n"
			    }

			    //Add Demand
			    val query3 = s"""Select * from demand_daily_mod where date_time>='$start_date' and date_time<='$end_date' 
				"""
				val rs3 = stmt.executeQuery(query3)
				while (rs3.next()) {
			       val date_time  = rs3.getString("date_time")
			       val object_id  = rs3.getString("object_id")
			       val demand = rs3.getString("demand_daily")
			       outString  += s"$date_time\t$object_id\t$demand\n"
			    }

			} finally {
				conn.close()
			}
		Ok(outString)
	}

	val saveForm = Form(
	  tuple(
	    "scenario_filename" -> text,
	    "scenario_comments" -> text,
	    "data_start" -> text,
	    "data_stop" -> text,
	    "id_initial_scenario" -> text,
	    "scenario_type" -> text,
	    "scenario_data" -> text
	  )
	)

	def db_save_scenario = Action { implicit request =>
		val (scenario_filename, scenario_comments, data_start, data_stop, id_initial_scenario, scenario_type, scenario_data) = saveForm.bindFromRequest.get
		val conn = DB.getConnection()
		var qq = ""
		var id = "0"
		try {
	  		val stmt = conn.createStatement
			val query = s"""INSERT INTO Scenario 
			(Network_id, id_user, id_initial_scenario, scenario, data_start, data_stop, simulation, Object_id)
			values ('1','1','3','$scenario_comments','$data_start', '$data_stop','0','$scenario_type')
			"""
			val rs = stmt.executeUpdate(query)
			val query2 = s"""Select max(Scenario_id) as Scenario_id from Scenario 
			"""
			val rs2 = stmt.executeQuery(query2)
			while (rs2.next()) {
		       id = rs2.getString("Scenario_id")
		    }



		    //Load via data
		    scenario_data.split("\n")foreach(q => {
		    	val ins = s"""INSERT INTO Data_scenario (Network_id,Scenario_id,Object_id,Data_type,Time_h,No_pipe,CondLimit,Index_Node1,
		    	Index_Node2, Node1, Node2, Valve, [Compressor pressure out], [Compressor Rate], Rog, Rod, Pressure_N1, Pressure_N2, Flow_g, Flow_cd, Temperature, Lpk_g, Lpk_cd, C1_g, C2_g, C3_g, iC4_g,    nC4_g,    iC5_g,
		    	nC5_g,    C6plus_g,    CO2_g,    N2_g,    H2S_g,    C1_cd,    C2_cd,    C3_cd,    iC4_cd, nC4_cd,    iC5_cd,    nC5_cd,
		    	C6plus_cd,    CO2_cd,    N2_cd,    H2S_cd,   H2O_cd, GHV)
		    	VALUES ('0', '$id', $q)
		    	"""
		    	//println(ins)
		    	val ins_r = stmt.executeUpdate(ins)
		    	})


		  	} finally {
		  	conn.close()
			}
		Ok(id)
	}


	def db_save_results = Action { implicit request =>
		val (scenario_filename, scenario_comments, data_start, data_stop, id_initial_scenario, scenario_type, scenario_data) = saveForm.bindFromRequest.get
		println("Scenario:" + scenario_filename)
		//println(scenario_data)
		val conn = DB.getConnection()
		var qq = ""
		var id = scenario_filename
		try {
	  		val stmt = conn.createStatement
			val query = s"""DELETE from Data_scenario where Data_type='2' and Scenario_id='$scenario_filename'
			"""
			val rs = stmt.executeUpdate(query)

		    //Save data
		    scenario_data.split("\n").foreach(q => {
		    	val ins = s"""INSERT INTO Data_scenario (Network_id,Scenario_id,Object_id,Data_type,Time_h,No_pipe,CondLimit,Index_Node1,
		    	Index_Node2, Node1, Node2, Valve, [Compressor pressure out], [Compressor Rate], Rog, Rod, Pressure_N1, Pressure_N2, Flow_g, Flow_cd, Temperature, Lpk_g, Lpk_cd, C1_g, C2_g, C3_g, iC4_g,    nC4_g,    iC5_g,
		    	nC5_g,    C6plus_g,    CO2_g,    N2_g,    H2S_g,    C1_cd,    C2_cd,    C3_cd,    iC4_cd, nC4_cd,    iC5_cd,    nC5_cd,
		    	C6plus_cd,    CO2_cd,    N2_cd,    H2S_cd,   H2O_cd, GHV)
		    	VALUES ('0', '$id', $q)
		    	"""
		    	//println(ins)
		    	val ins_r = stmt.executeUpdate(ins)
		    	})

		    //Update the simulation field
		    val q3 = s"""UPDATE Scenario set simulation='1' where Scenario_id='$id'"""
		    val rs3 = stmt.executeUpdate(q3)

		  	} finally {
		  	conn.close()
			}
		Ok(id)
	}

	def db_insert = Action { implicit request =>
		var id = "0"
		val conn = DB.getConnection()
	  	try {
	  		val stmt = conn.createStatement
			val query = s"""INSERT INTO Scenario 
			(Network_id, id_user, id_initial_scenario, scenario, data_start, data_stop, simulation)
			values ('1','1','3','Test 9','20150101 00:00:00', '20150102 00:00:00','0')
			"""
			val rs = stmt.executeUpdate(query)
			val query2 = s"""Select Scenario_id from Scenario 
			where scenario = 'Test 9' order by Network_id asc
			"""
			val rs2 = stmt.executeQuery(query2)
			while (rs2.next()) {

		       id = rs2.getString("Scenario_id")
		    }

		    //Load via addBatch

		  	} finally {
		  	conn.close()
			}

		Ok(id)
	}

	def db_admin_field_list = Action { implicit request =>
	val conn = DB.getConnection()
	var outString = "id_field\tobject_id\tfield\tmin\tmax\n"
	try {
			val stmt = conn.createStatement

			val query2 = s"""Select * from field 
			"""
			val rs2 = stmt.executeQuery(query2)
			while (rs2.next()) {
		       val id_field  	= rs2.getString("id_field")
		       val object_id  	= rs2.getString("object_id")
		       val field 	 	= rs2.getString("field")
		       val min 		 	= rs2.getString("min")
		       val max 		 	= rs2.getString("max")
		       outString  += s"$id_field\t$object_id\t$field\t$min\t$max\n"
		    }

		} finally {
			conn.close()
		}
	Ok(outString)
	}

	def db_admin_psc_list = Action { implicit request =>
	val conn = DB.getConnection()
	var outString = "id_psc\tpsc\n"
	try {
			val stmt = conn.createStatement

			val query2 = s"""Select * from psc 
			"""
			val rs2 = stmt.executeQuery(query2)
			while (rs2.next()) {
		       val id_psc  	= rs2.getString("id_psc")
		       val psc  	= rs2.getString("psc")
		       outString  += s"$id_psc\t$psc\n"
		    }

		} finally {
			conn.close()
		}
	Ok(outString)
	}

	def dl_scenario(id: String) = Action {
		var outString = ""
		Source
		  .fromFile(base_path + id + ".txt")
		  .getLines
		  .foreach { line =>
		    outString += line.replace('\t', ',') + '\n'
		  }
	  	//scala.tools.nsc.io.File("F:\\data\\igbo\\scenarios2\\" + id + ".csv").writeAll(outString)
	  	Some(new PrintWriter(base_path + id + ".csv")).foreach{ p => p.write(outString); p.close }

	  Ok.sendFile(
	    content = new java.io.File(base_path + id + ".csv"),
	    inline = false
	  )
	}

}
/*
 ,[Scenario_id]
      ,[Object_id]
      ,[Data_type]
      ,[Time_h]
      ,[No_pipe]
      ,[CondLimit]
      ,[Index_Node1]
      ,[Index_Node2]
      ,[Node1]
      ,[Node2]
      ,[Valve]
      ,[Compressor pressure out]
      ,[Compressor Rate]
      ,[Rog]
      ,[Rod]
      ,[Pressure_N1]
      ,[Pressure_N2]
      ,[Flow_g]
      ,[Flow_cd]
      ,[Temperature]
      ,[Lpk_g]
      ,[Lpk_cd]
      ,[C1_g]
      ,[C2_g]
      ,[C3_g]
      ,[iC4_g]
      ,[nC4_g]
      ,[iC5_g]
      ,[nC5_g]
      ,[C6plus_g]
      ,[CO2_g]
      ,[N2_g]
      ,[H2S_g]
      ,[C1_cd]
      ,[C2_cd]
      ,[C3_cd]
      ,[iC4_cd]
      ,[nC4_cd]
      ,[iC5_cd]
      ,[nC5_cd]
      ,[C6plus_cd]
      ,[CO2_cd]
      ,[N2_cd]
      ,[H2S_cd]
      ,[H2O_cd]
      ,[GHV]
*/