package forms.snippet

import code.lib.Bootstrap.Alerts.Info
import code.lib.Bootstrap.{Alerts, Glyphicons}
import net.liftweb.common.Full
import net.liftweb.http.{S, SHtml, StatefulSnippet}
import net.liftweb.util.Helpers._

/**
 * This guy is a stateful snippet.
 *
 * Required to be a class, because the snippet is kept around.
 * When a client is served a page, the snippet embeds a cryptographic guid in their page
 *  so that it can be called back to.
 * This isn't insecure as it stores no other state on the page, and any state you'd want
 *  is pretty much accessable on the page anyway.
 */
class Stateful extends StatefulSnippet {

  private var name = ""
  private var age = "0"

  // Never updated
  private val whence = S.referer openOr "/"

  def dispatch : DispatchIt = { case "render" => render }

  /*
   * You notice that below we're actually generating tag content for the page instead of
   * just setting hooks.
   */
  def render = {
    "@name"   #> SHtml.text(name, name = _) &
    "@age"    #> SHtml.text(age, age = _) &
    ":submit" #> SHtml.onSubmitUnit(process)
  }

  def process(): Unit = {
    asInt(age) match {
      case Full(v) if v < 13 => S.error("stateful-age-box", Glyphicons.wrap("Too Young!", "remove-sign"))
      case Full(v) =>
        Alerts !< (Info, s"Name: $name")
        Alerts !< (Info, s"Age: $age")
        S.redirectTo(whence)
      case _ => S.error("stateful-age-box", Glyphicons.wrap("Invalid number!", "remove-sign"))
    }
  }
}
