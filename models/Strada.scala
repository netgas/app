package models

case class Strada(
				id_strada: Long,
				denumire: String,
				nr_clienti: Int,
				material: String,
				dn: scala.math.BigDecimal,
				lungime: scala.math.BigDecimal,
				regim_presiune: String,
				creat_de: Option[Int],
				creat_la: Option[java.util.Date],
				modificat_de: Option[Int],
				modificat_la: Option[java.util.Date]
				)
