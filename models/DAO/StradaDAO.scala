package models.DAO

import models.Strada

import play.api.db._
import play.api.Play.current

import scala.math.BigDecimal

import java.util.Date

import anorm._
import anorm.SqlParser._

object StradaDAO {
	val simple = {
		get[Long]("id_strada")~
		get[String]("denumire")~
		get[Int]("nr_clienti")~
		get[String]("material")~
		get[BigDecimal]("dn")~
		get[BigDecimal]("lungime")~
		get[String]("regim_presiune")~
		get[Option[Int]]("creat_de")~
		get[Option[java.util.Date]]("creat_la")~
		get[Option[Int]]("modificat_de")~
		get[Option[java.util.Date]]("modificat_la") map {
			case id_strada~denumire~nr_clienti~material~dn~lungime~regim_presiune~creat_de~creat_la~modificat_de~modificat_la=>Strada(id_strada, denumire, nr_clienti, material, dn, lungime, regim_presiune, creat_de, creat_la, modificat_de, modificat_la)
		}
	}

	def findById(id: Long) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("SELECT * FROM strada WHERE id_strada={id}").on("id"->id).as(simple.singleOpt)
		}
	}

	def findByDenumire(denumire: String) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("SELECT * FROM strada WHERE denumire LIKE {denumire}").on("denumire"->("%"+denumire+"%")).as(simple *)
		}
	}

	def findAll(limit: Int = 1000, offset: Int = 0): Seq[Strada]={
	  	DB.withConnection("balotesti"){
			implicit connection=>
			SQL("SELECT * FROM strada ORDER BY id_strada OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY").on("offset"->offset, "limit"->limit).as(simple. *)
	  	}
	}

	def count() = {
		DB.withConnection("balotesti") { implicit connection =>
			SQL("SELECT COUNT(*) FROM strada").as(scalar[Long].single)
		}
	}

	def save(strada: Strada) = {
		strada.id_strada match {
			case 0 => this.insert(strada)
			case _ => this.update(strada)
		}
	}

	def insert(strada: Strada) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL(
				"""
				INSERT INTO strada (denumire, nr_clienti, material, dn, lungime, regim_presiune, creat_de, creat_la)
				VALUES ({denumire}, {nr_clienti}, {material}, {dn}, {lungime}, {regim_presiune}, {creat_de}, {creat_la})
				"""
			).on("denumire"->strada.denumire, "nr_clienti"->strada.nr_clienti, "material"->strada.material, "dn"->strada.dn, "lungime"->strada.lungime, "regim_presiune"->strada.regim_presiune, "creat_de"->strada.creat_de, "creat_la"->strada.creat_la)
				.executeInsert(scalar[scala.math.BigDecimal].single)
		}
	}

	def update(strada: Strada) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL(
				"""
				UPDATE strada SET denumire={denumire}, nr_clienti={nr_clienti}, material={material}, dn={dn}, lungime={lungime}, regim_presiune={regim_presiune}, modificat_de={modificat_de}, modificat_la={modificat_la}
				WHERE id_strada={id}
				"""
			).on("denumire"->strada.denumire, "nr_clienti"->strada.nr_clienti, "material"->strada.material, "dn"->strada.dn, "lungime"->strada.lungime, "regim_presiune"->strada.regim_presiune,"modificat_de"->strada.modificat_de, "modificat_la"->strada.modificat_la, "id"->strada.id_strada)
				.executeUpdate()
		}
	}

	def deleteById(id: Long) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("DELETE FROM strada WHERE id_strada={id}").on("id"->id).executeUpdate()
		}
	}
}
