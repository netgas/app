package controllers

import anorm._

import java.io._
import java.sql._
import java.text.SimpleDateFormat

import models.Temperatura
import models.DAO.TemperaturaDAO
import models.Strada
import models.DAO.StradaDAO
import models.Localitate
import models.DAO.LocalitateDAO

import play.api.libs.json.Writes
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import play.api.libs.json.Json.JsValueWrapper
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Results._
import play.api.libs.json.{JsNull,Json,JsString,JsValue}
import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._


import scala.concurrent.ExecutionContext.Implicits.global
import scala.io._
import scala.math.BigDecimal

import jp.t2v.lab.play2.auth.{AuthenticationElement, LoginLogout}

object Nomenclatura extends Controller with AuthenticationElement with AuthConfigImpl {

	//format data baza de date
	val formatDateDB = new java.text.SimpleDateFormat("yyyy-MM-dd")
	//format data aplicatie
	val formatDateApp = new java.text.SimpleDateFormat("dd-MM-yyyy")

	//formular temperatura
	val temperaturaForm = Form {
		tuple(
			"id_temperatura" ->optional(number),
			"data" -> nonEmptyText,
			"valoare" -> nonEmptyText
		)
	}

	//formular strada
	val stradaForm = Form {
		tuple(
			"id_strada" -> optional(number),
			"denumire" -> nonEmptyText,
			"nr_clienti" -> nonEmptyText,
			"material" -> nonEmptyText,
			"dn" -> nonEmptyText,
			"lungime" -> nonEmptyText,
			"regim_presiune" -> nonEmptyText
		)
	}

	//formular localitate
	val localitateForm = Form {
		tuple(
			"id_localitate" -> optional(number),
			"denumire" -> nonEmptyText
		)
	}

	//json writer temperatura
	implicit val temperaturaWrites = new Writes[Temperatura] {
		def writes(temperatura: Temperatura) = Json.obj(
			"id" -> temperatura.id_temperatura,
			"data" -> Json.arr(
				temperatura.id_temperatura,
				formatDateApp.format(temperatura.data),
				temperatura.valoare
			)
		)
	}

	//json writer strada
	implicit val stradaWrites = new Writes[Strada] {
		def writes(strada: Strada) = Json.obj(
			"id" -> strada.id_strada,
			"data" -> Json.arr(
				strada.id_strada,
				strada.denumire,
				strada.nr_clienti,
				strada.material,
				strada.dn,
				strada.lungime,
				strada.regim_presiune
			)
		)
	}

	//json writer localitate
	implicit val localitateWrites = new Writes[Localitate] {
		def writes(localitate: Localitate) = Json.obj(
			"id" -> localitate.id_localitate,
			"data" -> Json.arr(
				localitate.id_localitate,
				localitate.denumire
			)
		)
	}

	def index = StackAction { implicit request =>
		Ok(views.html.nomenclatura.nindex())
	}

	def listTemperatura = StackAction { implicit request =>
		val pos = request.getQueryString("posStart").getOrElse(0).toString.toInt
		val count = request.getQueryString("count").getOrElse(10).toString.toInt

		val total_count = models.DAO.TemperaturaDAO.count()
		val temps = models.DAO.TemperaturaDAO.findAll(count, pos)

		val datag = Json.obj(
			"total_count" -> JsNumber(total_count),
			"pos" -> JsNumber(pos),
			"rows" -> Json.toJson(temps)
		)
		Ok(Json.prettyPrint(datag))
	}

	def editTemperatura = StackAction { implicit request =>
		val id = request.getQueryString("id").getOrElse(0).toString.toInt
		val d = new java.util.Date()
		val temp = models.DAO.TemperaturaDAO.findById(id).getOrElse(new Temperatura(0, d, 0))
		val data = formatDateDB.format(temp.data)

		val formData = Json.arr(
			Json.obj(
				"type" -> "settings",
				"labelWidth" -> JsNumber(75),
				"inputWidth" -> JsNumber(100)
			),
			Json.obj(
				"type" -> "calendar",
				"dateFormat" -> "%d-%m-%Y",
				"serverDateFormat" -> "%Y-%m-%d",
				"name" -> "data",
				"label" -> "Data",
				"value" -> data,
				"validate" -> "NotEmpty",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "input",
				"name" -> "valoare",
				"label" -> "Valoare",
				"value" -> temp.valoare,
				"validate" -> "validTemp" //validTemp este o functie JS care verifica ca temp (-100; 100)
			),
			Json.obj(
				"type" -> "hidden",
				"name" -> "id_temperatura",
				"value" -> temp.id_temperatura
			),
			Json.obj(
				"type" -> "button",
				"value" -> "Salveaza",
				"id" -> "save_temperatura"
			)
		)
		Ok(formData).as("text/json");
	}

	def saveTemperatura = StackAction { implicit request =>
		temperaturaForm.bindFromRequest.fold(
			formWithErrors => {
				val response = Json.obj("res"->"NOTOK", "mess"->formWithErrors.errorsAsJson);
				Ok(Json.toJson(response))
			},
			value => {
				val id_temp = value._1
				val data = formatDateDB.parse(value._2)
				val valoare = BigDecimal(value._3)

				val temp = new Temperatura(id_temp.getOrElse(0).toInt, data, valoare)
				models.DAO.TemperaturaDAO.save(temp)

				val response = Json.obj("res" -> "OK", "mess"->"Datele au fost salvate!")
				Ok(Json.toJson(response))
			}
		)
	}

	def deleteTemperatura = StackAction { implicit request =>
		val id = request.getQueryString("id").getOrElse(0).toString.toInt
		models.DAO.TemperaturaDAO.deleteById(id)
		Ok("Temperatura a fost stearsa!")
	}

	def listStrada = StackAction { implicit request =>
		val pos = request.getQueryString("posStart").getOrElse(0).toString.toInt
		val count = request.getQueryString("count").getOrElse(10).toString.toInt

		val total_count = models.DAO.StradaDAO.count()
		val strazi = models.DAO.StradaDAO.findAll(count, pos)

		val datag = Json.obj(
			"total_count" -> JsNumber(total_count),
			"pos" -> JsNumber(pos),
			"rows" -> Json.toJson(strazi)
		)
		Ok(Json.prettyPrint(datag))
	}

	def editStrada = StackAction { implicit request =>
		val id = request.getQueryString("id").getOrElse(0).toString.toInt
		val d = new java.util.Date()
		val strada = models.DAO.StradaDAO.findById(id).getOrElse(new Strada(0, "", 0, "", 0, 0, "", None, None, None, None))

		val formData = Json.arr(
			Json.obj(
				"type" -> "settings",
				"labelWidth" -> JsNumber(75),
				"inputWidth" -> JsNumber(100)
			),
			Json.obj(
				"type" -> "input",
				"name" -> "denumire",
				"label" -> "Denumire",
				"value" -> strada.denumire,
				"validate" -> "NotEmpty",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "input",
				"name" -> "nr_clienti",
				"label" -> "Nr. Clienti",
				"value" -> strada.nr_clienti,
				"validate" -> "NotEmpty,ValidInteger",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "input",
				"name" -> "material",
				"label" -> "Material",
				"value" -> strada.material,
				"validate" -> "NotEmpty",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "input",
				"name" -> "dn",
				"label" -> "DN",
				"value" -> strada.dn,
				"validate" -> "validateZeciMii"
			),
			Json.obj(
				"type" -> "input",
				"name" -> "lungime",
				"label" -> "Lungime",
				"value" -> strada.lungime,
				"validate" -> "validateZeciMii"
			),
			Json.obj(
				"type" -> "input",
				"name" -> "regim_presiune",
				"label" -> "Regim pres.",
				"value" -> strada.regim_presiune,
				"validate" -> "NotEmpty",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "hidden",
				"name" -> "id_strada",
				"value" -> strada.id_strada
			),
			Json.obj(
				"type" -> "button",
				"value" -> "Salveaza",
				"id" -> "save_strada"
			)
		)
		Ok(formData).as("text/json");
	}

	def saveStrada = StackAction { implicit request =>
		stradaForm.bindFromRequest.fold(
			formWithErrors => {
				val response = Json.obj("res"->"NOTOK", "mess"->formWithErrors.errorsAsJson);
				Ok(Json.toJson(response))
			},
			value => {
				val id_strada = value._1
				val denumire = value._2
				val nr_client = value._3.toInt
				val material = value._4
				val dn = BigDecimal(value._5)
				val lungime = BigDecimal(value._6)
				val regim_presiune = value._7
				val user_id = loggedIn.id.toInt
				val date = new java.util.Date()

				val strada = new Strada(id_strada.getOrElse(0).toInt, denumire, nr_client, material, dn, lungime, regim_presiune, Some(user_id), Some(date), Some(user_id), Some(date))
				models.DAO.StradaDAO.save(strada)

				val response = Json.obj("res" -> "OK", "mess"->"Datele au fost salvate!")
				Ok(Json.toJson(response))
			}
		)
	}

	def deleteStrada = StackAction { implicit request =>
		val id = request.getQueryString("id").getOrElse(0).toString.toInt
		models.DAO.StradaDAO.deleteById(id)
		Ok("Strada a fost stearsa!")
	}

	def comboStrada = StackAction { implicit request =>
		val text = request.getQueryString("mask").getOrElse("").toString
		val strazi = models.DAO.StradaDAO.findByDenumire(text)
		val xml = <complete add="true">
				{strazi.map(s => <option value={s.id_strada.toString}>{s.denumire}</option>)}
			</complete>
		Ok(xml)
	}

	def listLocalitate = StackAction { implicit request =>
		val pos = request.getQueryString("posStart").getOrElse(0).toString.toInt
		val count = request.getQueryString("count").getOrElse(10).toString.toInt

		val total_count = models.DAO.LocalitateDAO.count()
		val localitati = models.DAO.LocalitateDAO.findAll(count, pos)

		val data = Json.obj(
			"total_count" -> JsNumber(total_count),
			"pos" -> JsNumber(pos),
			"rows" -> Json.toJson(localitati)
		)

		Ok(Json.prettyPrint(data))
	}

	def editLocalitate = StackAction { implicit request =>
		val id = request.getQueryString("id").getOrElse(0).toString.toInt
		val localitate = models.DAO.LocalitateDAO.findById(id).getOrElse(new Localitate(0, ""))

		val formData = Json.arr(
			Json.obj(
				"type" -> "settings",
				"labelWidth" -> JsNumber(75),
				"inputWidth" -> JsNumber(100)
			),
			Json.obj(
				"type" -> "input",
				"name" -> "denumire",
				"label" -> "Denumire",
				"value" -> localitate.denumire,
				"validate" -> "NotEmpty",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "hidden",
				"name" -> "id_localitate",
				"value" -> localitate.id_localitate
			),
			Json.obj(
				"type" -> "button",
				"value" -> "Salveaza",
				"id" -> "save_localitate"
			)
		)
		Ok(formData).as("text/json");
	}

	def saveLocalitate = StackAction { implicit request =>
		localitateForm.bindFromRequest.fold(
			formWithErrors => {
				val response = Json.obj("res"->"NOTOK", "mess"->formWithErrors.errorsAsJson)
				Ok(Json.toJson(response))
			},
			value => {
				val id_localitate = value._1
				val denumire = value._2

				val localitate = new Localitate(id_localitate.getOrElse(0).toInt, denumire)
				models.DAO.LocalitateDAO.save(localitate)

				val response = Json.obj("res"->"OK", "mess"->"Datele au fost salvate!")
				Ok(Json.toJson(response))
			}
		)
	}

	def deleteLocalitate = StackAction { implicit request =>
		val id = request.getQueryString("id").getOrElse(0).toString.toInt
		models.DAO.LocalitateDAO.deleteById(id)
		Ok("Localitatea a fost stearsa!")
	}
}
