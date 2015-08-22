package models

import java.util.Date

import scala.math.BigDecimal

case class Consum (
                  id_consum: Long,
                  client_id: Long,
                  contract_id: Long,
                  luna: Int,
                  an: Int,
                  valoare: BigDecimal,
                  creat_de: Option[Int],
                  creat_la: Option[Date],
                  modificat_de: Option[Int],
                  modificat_la: Option[Date]
                  )
