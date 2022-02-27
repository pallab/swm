package com.swym.test.memorydb

import slick.lifted.{Rep, Tag}
import slick.jdbc.H2Profile.api._
import com.google.common.cache._
import com.swym.test.memorydb.TweetsTable.table
import slick.sql.SqlStreamingAction

import scala.concurrent.ExecutionContext.Implicits.global

class TweetCountByUserTable(tag: Tag) extends Table[(String, Long, Long)](tag, TableNames.TWEETS_BY_USER) {

  def user_name: Rep[String] = column[String]("user_name", O.PrimaryKey)

  def count: Rep[Long] = column[Long]("count")

  def last_triggered_at: Rep[Long] = column[Long]("last_triggered_at")

  def * = (user_name, count, last_triggered_at)
}

/**
 * Using this table as a LRU cache
 * In practice a proper persisted cache such as redis
 * or a distributed counter should be used
 */
object TweetCountByUserTable {
  val table = TableQuery[TweetCountByUserTable]

  private val countsByUser: LoadingCache[String, (Long, Long)] = CacheBuilder
    .newBuilder()
    .maximumSize(1000)
    .build(new CacheLoader[String, (Long, Long)]() {
      def load(key: String): (Long, Long) = {
        get(key).getOrElse((0, 0))
      }
    }
    )


  def get(user: String) = {
    MemoryDB.conn.run(
      table.filter(_.user_name === user).map(r => (r.count, r.last_triggered_at))
        .result.headOption).block()
  }

  def incrementAndGet(user: String) = {
    val currentValue = countsByUser.get(user)
    val updatedValue = (currentValue._1 + 1, currentValue._2)
    if (currentValue._1 == 0) {
      MemoryDB.conn.run(table += (user, updatedValue._1, updatedValue._2)).block()
    } else {
      MemoryDB.conn.run(table.filter(_.user_name === user).map(_.count).update(updatedValue._1)).block()
    }
    countsByUser.put(user, updatedValue)
    updatedValue
  }

  def matchesCondition(user: String, condition : String) = {
    val query: SqlStreamingAction[Vector[Int], Int, Effect] =
      sql"""
        select count(*) from #${TableNames.TWEETS_BY_USER} where user_name = '#$user' and ( #$condition )
         """.as[Int]
    MemoryDB.conn.run(query).block().sum > 0
  }

  def updateLastTriggered(user: String, triggeredAt: Long) = {
    MemoryDB.conn.run(
      table.filter(_.user_name === user)
        .map(_.last_triggered_at)
        .update(triggeredAt)).block()
  }

  def createTable = {
//    println("Creating table ", table.schema.createIfNotExistsStatements.mkString("\n"))
    MemoryDB.conn.run(table.schema.dropIfExists).block()
    MemoryDB.conn.run(table.schema.createIfNotExists).block()
  }

}

