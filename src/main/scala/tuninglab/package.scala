import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import fr.janalyse.unittools._
import java.io._
import java.net.{Socket}
import resource._
import resource.ManagedResource

package object tuninglab {

  def sleep(howlong:fr.janalyse.unittools.DurationHelper) {Thread.sleep(howlong)}
  def gc() = System.gc()
  def alloc(szMb: Int = 25) = Array.fill[Byte](1024 * 1024 * szMb)(0x1)
  def alloc(howmuch:fr.janalyse.unittools.SizeHelper) = Array.fill[Byte](howmuch.toSize.toInt)(0x1)
  
  def primesGenerator() = new fr.janalyse.primes.PrimesGenerator[Long]()

  def now = System.currentTimeMillis
  def howlong[T](proc: => T) = { val s = now; val r=proc; ((now - s).toDurationDesc, r) }
  def futurehowlong[T](proc: => Future[T]) = { howlong(Await.result(proc, 60.seconds)) }

  def readall(reader:BufferedReader) = {
    Stream.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
  }
  def printall(reader:BufferedReader) = {
    Stream.continually(reader.readLine()).takeWhile(_ != null).foreach(println)
    "END"
  }
  
  // httpclient("localhost", 8080) (_.println("GET /check/123 HTTP/1.1\r\nHost:me.com\r\nConnection:Close\r\n\r\n"))
  def httpclient(host:String="127.0.0.1",port:Int=80, inBuffSz:Int=8192)
                (outproc:(PrintWriter)=>Any, inproc:(BufferedReader)=>String = readall) = {
    var res=Option.empty[String]
    def mkSocket = {
      val sock = new Socket(host, port)
      //sock.setReceiveBufferSize(inBuffSz)
      sock
    }
    for {
      cnx <- managed(mkSocket)
      outStream <- managed(cnx.getOutputStream)
      out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outStream)))
      inStream <- managed(new InputStreamReader(cnx.getInputStream))
      in = new BufferedReader(inStream,inBuffSz)
    } {
      
      outproc(out)
      out.flush()
      res = Some(inproc(in))
    }
    res
  }
  
  
  def slowPost(host:String="127.0.0.1", port:Int=80) {
    httpclient(host,port) { pout =>
        pout.println(s"POST /primesui/config HTTP/1.1")
        pout.println(s"Host: $host:$port")
        pout.println(s"Accept: */*")
        pout.println(s"Origin: http://$host:$port")
        pout.println(s"Content-Type: application/x-www-form-urlencoded")
        pout.println(s"Cache-Control: max-age=0")
        pout.flush()
        Thread.sleep(10*1000L) // => R
        pout.println(s"Referer: http://$host:$port/primesui/config")
        pout.println(s"Connection: close")
        pout.println(s"Content-Length: 17")
        pout.println("")
        pout.flush()
        Thread.sleep(10*1000L) // => W
        pout.println("""usecache:selected""")
        pout.flush()           // => W
        Thread.sleep(10*1000L)
    }
  }
  
  def slowClient(host:String="127.0.0.1", port:Int=80, sleep:Int=10) {
    httpclient(host,port, inBuffSz=10) (
       {pout =>
          pout.println(s"GET /primesui/big HTTP/1.1")
          pout.println(s"Host: $host:$port")
          pout.println(s"Accept: */*")
          pout.println(s"Cache-Control: max-age=0")
          pout.println(s"Connection: close")
          pout.println()},
       {reader => 
          Thread.sleep(sleep*1000) ; readall(reader)} )
  }
  
}
