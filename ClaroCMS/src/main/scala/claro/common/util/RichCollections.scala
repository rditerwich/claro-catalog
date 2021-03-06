package claro.common.util

import scala.collection.{immutable,mutable}
import scala.collection.generic.CanBuildFrom
import claro.common.util.Conversions._

/**
 * Extensions to the collection classes
 */
class RichTraversable[A](it : Traversable[A]) {

  /** 
   * Convert the iterable to an immutable set
   */
  def toSet = immutable.Set(it toSeq:_*)

  /**
   * Filters element that are not of specified type.
  def classFilter[B <: A](c : java.lang.Class[B])(implicit bf: CanBuildFrom[A, B, RichTraversable[B]]) : Traversable[B] = 
    it filter (c.isInstance(_)) map (_.asInstanceOf[B])
   */
    def classFilter[B <: A](c : java.lang.Class[B]) : Traversable[B] = 
      it filter (c.isInstance(_)) map (_.asInstanceOf[B])
 
  /**
   * Convert elements to a immutable hash map. 
   */
  def makeMap[B,C](f : A => Option[(B,C)]) : immutable.Map[B, C] = 
   immutable.Map((for (a <- it.toSeq; elt = f(a); if (elt != None)) yield elt.get):_*)
 
  def makeMapWithValues[B](map : A => B) : immutable.Map[A, B] = 
    immutable.Map((for (a <- it.toSeq; b = map(a)) yield (a, b)):_*)

  def mapBy[K](map : A => K) : immutable.Map[K, A] = 
    immutable.Map((for (a <- it.toSeq; k = map(a)) yield (k -> a)):_*)

  def mapFirst[B](f : A => Option[B]) : Option[B] = { 
    for (a <- it) {
      f(a) match {
        case Some(b) => return Some(b)
        case _ =>
      }
    }
    None
  }
  
  def groupBy[K](f: A => K): immutable.Map[K, Seq[A]] =
    immutable.Map((new mutable.HashMap[K, mutable.ArrayBuffer[A]] useIn 
      (m => it foreach { a => m getOrElseUpdate (f(a), new mutable.ArrayBuffer) += a })).toSeq:_*)
}

class RichCollection[A](col : Seq[A]) extends RichTraversable[A](col) {
  
}

class RichSeq[A](seq : Seq[A]) extends RichCollection[A](seq) {
  /**
   * Filters element that are not of specified type.
   */
  override def classFilter[B <: A](c : java.lang.Class[B]) : Seq[B] = 
    seq filter (c.isInstance(_)) map (_.asInstanceOf[B])

}

class RichList[A](list : List[A]) extends RichSeq[A](list) {
}

class RichSet[A](set : Set[A])  {
  /**
   * Filters element that are not of specified type.
   */
  override def classFilter[B <: A](c : java.lang.Class[B]) : Set[B] = 
    set filter (c.isInstance(_)) map (_.asInstanceOf[B])
    
  def immutable = collection.immutable.Set(set.toSeq:_*)
}

class RichMap[A,B](map : Map[A,B]) {
  def toJava = new java.util.HashMap[A,B] useIn (result => for ((a,b) <- map) result.put(a,b))
  def immutable = collection.immutable.Map(map.toSeq:_*)
}

class RichArray[A](array : Array[A]) {
  def toSet = immutable.Set(array.toSeq:_*)
}

class RichPartialFunctionIterable[A,B](it : Iterable[PartialFunction[A,B]]) {
  def findFirst(a : A) : Option[B] = {
    for (f <- it) if (f.isDefinedAt(a)) return Some(f(a))
    None
  }
}

