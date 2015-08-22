package models.DAO

import models.Localitate

import play.api.db._
import play.api.Play.current

import scala.math.BigDecimal

import anorm._
import anorm.SqlParser._

object LocalitateDAO {
  val simple = {
    get[Long]("id_localitate")~
    get[String]("denumire") map { case id_localitate~denumire=>Localitate(id_localitate, denumire)}
  }

	def findById(id: Long) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("SELECT * FROM localitate WHERE id_localitate={id}").on("id"->id).as(simple.singleOpt)
		}
	}

	def findAll(limit: Int = 1000, offset: Int = 0) = {
	  	DB.withConnection("balotesti"){
			implicit connection=>
			SQL("SELECT * FROM localitate ORDER BY id_localitate OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY").on("offset"->offset, "limit"->limit).as(simple. *)
	  	}
	}

	def count() = {
		DB.withConnection("balotesti") { implicit connection =>
			SQL("SELECT COUNT(*) FROM localitate").as(scalar[Long].single)
		}
	}

	def save(localitate: Localitate) = {
		localitate.id_localitate match {
			case 0 => this.insert(localitate)
			case _ => this.update(localitate)
		}
	}

	def insert(localitate: Localitate) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL(
				"""
				INSERT INTO localitate (denumire)
				VALUES ({denumire})
				"""
			).on("denumire"->localitate.denumire)
				.executeInsert(scalar[scala.math.BigDecimal].single)
		}
	}

	def update(localitate: Localitate) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL(
				"""
				UPDATE localitate SET denumire={denumire}
				WHERE id_localitate={id}
				"""
			).on("denumire"->localitate.denumire, "id"->localitate.id_localitate)
				.executeUpdate()
		}
	}

	def deleteById(id: Long) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("DELETE FROM localitate WHERE id_localitate={id}").on("id"->id).executeUpdate()
		}
	}
}
