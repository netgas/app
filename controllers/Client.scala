package controllers

import anorm._

import java.io._
import java.sql._
import java.text.SimpleDateFormat

import models.Client
import models.DAO.ClientDAO
import models.Cadresa
import models.DAO.CadresaDAO
import models.Contract
import models.DAO.ContractDAO
import models.Strada
import models.DAO.StradaDAO

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

import jp.t2v.lab.play2.auth.{AuthenticationElement, LoginLogout}


/**
 * Created by Net Gas on 31.07.2015.
 */
object Client extends Controller with AuthenticationElement with AuthConfigImpl {

	//format data baza de date
	val formatDateDB = new java.text.SimpleDateFormat("yyyy-MM-dd")
	//format data aplicatie
	val formatDateApp = new java.text.SimpleDateFormat("dd-MM-yyyy")

	//formular client nou
	val clientForm = Form {
		tuple(
			"id_client" ->optional(number),
			"nume" -> nonEmptyText,
			"prenume" -> nonEmptyText,
			"cod_client" -> nonEmptyText,
			"email" -> nonEmptyText,
			"telefon" -> nonEmptyText,
<<<<<<< HEAD
			//"tip" -> nonEmptyText
			//"tip" -> nonEmpty2Text
=======
			"tip" -> nonEmptyText
>>>>>>> origin/master
		)
	}

	//formular adresa noua
	val adresaForm = Form {
		tuple(
			"id_cadresa" -> optional(number),
			"id_client" -> number,
			"tip_adresa" -> nonEmptyText,
			"strada_id" -> number,
			"numar" -> optional(text),
			"bl" -> optional(text),
			"sc" -> optional(text),
			"ap" -> optional(text),
			"et" -> optional(text),
			"observatii" -> optional(text)
		)
	}

	//formular contract nou
	val contractForm = Form {
		tuple(
			"id_contract" -> optional(number),
			"id_client" -> number,
			"numar" -> nonEmptyText,
			"data" -> nonEmptyText
		)
	}

	implicit val clientWrites = new Writes[Client] {
		def writes(client: Client) = Json.obj(
			"id" -> client.id_client,
			"data" -> Json.arr(
				client.id_client,
				client.nume,
				client.prenume,
				client.cod_client,
				client.email,
				client.telefon,
				client.tip
			)
		)
	}

	//json write contract
	implicit val contractWrites = new Writes[Contract] {
		def writes(contract: Contract) = Json.obj(
			"id" -> contract.id_contract,
			"numar" -> contract.numar,
			"data" -> formatDateApp.format(contract.data)
		)
	}

	//index client
	def cindex = StackAction { implicit request =>
		Ok(views.html.client.cindex())
	}


	//list clients in grid
	def listgrid = Action { implicit request =>
		val pos = request.getQueryString("posStart").getOrElse(0).toString.toInt
		val count = request.getQueryString("count").getOrElse(10).toString.toInt

		val total_count = models.DAO.ClientDAO.count()

		val clients = models.DAO.ClientDAO.findAll(count, pos)

		val datag = Json.obj(
			"total_count" -> JsNumber(total_count),

			"pos" -> JsNumber(pos),

			"rows" -> Json.toJson(clients)
		)

		Ok(Json.prettyPrint(datag))
	}


	//client data edit
	def editclient = Action { implicit request=>
		val clientId = request.getQueryString("id").getOrElse(0).toString.toInt
		val d = new java.util.Date()
		var clDb = new Client(0,"", "", "", "", "", "", 1, None, None, None, None)
		if(clientId!=0){
			clDb = models.DAO.ClientDAO.findById(clientId)
		}

		val formData: JsArray =
			 Json.arr(
					Json.obj(
						"type" -> "input",
						"name"->"nume",
						"label" -> "Nume",
						"value" ->clDb.nume,
						"validate"->"NotEmpty",
						"required"->"true"
					),

					Json.obj(
						"type" -> "input",
						"label" -> "Prenume",
						"name"->"prenume",
						"value" -> clDb.prenume,
						"validate"->"NotEmpty",
						"required"->"true"
					),

					Json.obj(
						"type" -> "input",
						"label" -> "Cod Client",
						"name" -> "cod_client",
						"value" -> clDb.cod_client
					),

					Json.obj(
						"type" -> "input",
						"label" -> "Email",
						"name" -> "email",
						"value" -> clDb.email,
						"validate"->"ValidEmail"
					),

					Json.obj(
						"type" -> "input",
						"label" -> "Telefon",
						"name" -> "telefon",
						"value" -> clDb.telefon
					),

					Json.obj(
						"type" -> "select",
						"label" -> "Tip",
						"name" -> "tip",
						"value" -> clDb.tip,
						"options" -> Json.arr(
											Json.obj("text"->"casnic", "value"->"casnic"),
											Json.obj("text"->"noncasnic", "value"->"noncasnic")
											)
					),

					Json.obj(
						"type" -> "hidden",
						"name"->"id_client",
						"value" -> clDb.id_client
					),


					Json.obj(
						"type" -> "button",
						"value" -> "Salveaza",
						"id"->"save_action"
					)
		)

		val form = Json.arr(
			Json.obj(
				"type"->"settings",
				"position"->"label-top"
			),

			Json.obj(
				"type"->"fieldset",
				"name"->"client_form",
				"label"->"Client",
				"inputWidth"->"auto",
				"list"->formData
			)
		)

		Ok(formData).as("text/json");

	}

	//client data save
	def saveclient = StackAction { implicit request=>
		clientForm.bindFromRequest.fold(
			formWithErrors => Ok(formWithErrors.errorsAsJson),
			value=>{
				val id_client 		= value._1
				val nume      		= value._2
				val prenume   		= value._3
				val cod_client 		= value._4
				val email 			= value._5
				val telefon 		= value._6
				val tip 			= value._7
				val enable 			= 1

				if(id_client!=None) {
					val creat_de		= None
					val creat_la		= None
					val modificat_de 	= loggedIn.id.toInt
					val modificat_la	= new java.util.Date()

					val client = new Client(id_client.getOrElse(0).toInt, nume, prenume, cod_client, email, telefon, tip, enable, creat_de, creat_la, Some(modificat_de), Some(modificat_la))
					models.DAO.ClientDAO.update(client)
					// val updateRow = DB.withConnection("balotesti"){	implicit c=>
					// 	SQL("UPDATE client SET nume={nume}, prenume={prenume}  WHERE id_client={id_client}").on("nume"->nume,"prenume"->prenume,"id_client"->id_client).execute()
					// 	}
				}

				if(id_client==None){
					//daca este un client nou adauga informatii despre creare
					val creat_de		= loggedIn.id.toInt
					val creat_la		= new java.util.Date()
					val modificat_de 	= None
					val modificat_la 	= None

					val client = new Client(0, nume, prenume, cod_client, email, telefon, tip, enable, Some(creat_de), Some(creat_la), modificat_de, modificat_la)
					models.DAO.ClientDAO.insert(client)
				}

				Ok("Datele au fost salvate!!!!");

			}
		)
	}

	//stergere client TODO - trebuie stergere logica + stergerea inregistrarilor conexe
	def deletecl = Action{ implicit request=>
		val clientId = request.getQueryString("id").getOrElse(0).toString.toInt
		val updateRow = DB.withConnection("balotesti"){	implicit c=>
			SQL("DELETE FROM client WHERE id_client={clientId}").on("clientId"->clientId).execute()
		}
		Ok("Linia a fost stearsa!");
	}

	//denumire client ca JSON pentru a se afisa in header in celula detalii client
	def clientDenumire = StackAction { implicit request =>
		val client_id = request.getQueryString("client_id").getOrElse(0).toString.toInt
		val client = models.DAO.ClientDAO.findById(client_id)
		val data = Json.obj("nume" -> JsString(client.nume), "prenume" -> JsString(client.prenume))
		Ok(Json.prettyPrint(data))
	}

	//adaugare/editare adresa
	def editAdresa = StackAction { implicit request =>
		val id = request.getQueryString("id").getOrElse(0).toString.toInt
		val client_id = request.getQueryString("client_id").getOrElse(0).toString.toInt
		if(client_id == 0) {
			Ok("Selectati un client!");
		}

		val adresa = models.DAO.CadresaDAO.findById(id).getOrElse(new Cadresa(0, client_id, "", 0, "", "", "", "", "", "", 1, None, None, None, None))
		val strada = models.DAO.StradaDAO.findById(adresa.strada_id).getOrElse(new Strada(0, "", 0, "", 0, 0, "", None, None, None, None))

		val formData = Json.arr(
			Json.obj(
				"type" -> "settings",
				"labelWidth" -> JsNumber(75),
				"inputWidth" -> JsNumber(150)
			),
			Json.obj(
				"type" -> "select",
				"name" -> "tip_adresa",
				"label" -> "Tip adresa",
				"value" -> adresa.tip_adresa,
				"options" -> Json.arr(
					Json.obj("text" -> "consum", "value" -> "consum"),
					Json.obj("text" -> "corespondenta", "value" -> "corespondenta")
				),
				"validate" -> "NotEmpty",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "combo",
				"name" -> "strada",
				"label" -> "Strada",
				"value" -> strada.id_strada,
				"required" -> "true",
				"filtering" -> Json.arr("true", JsString(routes.Nomenclatura.comboStrada.toString))
			),
			Json.obj(
				"type" -> "input",
				"name" -> "numar",
				"label" -> "Numar",
				"value" -> adresa.numar
			),
			Json.obj(
				"type" -> "input",
				"name" -> "bl",
				"label" -> "Bloc",
				"value" -> adresa.bl
			),
			Json.obj(
				"type" -> "input",
				"name" -> "sc",
				"label" -> "Scara",
				"value" -> adresa.sc
			),
			Json.obj(
				"type" -> "input",
				"name" -> "ap",
				"label" -> "Apartament",
				"value" -> adresa.ap
			),
			Json.obj(
				"type" -> "input",
				"name" -> "et",
				"label" -> "Etaj",
				"value" -> adresa.et
			),
			Json.obj(
				"type" -> "input",
				"rows" -> 3,
				"name" -> "observatii",
				"label" -> "Observatii",
				"value" -> adresa.observatii
			),
			Json.obj(
				"type" -> "hidden",
				"name" -> "id_cadresa",
				"value" -> adresa.id_cadresa
			),
			Json.obj(
				"type" -> "hidden",
				"name" -> "id_client",
				"value" -> adresa.client_id
			),
			Json.obj(
				"type" -> "button",
				"value" -> "Salveaza",
				"id" -> "save_adresa"
			)
		)

		Ok(formData).as("text/json");
	}

	//listare contracte in grid - in functie de client
	def listGridContract = StackAction { implicit request =>
		val id_client = request.getQueryString("id_client").getOrElse(0).toString.toInt

		val contracts = models.DAO.ContractDAO.findByClientId(id_client)

		val data_json = Json.obj(
			"head" -> Json.arr(
				Json.obj(
					"id" -> "id",
					"width" ->50,
					"type" ->"ro" ,
					"align" -> "right",
					"sort" -> "int",
					"value" -> "ID"
				),
				Json.obj(
					"id" -> "numar",
					"width" -> 150,
					"type" -> "ro",
					"align" -> "right",
					"sort" -> "str",
					"value" -> "Numar"
				),
				Json.obj(
					"id" -> "data",
					"width" -> 150,
					"type" -> "ro",
					"align" -> "right",
					"sort" -> "date",
					"value" -> "Data"
				)
			),
			"data" -> Json.toJson(contracts)
		)

		Ok(Json.toJson(data_json))
	}

	//adaugare/editare contract
	def editContract = StackAction { implicit request =>
		val id = request.getQueryString("id").getOrElse(0).toString.toInt
		val id_client = request.getQueryString("id_client").getOrElse(0).toString.toInt
		if(id_client == 0) {
			Ok("Selectati un client!");
		}

		val d = new java.util.Date()
		val lastNumar = models.DAO.ContractDAO.getLastNumar()
		val contract = models.DAO.ContractDAO.findById(id).getOrElse(new Contract(0, id_client, lastNumar, d, 1, None, None, None, None))
		val data = formatDateDB.format(contract.data)

		val formData = Json.arr(
			Json.obj(
				"type" -> "input",
				"name" -> "numar",
				"label" -> "Numar",
				"value" -> contract.numar,
				"validate" -> "NotEmpty,ValidNumeric",
				"required" -> "true"
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
				"type" -> "hidden",
				"name" -> "id_contract",
				"value" -> contract.id_contract
			),
			Json.obj(
				"type" -> "hidden",
				"name" -> "id_client",
				"value" -> contract.id_client
			),
			Json.obj(
				"type" -> "button",
				"value" -> "Salveaza",
				"id" -> "save_contract"
			)
		)

		Ok(formData).as("text/json");
	}

	//salvare contract
	def saveContract = StackAction { implicit request =>
		contractForm.bindFromRequest.fold(
			formWithErrors => {
				val response = Json.obj("res"->"NOTOK", "mess"->formWithErrors.errorsAsJson);
				Ok(Json.toJson(response))
			},
			value => {
				val id_contract = value._1
				val id_client = value._2
				val numar = value._3
				val data = formatDateDB.parse(value._4)
				val enable = 1
				val creat_de = loggedIn.id.toInt
				val creat_la = new java.util.Date()
				val modificat_de = loggedIn.id.toInt
				val modificat_la = new java.util.Date()
				val contract = new Contract(id_contract.getOrElse(0).toInt, id_client, numar, data, enable, Some(creat_de), Some(creat_la), Some(modificat_de), Some(modificat_la));
				models.DAO.ContractDAO.save(contract)
				val response = Json.obj("res" -> "OK", "mess"->"Datele au fost salvate!")
				Ok(Json.toJson(response))
			}
		)
	}

	def disableContract = StackAction { implicit request =>
		val id = request.getQueryString("id").getOrElse(0).toString.toInt
		models.DAO.ContractDAO.disableById(id)
		Ok("Contractul a fost sters!")
	}

}
