package com.swym.test.memorydb

import slick.jdbc.H2Profile.api._

object MemoryDB {
  val conn = Database.forConfig("h2mem")
}

