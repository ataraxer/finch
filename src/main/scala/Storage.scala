package com.ataraxer.finch

import java.nio.file.{Paths, Files}


case class Storage(filePath: String) {
  private val file = Paths.get(filePath)

  def persist(message: StreamMessage) = {
    val content = (message.content + "\n").getBytes
    Files.write(file, content)
  }
}



// vim: set ts=2 sw=2 et:
