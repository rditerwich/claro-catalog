package claro.cms.webshop

import java.security.MessageDigest
import java.nio.charset.Charset
import net.liftweb.http.{S,SHtml,RequestVar}
import net.liftweb.util.Mailer.{To,Subject,PlainMailBodyType}
import claro.common.util.Conversions._
import claro.jpa
import claro.cms.{Form,Mail,Website}

object RegistrationForm extends RequestVar[RegistrationForm](new RegistrationForm(
    new jpa.party.User useIn 
      (_.setParty(new jpa.party.Party useIn 
        (_.setAddress(new jpa.party.Address))))))

class RegistrationForm(val user : jpa.party.User) extends Form {

  val emailField = TextField(user.getEmail, _ match {
    case Whitespace(s) => error = "No email address specified"; user.setEmail("")
    case EmailAddress(s) => if (emailExists(s)) error = "Email address is already in use"; user.setEmail(s)
    case s => error = "Invalid email address"; user.setEmail(s)
  })

  val partyForm = Nested(PartyForm(user.getParty))
    
  def emailExists(email : String) = WebshopDao.findUserByEmail(email) != None 

  def registerButton(label : String, href : String, changePasswordHref : String) = SHtml.submit(label, () => register(href, changePasswordHref)) % currentAttributes()
  
  def register(href : String, changePasswordHref : String) = {

    // store user when there are no validation errors
    if (errors.isEmpty) {
      
    	user.setIsCatalogUser(false)
      user.setPassword("")

      // send confirmation email
      val msg = "Dear " + user.getParty.getName + ",\n\n" +
        "Thank you for registering with " + Website.instance.name + ".\n\n" + 
        "Please select the following link to set your password and activate your account:\n\n" +
        ChangePasswordForm.createLink(changePasswordHref, user.getEmail)

      Mail.mail(Subject("Confirm " + Website.instance.name + " registration"),
        To(user.getEmail),
        PlainMailBodyType(msg))

      // store user
      WebshopDao transaction { em =>
      	for (party <- Option(user.getParty)) {
      		for (address <- Option(party.getAddress)) em.persist(address)
      		for (address <- Option(party.getDeliveryAddress)) em.persist(address)
      		for (address <- Option(party.getShippingAddress)) em.persist(address)
      		em.persist(party)
      	}
      	em.persist(user)  
      }
      if (href.trim != "") {
        S.redirectTo(href)
      }
    }
    else {
      // keep error messages
      RegistrationForm.set(this)
    }
  }
}
