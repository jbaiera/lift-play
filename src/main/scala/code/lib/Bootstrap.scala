package code.lib

import net.liftweb.common.{Empty, Box, Full}
import net.liftweb.http.NoticeType.{Value, Warning, Notice}
import net.liftweb.http.js.{JsCmd, JE}
import net.liftweb.http.{LiftRules, NoticeType, S}

import scala.xml.NodeSeq

object Bootstrap {

  object Alerts {
    sealed trait AlertClass {
      def styleClass = "list-group-item alert"
      def glyph: String
      def lead: String
    }
    object Info extends AlertClass {
      override def styleClass = super.styleClass + " list-group-item-info"
      override def glyph = "info-sign"
      override def lead = "Heads Up!"
    }
    object Warn extends AlertClass {
      override def styleClass = super.styleClass + " list-group-item-warning"
      override def glyph = "exclamation-sign"
      override def lead = "Warning!"
    }
    object Error extends AlertClass {
      override def styleClass = super.styleClass + " list-group-item-error"
      override def glyph = "remove-sign"
      override def lead = "Oh Snap!"
    }

    /**
      * Call this in the Boot class and set it to the [[LiftRules.noticesEffects]]
      * lift rule entry to modify the notice effects.
      * @return A function that maps NoticeTypes and Id's to JS effect functions
      *         that set Bootstrap styling.
      */
    def getNoticeEffects(): (Box[Value], String) => Full[JsCmd] = {
      val jq = "$"

      /*
       * Make some javascript entries that change the notice
       * containers to the boostrap classes and add clear buttons.
       */

      val jsNotice =
        s"""$jq('#lift__noticesContainer___notice li')
          |.addClass("${Info.styleClass}")
          |.append('<button type="button" class="close" data-dismiss="alert">×</button>')
          |$jq('#lift__noticesContainer___notice ul')
          |.addClass("list-group")""".stripMargin

      val jsWarning =
        s"""$jq('#lift__noticesContainer___warning li')
          |.addClass("${Warn.styleClass}")
          |.append('<button type="button" class="close" data-dismiss="alert">×</button>')
          |$jq('#lift__noticesContainer___warning ul')
          |.addClass("list-group")""".stripMargin

      val jsError =
        s"""$jq('#lift__noticesContainer___error li')
          |.addClass("${Error.styleClass}")
          |.append('<button type="button" class="close" data-dismiss="alert">×</button>')
          |$jq('#lift__noticesContainer___error ul')
          |.addClass("list-group")""".stripMargin

      (notice: Box[Value], id: String) => {
        notice match {
          case Full(v) => v match {
            case Notice           => Full(JE.JsRaw( jsNotice ).cmd)
            case Warning          => Full(JE.JsRaw( jsWarning ).cmd)
            case NoticeType.Error => Full(JE.JsRaw( jsError ).cmd)
          }
          case _                  => Full(JE.JsRaw( jsNotice ).cmd)
        }
      }
    }

    def genAlert(lead: Box[String], message: String, alertClass: AlertClass): Unit = {
      val msg = makeMessage(lead, message, alertClass)
      alertClass match {
        case Info => S.notice(msg)
        case Warn => S.warning(msg)
        case Error => S.error(msg)
      }
    }

    def genAlert(message: String, alertClass: AlertClass): Unit = {
      genAlert(Empty, message, alertClass)
    }

    def genAlert(lead: Box[String], id: String, message: String, alertClass: AlertClass): Unit = {
      val msg = makeMessage(lead, message, alertClass)
      alertClass match {
        case Info => S.notice(id, msg)
        case Warn => S.warning(id, msg)
        case Error => S.error(id, msg)
      }
    }

    def genAlert(id: String, message: String, alertClass: AlertClass): Unit = {
      genAlert(Empty, id, message, alertClass)
    }

    private def makeMessage(lead: Box[String], s: String, ac: AlertClass): NodeSeq =
      <span><span class={"glyphicon glyphicon-"+ac.glyph}></span> <strong>{lead openOr ac.lead}</strong> {s}</span>

  }
}
