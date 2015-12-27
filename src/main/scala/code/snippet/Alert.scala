package code.snippet

import net.liftweb.common.Full
import net.liftweb.http.{DispatchSnippet, S}

import scala.xml.NodeSeq

object Alert extends DispatchSnippet {

  private val noticeClass = "label label-info"
  private val warningClass = "label label-warning"
  private val errorClass = "label label-danger"

  def dispatch:DispatchIt = {
    case _ => render
  }

  def render(ignore: NodeSeq): NodeSeq = {
    S.attr("id") match {
      case Full(id) =>
        <span data-lift={s"Msg?id=$id;noticeClass=$noticeClass;warningClass=$warningClass;errorClass=$errorClass"}></span>
      case _ => NodeSeq.Empty
    }
  }
}
