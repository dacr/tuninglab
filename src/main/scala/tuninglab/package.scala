package object tuninglab {

  def gc()=System.gc()
  def alloc(szMb:Int=25) = Array.fill[Byte](1024*1024*szMb)(0x1)
}