package forms.snippet

import code.lib.Bootstrap.Alerts.Info
import code.lib.Bootstrap.{Alerts, Glyphicons}
import net.liftweb.common.Full
import net.liftweb.http.{SHtml, S, RequestVar}
import net.liftweb.util.Helpers._

/**
 * Vars are a type of variable that magically keeps state within a specific scope,
 * but also keeps the state separate across different client requests. RequestVars
 * are only kept around for the current HTTP request (page load and all subsequent
 * AJAX requests).
 */
object ReqVar {

  private object name extends RequestVar("")
  private object age extends RequestVar("0")
  private object whence extends RequestVar(S.referer openOr "/")

  def render = {
    val w = whence.is

    /*
     * RequestVars are "Settable" which means we don't have to
     * provide a get and set function.
     */
    "@name" #> SHtml.textElem(name) &
    "@age" #> SHtml.textElem(age) &
    "#hidden-whence" #> SHtml.hidden(() => whence.set(w)) & // Capture the whence value to reset back into the request var
    ":submit" #> SHtml.onSubmitUnit(process)
  }

  def process(): Unit = {
    asInt(age.is) match {
      case Full(v) if v < 13 => S.error("reqvar-age-box", Glyphicons.wrap("Too Young!", "remove-sign"))
      case Full(v) =>
        Alerts !< (Info, s"Name: $name")
        Alerts !< (Info, s"Age: $v")
        S.redirectTo(whence)
      case _ => S.error("reqvar-age-box", Glyphicons.wrap("Invalid Age", "remove-sign"))
    }
  }
}
