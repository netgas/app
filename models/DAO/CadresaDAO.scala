package models.DAO

import models.Cadresa
import models.DAO.StradaDAO

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import java.util.Date

import scala.math.BigDecimal

object CadresaDAO {
  val simple = {
    get[Long]("id_cadresa")~
    get[Long]("client_id")~
    get[String]("tip_adresa")~
    get[Long]("strada_id")~
    get[String]("numar")~
    get[String]("bl")~
    get[String]("sc")~
    get[String]("ap")~
    get[String]("et")~
    get[String]("observatii")~
    get[Int]("enable")~
    get[Option[Int]]("creat_de")~
    get[Option[java.util.Date]]("creat_la")~
    get[Option[Int]]("modificat_de")~
    get[Option[java.util.Date]]("modificat_la") map {
      case id_cadresa~client_id~tip_adresa~strada_id~numar~bl~sc~ap~et~observatii~enable~creat_de~creat_la~modificat_de~modificat_la=>Cadresa(id_cadresa, client_id, tip_adresa, strada_id, numar, bl, sc, ap, et, observatii, enable, creat_de, creat_la, modificat_de, modificat_la)
    }
  }

  val withStrada = CadresaDAO.simple ~ (StradaDAO.simple ?) map {
    case cadresa~strada => (cadresa, strada)
  }

  def findAll(limit: Int = 1000, offset: Int = 0) = {
    DB.withConnection("balotesti") { implicit c =>
      SQL("SELECT * FROM cadresa LEFT OUTER JOIN strada ON strada.id=cadresa.strada.id ORDER BY id_cadresa OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY")
      .on("offset"->offset, "limit"->limit)
      .as(withStrada. *)
    }
  }

  def count() = {
    DB.withConnection("balotesti") { implicit connection =>
      SQL("SELECT COUNT(*) FROM cadresa").as(scalar[Long].single)
    }
  }

  def findById(id: Int) ={
    DB.withConnection("balotesti"){	implicit connection=>
      SQL("SELECT * FROM cadresa where id_cadresa={id} ").on("id"->id).as(simple.singleOpt)
    }
  }

  def save(cadresa: Cadresa) = {
    cadresa.id_cadresa match {
      case 0 => this.insert(cadresa)
      case _ => this.update(cadresa)
    }
  }

  def insert(cadresa: Cadresa) = {
    DB.withConnection("balotesti") { implicit c =>
      SQL(
        """
        INSERT INTO cadresa (id_cadresa, client_id, tip_adresa, strada_id, numar, bl, sc, ap, et, observatii, enable, creat_de, creat_la)
        VALUES ({id_cadresa}, {client_id}, {tip_adresa}, {strada_id}, {numar}, {bl}, {sc}, {ap}, {et}, {observatii}, {enable}, {creat_de}, {creat_la})
        """
      ).on("id_cadresa"->cadresa.id_cadresa, "client_id"->cadresa.client_id, "tip_strada"->cadresa.strada_id, "numar"->cadresa.numar, "bl"->cadresa.bl, "sc"->cadresa.sc, "ap"->cadresa.ap, "et"->cadresa.et, "observatii"->cadresa.observatii, "enable"->cadresa.enable, "creat_de"->cadresa.creat_de, "creat_la"->cadresa.creat_la)
      .executeInsert(scalar[BigDecimal].single)
    }
  }

  def update(cadresa: Cadresa) = {
    DB.withConnection("balotesti") { implicit c =>
      SQL(
        """
        UPDATE cadresa SET client_id={client_id}, tip_adresa={tip_adresa}, strada_id={strada_id}, numar={numar}, bl={bl}, sc={sc}, ap={ap}, et={et}, observatii={observatii}, modificat_de={mod_de}, modificat_la={mod_la}
        WHERE id_cadresa={id}
        """
      ).on("client_id"->cadresa.client_id, "tip_strada"->cadresa.strada_id, "numar"->cadresa.numar, "bl"->cadresa.bl, "sc"->cadresa.sc, "ap"->cadresa.ap, "et"->cadresa.et, "observatii"->cadresa.observatii, "modificat_de"->cadresa.modificat_de, "modificat_la"->cadresa.modificat_la)
      .executeUpdate()
    }
  }

  def disableById(id: Long) = {
    DB.withConnection("balotesti") { implicit c =>
      SQL("UPDATE cadresa SET enable=0 WHERE id_cadresa={id}").on("id"->id).executeUpdate()
    }
  }

}
