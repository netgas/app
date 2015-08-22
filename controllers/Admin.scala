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



object Admin extends Controller {


  
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

	/**
	 * 
	 *
	 */

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

	def set_base_scenario(id: String) = Action { implicit request =>
		val conn = DB.getConnection()
		try {
			//Set to 0 all scenarios for the same period
			val stmt = conn.createStatement
			val query = s"""update Scenario set base_plan=0 
						where data_start=(select data_start from Scenario where Scenario_id='$id') 
						and data_stop=(select data_stop from Scenario where Scenario_id='$id')"""
		    val rs = stmt.executeUpdate(query)
		    //Set to 1 the specified scenario
		    val query2 = s"""UPDATE Scenario set base_plan=1
		    			WHERE Scenario_id='$id'"""
		    val rs2 = stmt.executeUpdate(query2)
		} finally {
			conn.close()
		}

		Ok("0")
	}

	/**
	 * 
	 * ucgr
	 */

	def db_admin_table_list(table_name: String) = Action { implicit request =>

		//Determine the table structure based on the table's name
		val table_fields: List[String] = table_name match {
			case "psc" 						=> List("id_psc","psc")
			case "field" 					=> List("id_field", "object_id", "field", "min", "max")
			case "ucgr"						=> List("id_ucgr", "object_id", "field", "ucgr")
			case "specification"			=> List("id_specification", "specification", "min_max", "id_composition", "coefficient")
			case "gas_specification"		=> List("id_gas_specification", "id_specification", "object_id", "gas_specification")
			case "node_pressure"			=> List("id_node_pressure", "index_node", "node", "pmin", "pmax")
			case "capacitate"				=> List("id_capacitate", "name", "min", "max", "suma")
			case "capacitate_conducte"		=> List("id_capacitate_conducte", "id_capacitate", "object_id")
			case "demand"					=> List("id_demand", "object_id", "pgsa", "name")
			case "availability_prediction"	=> List("id_availability_prediction", "date_time", "object_id", "availability_prediction")
			case "availability_daily"		=> List("id_availability_daily", "date_time", "object_id", "availability_daily")
			case "demand_daily"				=> List("id_demand_daily", "date_time", "object_id", "demand_daily")
			case "demand_prediction"		=> List("id_demand_prediction", "date_time", "object_id", "demand_prediction")
			case _ 		=> List()
		}

		val conn = DB.getConnection()
		var outString = ""
		table_fields.foreach(f => { outString += f + "\t"; })
		outString += "\n"

		try {
				val stmt = conn.createStatement
				val query = s"""Select * from $table_name 
				"""
				val rs = stmt.executeQuery(query)
				while (rs.next()) {
					table_fields.foreach(f => { val x = rs.getString(f); outString += x + "\t"; })
					outString += "\n"
			    }

			} finally {
				conn.close()
			}
		Ok(outString)
	}

	val fieldForm = Form(
	  tuple(
	    "id_field" -> text,
	    "field" -> text,
	    "min" -> text,
	    "max" -> text
	  )
	)

	def update_record_field = Action { implicit request =>
		val (id_field, field, min, max) = fieldForm.bindFromRequest.get
		val conn = DB.getConnection()
		try {
			//Write to DB
			val stmt = conn.createStatement
			val query = s"""UPDATE field set field='$field', min='$min', max='$max' where id_field='$id_field'"""

		    val rs = stmt.executeUpdate(query)
		} finally {
			conn.close()
		}

	Ok("0")

	}

	val pscForm = Form(
	  tuple(
	    "id_psc" -> text,
	    "psc" 	-> text
	  )
	)

	def update_record_psc = Action { implicit request =>
		val (id_psc, psc) = pscForm.bindFromRequest.get
		val conn = DB.getConnection()
		try {
			//Write to DB
			val stmt = conn.createStatement
			val query = s"""UPDATE psc set psc='$psc' where id_psc='$id_psc'"""
		    val rs = stmt.executeUpdate(query)
		} finally {
			conn.close()
		}

	Ok("0")

	}

	val ucgrForm = Form(
	  tuple(
	    "id_ucgr" -> text,
	    "ucgr" 	-> text
	  )
	)

	def update_record_ucgr = Action { implicit request =>
		val (id_ucgr, ucgr) = ucgrForm.bindFromRequest.get
		val conn = DB.getConnection()
		try {
			//Write to DB
			val stmt = conn.createStatement
			val query = s"""UPDATE ucgr set ucgr='$ucgr' where id_ucgr='$id_ucgr'"""
		    val rs = stmt.executeUpdate(query)
		} finally {
			conn.close()
		}

	Ok("0")

	}

	val node_pressureForm = Form(
	  tuple(
	    "id_node_pressure" -> text,
	    "pmin" 	-> text,
	    "pmax" 	-> text
	  )
	)

	def update_record_node_pressure = Action { implicit request =>
		val (id_node_pressure, pmin, pmax) = node_pressureForm.bindFromRequest.get
		val conn = DB.getConnection()
		try {
			//Write to DB
			val stmt = conn.createStatement
			val query = s"""UPDATE node_pressure set pmin='$pmin',pmax='$pmax' where id_node_pressure='$id_node_pressure'"""
		    val rs = stmt.executeUpdate(query)
		} finally {
			conn.close()
		}

	Ok("0")

	}

	val gas_specificationForm = Form(
	  tuple(
	    "id_gas_specification" 	-> text,
	    "gas_specification" 	-> text
	  )
	)

	def update_record_gas_specification = Action { implicit request =>
		val (id_gas_specification, gas_specification) = gas_specificationForm.bindFromRequest.get
		val conn = DB.getConnection()
		try {
			//Write to DB
			val stmt = conn.createStatement
			val query = s"""UPDATE gas_specification set gas_specification='$gas_specification' where id_gas_specification='$id_gas_specification'"""
		    val rs = stmt.executeUpdate(query)
		} finally {
			conn.close()
		}

	Ok("0")

	}



	val specificationForm = Form(
	  tuple(
	    "id_specification" 	-> text,
	    "min_max" 	-> text
	  )
	)

	def update_record_specification = Action { implicit request =>
		val (id_specification, min_max) = specificationForm.bindFromRequest.get
		val conn = DB.getConnection()
		try {
			//Write to DB
			val stmt = conn.createStatement
			val query = s"""UPDATE specification set min_max='$min_max' where id_specification='$id_specification'"""
		    val rs = stmt.executeUpdate(query)
		} finally {
			conn.close()
		}

	Ok("0")

	}

	val capacitateForm = Form(
	  tuple(
	    "id_capacitate" 	-> text,
	    "name" 	-> text,
	    "min" 	-> text,
	    "max" 	-> text
	  )
	)

	def update_record_capacitate = Action { implicit request =>
		val (id_capacitate, name, min, max) = capacitateForm.bindFromRequest.get
		val conn = DB.getConnection()
		try {
			//Write to DB
			val stmt = conn.createStatement
			val query = s"""UPDATE capacitate set name='$name', min='$min', max='$max' where id_capacitate='$id_capacitate'"""
		    val rs = stmt.executeUpdate(query)
		} finally {
			conn.close()
		}

	Ok("0")

	}


}
