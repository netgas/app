package models

case class Contract(
					id_contract: Long,
					id_client: Long,
					numar: String,
					data: java.util.Date,
					enable: Int,
					creat_de: Option[Int],
					creat_la: Option[java.util.Date],
					modificat_de: Option[Int],
					modificat_la: Option[java.util.Date]
				)