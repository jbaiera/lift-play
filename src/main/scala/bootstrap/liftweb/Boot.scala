package bootstrap.liftweb

import code.lib.Bootstrap
import code.snippet.SpellInfo
import net.liftweb._
import util._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot() {
    configurePersistence()
    LiftRules.setSiteMapFunc(() => sitemap)
    LiftRules.addToPackages("code")
    configureAjax()
    configureEncodings()
    LiftRules.noticesEffects.default.set(Bootstrap.Alerts.getNoticeEffects)
  }

  def userLinkText = User.currentUser.map(_.shortName).openOr("not logged in")

  def sitemap = SiteMap(
    Menu.i("Index") / "index" >> LocGroup("lift-internals"),

    Menu.i("Apps") / "apps" / "#" >> LocGroup("main") >> PlaceHolder submenus (
      Menu.i("Chat") / "apps"/ "chat"),

    Menu.i("Forms") / "forms" / "#" >> LocGroup("main") >> PlaceHolder submenus (
      Menu.i("Dumb") / "forms" / "dumb"),

    Menu.i("Search") / "spellbook" / "search" >> LocGroup("main") submenus(
      Menu.param[SpellInfo]("Spell", "Spell", s => Full(SpellInfo(s)), p => p.spellId) / "spellbook" / "spell" >> Hidden),
    Menu.i("Browse") / "spellbook" / "browse" >> LocGroup("main"),

    User.loginMenuLoc.openOrThrowException("User Module Login Menu Error"),
    User.createUserMenuLoc.openOrThrowException("User Module Create Menu Error"),

    Menu("user", userLinkText) / "#" >> LocGroup("user") >> PlaceHolder submenus (
      User.logoutMenuLoc.openOrThrowException("User Module Logout Menu Error"),
      User.editUserMenuLoc.openOrThrowException("User Module Edit Menu Error"),
      User.changePasswordMenuLoc.openOrThrowException("User Module PWD Menu Error"))
  )

  def configurePersistence(): Unit = {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = new StandardDBVendor(
        Props.get("db.driver") openOr "org.h2.Driver",
        Props.get("db.url") openOr "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
        Props.get("db.user"),
        Props.get("db.password")
      )
      LiftRules.unloadHooks.append(vendor.closeAllConnections_!)
      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }
    Schemifier.schemify(true, Schemifier.infoF _, User)
  }

  def configureAjax(): Unit = {
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)
  }

  def configureEncodings(): Unit = {
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))
  }
}
