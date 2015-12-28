package forms.snippet

import code.lib.Bootstrap.Alerts
import code.lib.Bootstrap.Alerts.Info
import net.liftweb.http.LiftScreen

/**
 * You can also, alternatively, use the screen and wizard technology to make forms.
 */
class ScreenExample extends LiftScreen {

  val name = field("Name", "")

  val age = field("Age", 0, minVal(13, "Too Young!"))

  def finish(): Unit = {
    Alerts !< (Info, s"Name: $name")
    Alerts !< (Info, s"Age: $age")
  }

}
