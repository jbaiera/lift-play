package code.snippet

import code.comet.ChatServer
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds.SetValById

/**
 * This is what Lift calls a "snippet". Basically all html tags can define
 * snippets that modify their behavior in some way. Plainly put, snippets
 * modify data by way of transformation, taking an element in, modifying it,
 * and returning the modified data.
 */
object ChatIn {

  /**
   * This method returns a function that performs the mutation.
   * The function it returns takes in the &lt;input class="..."&gt; tag as
   * input, and mutates it by setting the "onSubmit" hook for the input.
   * The 'onSubmit' function takes in a function to handle the string data
   * provided by the input tag. The lambda submits the string to the chat
   * server, then returns a javascript command that sets the input box to
   * be empty thus causing no page loads.
   */
  def render = SHtml.onSubmit(s => {
    ChatServer ! s
    SetValById("chat_in", "")
  })
}
