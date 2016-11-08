package org.apache.james.gatling.imap.protocol.action

import akka.actor.{Actor, ActorRef, Props}
import com.sun.mail.imap.protocol.IMAPResponse
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.TimeHelper._
import io.gatling.commons.validation.Validation
import io.gatling.core.action.{Action, ValidatedActionActor}
import io.gatling.core.session._
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import org.apache.james.gatling.imap.protocol.ImapCommand.Select
import org.apache.james.gatling.imap.protocol.ImapProtocol
import org.apache.james.gatling.imap.protocol.command.SelectHandler

object SelectAction {
  def props(protocol: ImapProtocol, sessions: ActorRef, requestname: String, statsEngine: StatsEngine, next: Action, mailbox:Expression[String]) =
    Props(new SelectAction(protocol, sessions, requestname,statsEngine, next, mailbox))
}

class SelectAction(protocol: ImapProtocol, sessions: ActorRef, requestName: String, val statsEngine: StatsEngine, val next: Action, mailbox:Expression[String]) extends ValidatedActionActor {

  def handleSelected(session: Session, start: Long) =
    context.actorOf(Props( new Actor {
    override def receive: Receive = {
      case SelectHandler.Selected(response: Seq[IMAPResponse]) =>
        logger.trace("SelectAction#Selected on SelectHandler.Selected")
        response.find(_.isOK).fold(noOkResponse(session, start))(onOkResponse(session, start))
      case e: Exception =>
        noOkResponse(session, start, Some(e.getMessage))
      case msg =>
        logger.error(s"received unexpected message $msg")

    }

    def noOkResponse(session: Session, start: Long, message: Option[String] = None) = {
      statsEngine.logResponse(session, requestName, ResponseTimings(start, nowMillis), KO, None, message)
      next ! session.markAsFailed
      context.stop(self)
    }

    def onOkResponse(session: Session, start: Long)(response: IMAPResponse) = {
      statsEngine.logResponse(session, requestName, ResponseTimings(start, nowMillis), OK, None, None)
      next ! session
      context.stop(self)
    }
  }), s"user-${session.userId}")

  override protected def executeOrFail(session: Session): Validation[_] = {
      for{
        mailbox<-mailbox(session)
      } yield {
        val id: Long = session.userId
        val handler =handleSelected(session, nowMillis)
        sessions.tell(Select(id.toString, mailbox),handler)
      }
  }
}