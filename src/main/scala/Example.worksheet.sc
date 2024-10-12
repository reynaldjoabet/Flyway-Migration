
import scala.io.Source
List("Steve", "Bobby", "Tom", "John", "Bob").sortWith((x, y) => x.take(3).compareTo(y.take(3)) < 0)


// List("Bobby", "Bob", "John", "Steve", "Tom")
case class Transaction( transactionId: String, accountId:String, transactionDay: Int, category: String,transactionAmount: Double)

//The lines of the CSV file (dropping the first to remove the header)
  val transactionslines = Source.fromFile("./src/main/resources/transactions.txt").getLines().drop(1)

  //Classpath: Resources under src/main/resources are automatically added to the classpath by SBT during both compilation and packaging.
  //The path to the resource you want to load. The path should start with a / if it’s an absolute path, relative to the classpath root.
  val rawtransactions=Source.fromInputStream(getClass.getResourceAsStream("/transactions.txt")).getLines()

val transactions: List[Transaction]=rawtransactions.drop(1).map { line =>
    val split = line.split(',')
    Transaction(split(0), split(1), split(2).toInt, split(3), split(4).toDouble)
  }.toList
  
  transactions.groupBy(_.transactionDay).toList.sortWith((x,y)=>x.head<y.head)

  transactions.groupBy(_.transactionDay).toList.sortWith((x,y)=>x._1<y._1)