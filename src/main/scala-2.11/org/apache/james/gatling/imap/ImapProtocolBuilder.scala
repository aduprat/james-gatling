package org.apache.james.gatling.imap

import org.apache.james.gatling.imap.protocol.ImapProtocol

object ImapProtocolBuilder {
  val default = new ImapProtocolBuilder("localhost", 143)
}

case class ImapProtocolBuilder(host: String, port: Int) {

  def host(host: String): ImapProtocolBuilder = copy(host = host)
  def port(port: Int): ImapProtocolBuilder = copy(port = port)

  def build(): ImapProtocol = new ImapProtocol(host, port)

}
