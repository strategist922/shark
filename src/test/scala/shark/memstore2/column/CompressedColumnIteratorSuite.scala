package shark.memstore2.column

import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.scalatest.FunSuite

import org.apache.hadoop.io.Text

import shark.memstore2.column.Implicits._

class CompressedColumnIteratorSuite extends FunSuite {
  
  test("RLE Decompression") {

    class TestIterator(val buffer: ByteBuffer, val columnType: ColumnType[_,_])
      extends CompressedColumnIterator
    
    val b = ByteBuffer.allocate(1024)
    b.order(ByteOrder.nativeOrder())
    b.putInt(STRING.typeID)
    val rle = new RLE()
    
    Array(new Text("abc"), new Text("abc"), new Text("efg"), new Text("abc")).foreach { text =>
      STRING.append(text, b)
      rle.gatherStatsForCompressibility(text, STRING)
    }
    b.limit(b.position())
    b.rewind()
    val compressedBuffer = rle.compress(b, STRING)
    val iter = new TestIterator(compressedBuffer, compressedBuffer.getInt())
    iter.next()
    println(iter.current)
  }
}
