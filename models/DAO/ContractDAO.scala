package models.DAO

import models.Contract

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

object ContractDAO {
	val simple = {
		get[Long]("id_contract")~
		get[Long]("id_client")~
		get[String]("numar")~
		get[java.util.Date]("data")~
		get[Int]("enable")~
		get[Option[Int]]("creat_de")~
		get[Option[java.util.Date]]("creat_la")~
		get[Option[Int]]("modificat_de")~
		get[Option[java.util.Date]]("modificat_la") map {
			case id_contract~id_client~numar~data~enable~creat_de~creat_la~modificat_de~modificat_la=>Contract(id_contract, id_client, numar, data, enable, creat_de, creat_la, modificat_de, modificat_la)
		}
	}

	def findById(id: Long) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("SELECT * FROM contract WHERE id_contract={id}").on("id"->id).as(simple.singleOpt)
		}
	}

	def findByClientId(id_client: Long) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("SELECT * FROM contract WHERE id_client={id_client} AND enable=1")
				.on("id_client" -> id_client)
					.as(simple *)
		}
	}

	//calculeaza ultimul numar de contract
	def getLastNumar() = {
		DB.withConnection("balotesti") { implicit c =>
			val last = SQL("SELECT numar FROM contract ORDER BY CAST(numar AS Numeric(10,0)) DESC OFFSET 0 ROW FETCH NEXT 1 ROW ONLY").as(scalar[String].singleOpt)
			println(last)
			last match {
				case Some(result) => (result.toInt + 1).toString
				case _ => "1"
			}
		}
	}

	def save(contract: Contract) = {
		contract.id_contract match {
			case 0 => this.insert(contract)
			case _ => this.update(contract)
		}
	}

	def insert(contract: Contract) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL(
				"""
				INSERT INTO contract (id_client, numar, data, enable, creat_de, creat_la)
				VALUES ({id_client}, {numar}, {data}, {enable}, {creat_de}, {creat_la})
				"""
			).on("id_client" -> contract.id_client, "numar" -> contract.numar, "data" -> contract.data, "enable" -> contract.enable, "creat_de" -> contract.creat_de, "creat_la" -> contract.creat_la)
				.executeInsert(scalar[scala.math.BigDecimal].single)
		}
	}

	def update(contract: Contract) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL(
				"""
				UPDATE contract SET id_client={id_client}, numar={numar}, data={data}, modificat_de={mod_de}, modificat_la={mod_la}
				WHERE id_contract={id}
				"""
			).on("id_client"->contract.id_client, "numar"->contract.numar, "data"->contract.data, "mod_de"->contract.modificat_de, "mod_la"->contract.modificat_la, "id"->contract.id_contract)
				.executeUpdate()
		}
	}

	def disableById(id: Long) = {
		DB.withConnection("balotesti") { implicit c =>
			SQL("UPDATE contract SET enable=0 WHERE id_contract={id}").on("id"->id).executeUpdate()
		}
	}
	
}
