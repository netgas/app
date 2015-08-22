package models.DAO

import models.Consum
import models.DAO.ContractDAO

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import java.util.Date

import scala.math.BigDecimal

object ConsumDAO {
  val simple = {
    get[Long]("id_consum")~
    get[Long]("client_id")~
    get[Long]("contract_id")~
    get[Int]("luna")~
    get[Int]("an")~
    get[BigDecimal]("valoare")~
    get[Option[Int]]("creat_de")~
    get[Option[Date]]("creat_la")~
    get[Option[Int]]("modificat_de")~
    get[Option[Date]]("modificat_la") map {
      case id_consum~client_id~contract_id~luna~an~valoare~creat_de~creat_la~modificat_de~modificat_la=>Consum(id_consum, client_id, contract_id, luna, an, valoare, creat_de, creat_la, modificat_de, modificat_la)
    }
  }

}
