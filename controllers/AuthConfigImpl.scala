package controllers

import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

trait AuthConfigImpl extends BaseAuthConfig {

  // def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.User.users))

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = {
  		val uri = request.session.get("access_uri").getOrElse(routes.User.users.url.toString)
    	Future.successful(Redirect(uri).withSession(request.session - "access_uri"))
  }

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.User.login))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful {
    request.headers.get("X-Requested-With") match {
      case Some("XMLHttpRequest") => Unauthorized("Autentificare a esuat!")
      case _ => Redirect(routes.User.login).flashing("success"->"Nu sunteti autentificat!").withSession("access_uri" -> request.uri)
    }
  }

}