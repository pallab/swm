package com.swym.test.memorydb

import com.swym.test.mock.MockTwitter
import com.swym.test.types.Tweet
import slick.lifted.{Rep, Tag}
import slick.jdbc.H2Profile.api._
import slick.sql.SqlStreamingAction

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class TweetsTable(tag: Tag) extends Table[Tweet](tag, TableNames.TWEETS) {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey)

  def user: Rep[String] = column[String]("user")

  def text: Rep[String] = column[String]("text")

  def location: Rep[String] = column[String]("location")

  def * = (id, user, text, location).mapTo[Tweet]
}


object TweetsTable {
  val table = TableQuery[TweetsTable]


  def insert( t : Tweet) = {
    MemoryDB.conn.run( table += t).block()
  }

  def delete(id: Long): Int = {
    MemoryDB.conn.run(table.filter(_.id === id).delete).block()
  }

  def get(id: Long): Option[Tweet] = {
    MemoryDB.conn.run(
      table.filter(_.id === id).result.headOption)
      .block()
  }

  def matchesCondition(tweetId: Long, condition : String) = {
    val query: SqlStreamingAction[Vector[Int], Int, Effect] =
      sql"""
        select count(*) from #${TableNames.TWEETS} where id = #$tweetId and ( #$condition );
         """.as[Int]

    MemoryDB.conn.run(query).block().sum > 0
  }

  def createTable = {
//    println("Creating table " table.schema.createIfNotExistsStatements.mkString("\n"))
    MemoryDB.conn.run(table.schema.dropIfExists).block()
    MemoryDB.conn.run(table.schema.createIfNotExists).block()
  }

}

