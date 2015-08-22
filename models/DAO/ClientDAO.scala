package models.DAO

import models.Client

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._


/**
 * Created by cwn on 31.07.2015.
 */
object ClientDAO {

	val simple = {

		get[Long]("id_client")~
		get[String]("nume")~
		get[String]("prenume")~
		get[String]("cod_client")~
		get[String]("email")~
		get[String]("telefon")~
		get[String]("tip")~
		get[Int]("enable")~
		get[Option[Int]]("creat_de")~
		get[Option[java.util.Date]]("creat_la")~
		get[Option[Int]]("modificat_de")~
		get[Option[java.util.Date]]("modificat_la") map{ case id_client~nume~prenume~cod_client~email~telefon~tip~enable~creat_de~creat_la~modificat_de~modificat_la=>Client(id_client,nume,prenume, cod_client, email, telefon,tip, enable, creat_de, creat_la, modificat_de, modificat_la) }

	}

	def findAll(limit: Int = 1000, offset: Int = 0): Seq[Client]={
	  	DB.withConnection("balotesti"){
			implicit connection=>
			SQL("SELECT * FROM client ORDER BY id_client OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY").on("offset"->offset, "limit"->limit).as(simple. *)
	  	}
	}

	def count() = {
		DB.withConnection("balotesti") { implicit connection =>
			SQL("SELECT COUNT(*) FROM client").as(scalar[Long].single)
		}
	}

	def findById(id: Int): Client ={
		DB.withConnection("balotesti"){	implicit connection=>
			SQL("SELECT * FROM client where id_client={id} ").on("id"->id).as(simple.single)
	  	}
	}

	def insert(client: Client) = {
		DB.withConnection("balotesti"){ implicit connection =>
			SQL(
				"""
				INSERT INTO client (nume, prenume, cod_client, email, telefon, tip, enable, creat_de, creat_la, modificat_de, modificat_la)
				VALUES({nume}, {prenume}, {cod_client}, {email}, {telefon}, {tip}, {enable}, {creat_de}, {creat_la}, {modificat_de}, {modificat_la})
				"""
			).on("nume" -> client.nume, "prenume" -> client.prenume, "cod_client" -> client.cod_client, "email" -> client.email, "telefon" -> client.telefon, "tip" -> client.tip, "enable" -> client.enable, "creat_de" -> client.creat_de, "creat_la" -> client.creat_la, "modificat_de" -> client.modificat_de, "modificat_la" -> client.modificat_la)
				.executeInsert(scalar[scala.math.BigDecimal].single)
		}
	}

	def update(client: Client) = {
		//daca modificam un client, nu ii updatam info creare, si enable (poate o sa modificam un client disable???)
		DB.withConnection("balotesti") { implicit connection =>
			SQL(
				"""
				UPDATE client SET nume={nume}, prenume={prenume}, cod_client={cod_client}, email={email}, telefon={telefon}, tip={tip}, modificat_de={modificat_de}, modificat_la={modificat_la}
				WHERE id_client={id_client}
				"""
			).on("nume" -> client.nume, "prenume" -> client.prenume, "cod_client" -> client.cod_client, "email" -> client.email, "telefon" -> client.telefon, "tip" -> client.tip, "modificat_de" -> client.modificat_de, "modificat_la" -> client.modificat_la, "id_client" -> client.id_client)
				.executeUpdate()
		}
	}
}
