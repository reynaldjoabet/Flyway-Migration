object Main extends App {
  import scala.math.Ordering.comparatorToOrdering
   import scala.math.Ordering.ordered
  println(s"Hello, World! "+{Environment.Qa})

  case class Transaction( transactionId: String, accountId:String, transactionDay: Int, category: String,transactionAmount: Double)
  val orderingTransaction= new Ordering[Transaction]{
   override def compare(x: Transaction, y: Transaction): Int = if(x.transactionDay < y.transactionDay) -1 else if(y.transactionDay<x.transactionDay) 1 else 0
  }
  val transactions:List[Transaction]=List.empty

  transactions.sortWith((x,y)=>x.transactionDay<y.transactionDay)

  transactions.groupBy( transaction =>
      transaction.transactionDay
    ).toList.sortWith((x,y)=>x._1<y._1)
}
