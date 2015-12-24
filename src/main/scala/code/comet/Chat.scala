package code.comet

import net.liftweb.common.SimpleActor
import net.liftweb.http._
import net.liftweb.http.js.jquery.JqJE.{Jq, JqScrollToBottom}
import net.liftweb.util._

/**
 * This Actor handles changes to the server state (the chat history) by pushing the
 * required changes out to clients using Comet. In this case, this is basically an
 * event call back for the application state, that updates a web page without requiring
 * the page to be reloaded.
 */
class Chat extends CometActor with CometListener {

  def $(s: String): Jq = Jq(s)

  /**
   * This is the current state of the chat room that should be rendered.
   * We will re-render the web page with the contents on update.
   */
  private var msgs: Vector[String] = Vector()

  /**
   * I want to be notified by the ChatServer when things change. When this actor
   * activates, it will register itself to what ever is returned from this method.
   */
  override protected def registerWith: SimpleActor[Any] = ChatServer

  /**
   * Returns a partial function that responds to the messages from ChatServer.
   * When I get an update with a Vector of Strings, update the messages we have
   * and reRender them to the page via a Comet call.
   */
  override def lowPriority: PartialFunction[Any, Unit] = {
    case v: Vector[String] => msgs = v; reRender();
  }

  /**
   * Put the messages in the &lt;li&gt; elements and clear any elements that have the clearable class.
   * Create a css selector object that replaces li and it's children with the msgs to render, and that
   *    clears clearable messages.
   * Also attach some generated javascript. Lift can generate some basic jQuery recipes, in this case,
   *    scroll the "messages" id to the bottom.
   */
  override def render: RenderOut = ("li *" #> msgs & ClearClearable) ++ ($("#messages") ~> JqScrollToBottom).cmd

}