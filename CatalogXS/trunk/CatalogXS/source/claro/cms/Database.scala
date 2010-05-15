package claro.cms

import net.liftweb.http.RequestVar
import javax.persistence.EntityManager

class Dao(dataSource : String) {

  def createEntityManager = Request.website.entityManagerFactory(dataSource).createEntityManager
  private object currentEntityManager extends RequestVar[Option[EntityManager]](None)
    
  def access[A](f : EntityManager => A) : A = {
    currentEntityManager.get match {
      case Some(em) => f(em)
      case None =>
        val em = createEntityManager
        try {
          currentEntityManager.set(Some(em))
          val tx = em.getTransaction
          try {
            tx.begin
            val result = f(em)
            tx.commit
            result
          } catch {
            case e => 
              if (tx.isActive) tx.rollback
              throw e
          }
        }
        finally {
          currentEntityManager.set(None)
          em.close
        }
    }
  }
  
  def querySingle[A](query : String, parameters : (String,Any)*) : Option[A] = access { em => 
    val q = em.createQuery(query)
    for ((parameter, value) <- parameters) q.setParameter(parameter, value)
    if (q.getResultList.isEmpty) {
      None
    } else {
      Some(q.getResultList.get(0).asInstanceOf[A])
    }
  }
}
