package code.snippet

import net.liftweb.http.S
import net.liftweb.util.Helpers._

import scala.xml._
import scala.xml.transform.{RewriteRule, RuleTransformer}

object Nav {

  def currentId = S.attr("current-id") openOr "none"

  def bootstrap(in: NodeSeq): NodeSeq = {
    Xfmer.transform(in)
  }

  object Xfmer extends RuleTransformer(XfmRule, SRCurrentRule)

  object XfmRule extends RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case Text(text) if text.matches("\\s+") => NodeSeq.Empty

      case li @ Elem(liPrefix, "li", liAttributes, liScope, // An <li> node
      span @ Elem(spanPrefix, "span", spanAttributes, spanScope, spanChildren @ _*), // with a child <span> elem
      ul @ Elem(ulPrefix, "ul", ulAttributes, ulScope, ulChildren @ _*), // And then a child <ul> elem
      other @ _*) => // and what ever else might be in there.
        Elem(liPrefix, "li", modifyLiAttributes(liAttributes), liScope, true,
            Elem(spanPrefix, "a", modifyAnchorAttributes(spanAttributes), spanScope, true, modifyAnchorChildren(spanChildren): _*) ++
            Elem(ulPrefix, "ul", modifyUlAttributes(ulAttributes), ulScope, true, ulChildren: _*) ++
            other: _*
        )

      case li @ Elem(liPrefix, "li", liAttributes, liScope, // An <li> node
      a @ Elem(aPrefix, "a", aAttributes, aScope, aChildren @ _*), // with a child <a> elem
      ul @ Elem(ulPrefix, "ul", ulAttributes, ulScope, ulChildren @ _*), // And then a child <ul> elem
      other @ _*) => // and what ever else might be in there.
        Elem(liPrefix, "li", modifyLiAttributes(liAttributes), liScope, true,
            Elem(aPrefix, "a", modifyAnchorAttributes(aAttributes), aScope, true, modifyAnchorChildren(aChildren): _*) ++
            Elem(ulPrefix, "ul", modifyUlAttributes(ulAttributes), ulScope, true, ulChildren: _*) ++
            other: _*
        )

      case other @ _ => other
    }

    def modifyLiAttributes(old: MetaData) = appendToClass(old, "dropdown")
    def modifyUlAttributes(old: MetaData) = appendToClass(old, "dropdown-menu")
    def modifyAnchorAttributes(old: MetaData) = {
      appendToClass(old, "dropdown-toggle")
        .append("href" -> "#")
        .append("data-toggle" -> "dropdown")
        .append("role" -> "button")
        .append("aria-haspopup" -> "true")
        .append("aria-expanded" -> "false")
    }
    def modifyAnchorChildren(old: NodeSeq) = old ++ <span class="caret"></span>

    def appendToClass(attributes: MetaData, newClass: String): MetaData = {
      val resultingClass = attributes.get("class")
      .map(_.mkString)
      .filterNot(_ == "")
      .map(_.trim + " ")
      .getOrElse("") + newClass
      attributes.append("class" -> resultingClass)
    }
  }

  object SRCurrentRule extends RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case li @ Elem(liPrefix, "li", liAttributes, liScope, a @ Elem(aPrefix, "a", aAttributes, aScope, aChildren @ _*))
        if li.attributes.get("id").getOrElse(NodeSeq.Empty).map(_.toString().trim).contains(currentId) =>
        Elem(liPrefix, "li", liAttributes, liScope, true,
          Elem(aPrefix, "a", aAttributes, aScope, true, setSROnlyCurrentTag(aChildren): _*)
        )

      case other @ _ => other
    }

    def setSROnlyCurrentTag(children: NodeSeq) = children ++ <span class="sr-only">(current)</span>
  }


}
