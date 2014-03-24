import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import fr.janalyse.unittools._

package object tuninglab {

  def gc() = System.gc()
  def alloc(szMb: Int = 25) = Array.fill[Byte](1024 * 1024 * szMb)(0x1)
  def primesGenerator() = new fr.janalyse.primes.PrimesGenerator[Long]()

  def now = System.currentTimeMillis
  def howlong[T](proc: => T) = { val s = now; val r=proc; ((now - s).toDurationDesc, r) }
  def futurehowlong[T](proc: => Future[T]) = { howlong(Await.result(proc, 60.seconds)) }

}
