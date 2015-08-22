package models

case class Cadresa (
            id_cadresa: Long,
            client_id: Long,
            tip_adresa: String,
            strada_id: Long,
            numar: String,
            bl: String,
            sc: String,
            ap: String,
            et: String,
            observatii: String,
            enable: Int,
            creat_de: Option[Int],
            creat_la: Option[java.util.Date],
            modificat_de: Option[Int],
            modificat_la: Option[java.util.Date]
            )
