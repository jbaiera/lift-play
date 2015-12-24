package code.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager
import net.liftweb.util.Helpers

import scala.xml.NodeSeq

/**
 * Singleton that provides the chat features to all clients.
 * Essentially, the server accepts some incoming information (Lift Actor) and
 * dispatches the change to all people listening to it (Listener Manager dispatching
 * to the CometActor).
 */
object ChatServer extends LiftActor with ListenerManager {

  case class ChatMessage(mesg: String, sender: Sender)

  /**
   * This is the chat history as the chat server knows it. We start with a welcome message
   * and as it receives updates, it will append them to the chat history.
   */
  private var msgs: Vector[NodeSeq] = Vector(wrap("Welcome", FromServer))

  /**
   * This method produces the update to send to it's listeners when needed.
   * In this case, we're going to tell the listeners what the current chat history is.
   */
  override protected def createUpdate: Any = msgs

  /**
   * This returns the partial function to handle messages sent to this actor.
   * Here we're looking for Strings sent to us from users from anywhere. We
   * append them to the messages currently in the server and update all the listeners.
   */
  override protected def lowPriority: PartialFunction[Any, Unit] = {
    case ChatMessage(mesg, sender) => msgs :+= wrap(mesg, sender); updateListeners()
  }

  sealed trait Sender {
    def level = "label"
    def who: String
  }
  object FromServer extends Sender {
    override def level = super.level + " label-success"
    override def who = "Server"
  }
  case class FromUser(whoami: String) extends Sender {
    override def level = super.level + " label-info"
    override def who = whoami
  }
  object FromGuest extends Sender {
    override def level = super.level + " label-warning"
    override def who = "Guest"
  }

  private def wrap(mesg: String, sender: Sender): NodeSeq =
    <span>
      <span class="label label-default">@{Helpers.formattedTimeNow}</span>
      <span class={sender.level}>{sender.who}</span> <em>said:</em> {mesg}</span>
}
