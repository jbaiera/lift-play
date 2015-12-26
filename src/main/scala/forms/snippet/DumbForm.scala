package forms.snippet

import code.lib.Bootstrap.Alerts
import code.lib.Bootstrap.Alerts.Info
import net.liftweb.http.S

import scala.xml.NodeSeq

object DumbForm {

  /**
    * Box objects are from the Lift framework and are pretty much just
    * Option objects with more features. Box objects have many iterable
    * based methods, we'll exploit that to make the unboxing process go
    * faster. If the boxes are empty, then the for loop will just not
    * execute. Check to see if the current request is a post. We're posting
    * back to this page. Redirect to the index to break out of any further
    * page processing. Form not submitted or filled out correctly? Just
    * keep swimming.
    *
    * @param in NodeSeq that we're attached to
    * @return Either the given NodeSeq or adds notices and redirects.
    */
  def render(in: NodeSeq): NodeSeq = {
    for {
      r <- S.request if r.post_?
      name <- S.param("name")
      age <- S.param("age")
    } {
      Alerts ! (Info, s"Name: $name")
      Alerts ! (Info, s"Age: $age")
      S.redirectTo("/")
    }

    in
  }
}
