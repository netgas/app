package models.DAO

import models.Temperatura

import play.api.db._
import play.api.Play.current

import scala.math.BigDecimal

import java.util.Date

import anorm._
import anorm.SqlParser._

object TemperaturaDAO {
	val simple = {
		get[Long]("id_temperatura")~
		get[Date]("data")~
		get[BigDecimal]("valoare") map {
			case id_temperatura~data~valoare=>Temperatura(id_temperatura, data, valoare)
		}
	}

	def findById(id: Long) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("SELECT * FROM temperatura WHERE id_temperatura={id}").on("id"->id).as(simple.singleOpt)
		}
	}

	def findAll(limit: Int = 1000, offset: Int = 0): Seq[Temperatura]={
	  	DB.withConnection("balotesti"){
			implicit connection=>
			SQL("SELECT * FROM temperatura ORDER BY id_temperatura OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY").on("offset"->offset, "limit"->limit).as(simple. *)
	  	}
	}

	def count() = {
		DB.withConnection("balotesti") { implicit connection =>
			SQL("SELECT COUNT(*) FROM temperatura").as(scalar[Long].single)
		}
	}

	def save(temperatura: Temperatura) = {
		temperatura.id_temperatura match {
			case 0 => this.insert(temperatura)
			case _ => this.update(temperatura)
		}
	}

	def insert(temperatura: Temperatura) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL(
				"""
				INSERT INTO temperatura (data, valoare)
				VALUES ({data}, {valoare})
				"""
			).on("data" -> temperatura.data, "valoare" -> temperatura.valoare)
				.executeInsert(scalar[scala.math.BigDecimal].single)
		}
	}

	def update(temperatura: Temperatura) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL(
				"""
				UPDATE temperatura SET data={data}, valoare={valoare}
				WHERE id_temperatura={id}
				"""
			).on("data"->temperatura.data, "valoare"->temperatura.valoare, "id"->temperatura.id_temperatura)
				.executeUpdate()
		}
	}

	def deleteById(id: Long) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("DELETE FROM temperatura WHERE id_temperatura={id}").on("id"->id).executeUpdate()
		}
	}
}