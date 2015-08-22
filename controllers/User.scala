package controllers

import jp.t2v.lab.play2.auth.{AuthElement, LoginLogout}

import models.Account

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Writes
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import play.api.libs.json.Json.JsValueWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object User extends Controller with LoginLogout with AuthElement with AuthConfigImpl {

	val userForm = Form {
		tuple(
			"id" -> optional(number),
			"name" -> nonEmptyText,
			"password" -> nonEmptyText,
			"role" -> nonEmptyText
		)
	}

	val loginForm = Form {
		tuple(
			"name" -> nonEmptyText,
			"password" -> nonEmptyText
		)
	}

	val allUsers = models.DAO.AccountDAO.findAll()

	implicit val userWrites = new Writes[Account] {
		def writes(user: Account) = Json.obj(
	      "id" -> user.id,
				"data" -> Json.arr(
					user.id,
					user.name,
					user.role
				)
	    )
	}

	def login = Action { implicit request =>
		Ok(views.html.user.login(loginForm))
	}

	def authentificate = Action.async { implicit request =>
		loginForm.bindFromRequest.fold(
			formWithErrors => Future.successful(BadRequest(views.html.user.login(formWithErrors))),
			formData => {
				val name = formData._1
				val password = formData._2
				val auth = models.DAO.AccountDAO.authentificate(name, password)
				auth match {
					case Some(user: Account) => gotoLoginSucceeded(user.id)
					case _ => Future.successful(BadRequest(views.html.user.login(loginForm.fillAndValidate(formData).withGlobalError("Date incorecte!"))))
				}

			}
		)
	}

	def logout = Action.async { implicit request =>
		gotoLogoutSucceeded.map(_.flashing(
			"success" -> "Ati fost delogat!"
			).removingFromSession("rememberme"))

	}

	def users = StackAction(AuthorityKey -> ("Administrator" :: Nil)) { implicit request =>
		Ok(views.html.user.users())
	}

	//afiseaza utilizatori ca json pentru a fi afisati in dhtmlx
	def listAllGrid = StackAction(AuthorityKey -> ("Administrator" :: Nil)) { implicit request =>
		val pos = request.getQueryString("posStart").getOrElse(0).toString.toInt
		val count = request.getQueryString("count").getOrElse(10).toString.toInt

		val total_count = models.DAO.AccountDAO.count()
		val accounts = models.DAO.AccountDAO.findAll(count, pos)

		val data = Json.obj(
			"total_count" -> JsNumber(total_count),
			"pos" -> JsNumber(pos),
			"rows" -> Json.toJson(accounts)
		)

		Ok(Json.prettyPrint(data))
	}

	//adaugare/editare utilizator nou
	def edit = StackAction(AuthorityKey -> ("Administrator" :: Nil)) { implicit request =>

		val userID = request.getQueryString("id").getOrElse(0).toString.toInt

		val user = models.DAO.AccountDAO.findById(userID).getOrElse(new Account(0, "", "", "Administrator"))

		val formData = Json.arr(
			Json.obj(
				"type" -> "settings",
				"labelWidth" -> JsNumber(75),
				"inputWidth" -> JsNumber(150)
			),
			Json.obj(
				"type" -> "hidden",
				"name" -> "id",
				"value" -> user.id
			),
			Json.obj(
				"type" -> "input",
				"name" -> "name",
				"label" -> "Nume",
				"value" -> user.name,
				"position" -> "label-left",
				"validate" -> "NotEmpty",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "password",
				"name" -> "password",
				"label" -> "Parola",
				"value" -> user.password,
				"position" -> "label-left",
				"validate" -> "NotEmpty",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "select",
				"name" -> "role",
				"label" -> "Rol",
				"value" -> user.role,
				"options" -> Json.arr(
								Json.obj("text"->"Administrator", "value"->"Administrator"),
								Json.obj("text"->"User", "value"->"User")
							),
				"position" -> "label-left",
				"validate" -> "NotEmpty",
				"required" -> "true"
			),
			Json.obj(
				"type" -> "multiselect",
				"name" -> "localitati[]",
				"label" -> "Localitati",
				"required" -> "true",
				"connector" -> JsString(routes.User.getOptionsLocalitati.toString + "?role=" + user.role.toString + ""),
				"size" -> 8
			),
		  Json.obj(
		      "type" -> "button",
		      "value" -> "Salveaza",
		      "id"->"save_action"
		    )
		)

  		Ok(formData).as("text/json");
	}

	def save = StackAction(AuthorityKey -> ("Administrator" :: Nil)) { implicit request =>
		userForm.bindFromRequest.fold(
			formWithErrors => Ok("date eronate"),
			value => {
				println(value);
				// val id = value._1.getOrElse(0)
				// val name = value._2
				// val password = value._3
				// val role = value._4
				// if(id > 0) {
				// 	var user = models.DAO.AccountDAO.findById(id).getOrElse(new Account(0, "", "", ""))
				// 	models.DAO.AccountDAO.update(user, name, password, role)
				// 	Ok("Datele au fost modificate")
				// }else{
				// 	val user = new Account(0, name, password, role)
				// 	val existingAccount = models.DAO.AccountDAO.findByName(name)
				// 	existingAccount match {
				// 		case Some(result: Account) => Ok("Exista deja un utilizator cu acest nume!")
				// 		case None => {
				// 			models.DAO.AccountDAO.insert(user)
				// 			Ok("Utilizatorul a fost adaugat!")
				// 		}
				// 	}
				// }
				Ok("TEST");
			}
		)
	}

	def delete = StackAction(AuthorityKey -> ("Administrator" :: Nil)) { implicit request =>
		val userID = request.getQueryString("id").getOrElse(0).toString.toInt
		val deletedRow = models.DAO.AccountDAO.deleteById(userID)
		Ok("Utilizatorul a fost sters!")
	}

	def getOptionsLocalitati = StackAction(AuthorityKey -> ("Administrator" :: Nil)) { implicit request =>
		val role = request.getQueryString("role").getOrElse("").toString
		val currentUser = loggedIn

		var localitati = models.DAO.AccountDAO.getLocalitati(currentUser.id)

		if(role == "Administrator") {
			localitati = models.DAO.LocalitateDAO.findAll(10000, 0)
		}
		if(role == "User") {
			localitati = models.DAO.AccountDAO.getLocalitati(currentUser.id)
		}
		val xml = <data>
								{localitati.map(l => <item value={l.id_localitate.toString} label={l.denumire.toString}/>)}
							</data>
		Ok(xml)
	}
}
