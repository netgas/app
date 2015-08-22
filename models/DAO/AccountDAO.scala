package models.DAO

import models.Account

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import org.mindrot.jbcrypt.BCrypt

object AccountDAO {
	val simple = {
		get[Long]("id")~
		get[String]("name")~
		get[String]("password")~
		get[String]("role") map{case id~name~password~role=>Account(id, name, password, role)}
	}

	def findById(id: Long) = {
		DB.withConnection("balotesti") { implicit connection =>
			SQL("SELECT * FROM account where id={id} ").on("id"->id).as(simple.singleOpt)
		}
	}

	def findByName(name: String) = {
		DB.withConnection("balotesti") { implicit connection =>
			SQL("SELECT * FROM account WHERE name={name}").on("name"->name).as(simple.singleOpt)
		}
	}

	def findAll(limit: Int = 1000, offset: Int = 0) = {
		DB.withConnection("balotesti") {implicit connection =>
			SQL("SELECT * FROM account ORDER BY id OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY").on("offset"->offset, "limit"->limit).as(simple. *)
		}
	}

	def count() = {
		DB.withConnection("balotesti") { implicit connection =>
			SQL("SELECT COUNT(*) FROM account").as(scalar[Long].single)
		}
	}

	def authentificate(name: String, password: String) = {
		val account = findByName(name)
		account.filter(user => BCrypt.checkpw(password, user.password))
	}

	def insert(account: Account) = {
		val password = BCrypt.hashpw(account.password, BCrypt.gensalt())
		DB.withConnection("balotesti") { implicit connection =>
			SQL("INSERT INTO account (name, password, role) VALUES({name}, {password}, {role})")
				.on("name"->account.name, "password"->password, "role"->account.role)
					.executeInsert(scalar[scala.math.BigDecimal].single)
		}
	}

	def update(account: Account, name: String, newPassword: String, role: String) = {
		if(newPassword == account.password) {
			//daca noua parola si parola din baze de date sunt egale atunci nu a fost introdusa o parola noua
			DB.withConnection("balotesti") { implicit connection =>
				SQL("UPDATE account SET name={name}, role={role} WHERE id={id}")
					.on("name"->name, "role"->role, "id"->account.id)
						.executeUpdate()
			}
		}else {
			//daca parolele sunt diferite, a fost introdusa o parola noua, care trebuie criptata mai intai
			val password = BCrypt.hashpw(newPassword, BCrypt.gensalt())
			DB.withConnection("balotesti") { implicit connection =>
				SQL("UPDATE account SET name={name}, password={password}, role={role} WHERE id={id}")
					.on("name"->name, "password"->password, "role"->role, "id"->account.id)
						.executeUpdate()
			}
		}
	}

	def deleteById(id: Int) = {
		DB.withConnection("balotesti") { implicit connection =>
			SQL("DELETE FROM account WHERE id={id}").on("id"->id).executeUpdate()
		}
	}

	//returneaza localitatile asociate cu anumit cont
	def getLocalitati(id: Long) = {
		DB.withConnection("balotesti") { implicit connection =>
			SQL("SELECT localitate.* FROM localitate JOIN account_localitate ON account_localitate.localitate_id=localitate.id_localitate WHERE account_localitate.account_id={id}")
				.on("id"->id)
					.as(models.DAO.LocalitateDAO.simple *)
		}
	}
}
