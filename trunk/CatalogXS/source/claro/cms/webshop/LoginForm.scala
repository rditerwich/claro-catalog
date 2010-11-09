package claro.cms.webshop

import net.liftweb.http.{S,SHtml,RequestVar}
import net.liftweb.http.js.JsCmds
import net.liftweb.util.Mailer.{To,Subject,PlainMailBodyType}
import xml.{Node, NodeSeq, MetaData}
import agilexs.catalogxs.jpa

object LoginForm extends RequestVar[LoginForm](new LoginForm) 

class LoginForm extends Form {
  val dummyEmail = "Email"
  val dummyPassword = ""
  var email : String = dummyEmail
  var password : String = dummyPassword
  var failure : Option[LoginFailure] = None 
  
  val pathPrefix = WebshopModel.currentProductGroup match {
    case Some(group) => "/group/" + group.id
    case None => ""
  }
  
  def emailField = SHtml.text(email, email = _, 
      ("onclick", "javascript:if (value == '" + dummyEmail + "') value = '';"), 
      ("onblur", "javascript:if (value == '') value = '" + dummyEmail + "';")) % currentAttributes()
    
  def passwordField = SHtml.password(password, password = _, 
        ("onclick", "javascript:if (value == '" + dummyPassword + "') value = ''"), 
        ("onblur", "javascript:if (value == '') value = '" + dummyPassword + "';")) % currentAttributes()
  
  def forgotPasswordLink(href : String, changePasswordHref : String) = (xml : NodeSeq) => 
    SHtml.a(() => forgotPassword(href, changePasswordHref), xml) % currentAttributes()
  
  private def forgotPassword(href : String, changePasswordHref : String) = {
    Mail.mail(Subject("Set Password " + Request.website.name),
      To(email),
      PlainMailBodyType("Please use the following link to set your password and activate your account:\n\n" +
      ChangePasswordForm.createLink(changePasswordHref, email)))
    JsCmds.RedirectTo(href)
  }

  def loginButton = SHtml.submit("Login", () => login) % currentAttributes()
  
  private def login = {
    
    // copy email to current form
    val current = LoginForm.get
    current.email = email
    
    WebshopDao.findUserByEmailAndPassword(email, ChangePasswordForm.encrypt(password)) match {
      case Some(user) => WebshopModel.currentUserVar.set(Some(user))
      case None => current.failure = Some(LoginFailure("login failed"))
    }
  }
}

case class LoginFailure(message : String) 