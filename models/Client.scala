package models

/**
 * Created by cwn on 31.07.2015.
 */
case class  Client(
					id_client: Long,
					nume: String,
					prenume: String,
					cod_client: String,
					email: String,
					telefon: String,
					tip: String,
					enable: Int,
					creat_de: Option[Int],
					creat_la: Option[java.util.Date],
					modificat_de: Option[Int],
					modificat_la: Option[java.util.Date]
				)


