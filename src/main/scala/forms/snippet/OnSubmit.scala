package forms.snippet

import code.lib.Bootstrap.Alerts
import code.lib.Bootstrap.Alerts.{Info, Error}
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml, S}

object OnSubmit {

  def render = {
    // some variables to hold the state between executions
    var name = ""
    var age = 0

    // function to call when we need to process the form
    def process(): Unit = {
      // if age is < 13 display an error
      if (age < 13) Alerts ! (Error, "Too young!")
      else {
        Alerts ! (Info, s"Name : $name")
        Alerts ! (Info, s"Age : $age")
        S.redirectTo("/")
      }
    }

    // Return some CSSBindFunc's that do some interesting things:

    "@name" #> SHtml.onSubmit(name = _) &
    "@age" #> SHtml.onSubmit(s => asInt(s).foreach(age = _)) &
    ":submit" #> SHtml.onSubmitUnit(process)

    // Synopsis:
    // First, we're going to use the "@name" binding to search for tags with the name of "name" or "age".
    // Another binding we're going to use is the ":type" binding to find the submit button.
    // We'll transform those nodes by updating their onSubmit functionality.

    // SHtml.onSubmit associates a specific scala function to be called when a form control is submitted to the server.
    // This is handled by associating the function with a guid and keeping the guid in the form control.

    // Since functions in Scala close scope, the functions in this class promote the "name" and "age" variables from
    // the stack to the heap.

    // This allows the process() function to see the state updates from the anonymous functions passed to onSubmit()

    // Remember that the render function, instead of being type (NodeSeq) => NodeSeq, may return a function that is
    // of type (NodeSeq) => NodeSeq, which is something that CSSBindFunc's are able to be cast to.
  }
}
