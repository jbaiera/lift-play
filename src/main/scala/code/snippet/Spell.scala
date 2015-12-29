package code.snippet

import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.util.Helpers._

import scala.xml.{Text, NodeSeq}

class SpellInfo(val spellId: String) {
  def title = "Spell Name"
  def level = 0
  def school = "Testification"
//  def isRitual = false
  def isRitual = true
  def castTime = "1 action"
  def components = Seq("V", "S", "M")
//  def materials = Full("A pinch of salt or pepper")
  def materials = Empty
  def duration = "Instantaneous"
  def isConcentration = false
//  def isConcentration = true
  def description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin varius enim ac magna mollis, eget interdum sapien sodales. Morbi vehicula vestibulum ultrices. Praesent aliquam sollicitudin egestas. Ut vel feugiat lacus."
//  def atHigherLevels = Full("Aliquam tempor vitae odio at vehicula. Integer in accumsan risus. Sed vestibulum odio leo, id mattis tortor semper interdum.")
  def atHigherLevels = Empty
}

object Spell {
  def getSpellById(spellId: String): Box[SpellInfo] = if (spellId == "1234") Full(new SpellInfo(spellId)) else Empty
}

class Spell(spellParam: SpellInfo) {
  def render = {
    "#title"            #> spellParam.title &
    "#ritual"           #> ((in: NodeSeq) => if (spellParam.isRitual) Text(" Ritual") else NodeSeq.Empty) &
    "#ritual-tag"       #> ((in: NodeSeq) => if (spellParam.isRitual) in else NodeSeq.Empty) &
    "#concentrate"      #> ((in: NodeSeq) => if (spellParam.isConcentration) Text(" (Concentration)") else NodeSeq.Empty) &
    "#concentrate-tag"  #> ((in: NodeSeq) => if (spellParam.isConcentration) in else NodeSeq.Empty) &
    "#spell-level"      #> spellParam.level &
    "#school"           #> spellParam.school &
    "#cast-time"        #> spellParam.castTime &
    "#components"       #> spellParam.components.mkString("[", ", ", "]") &
    "#materials"        #> spellParam.materials.map("(" + _ + ")") &
    "#duration"         #> spellParam.duration &
    "#description"      #> spellParam.description &
    "#higher-level"     #> spellParam.atHigherLevels.map(s => <dt>At Higher Levels</dt><dd>{s}</dd>)
  }
}
