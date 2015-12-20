package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

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
  def boot {

    case class ParamInfo(paramInfo: String)

    // Build SiteMap
    // - Site map is really just a way to dictate which pages you will ever serve for your app.
    //
    // - Pages not in the site map are never to be served.
    //
    // - Menu's have an ID, a display name (identical with single argument) and a 'Loc'.
    //
    // - The Loc is defined by using the slashes, which identifies both the app URL and
    //      where to locate the source html file.
    //
    // - You may use the '**' special Loc to state that all pages defined underneath are
    //      allowed to be served. The Menu that you set this with defaults to point to the
    //      directory above the '**'.
    //
    // - You can attach rules for the locations with ">>".
    // -- Using the "If" rule, you can define a boolean function that decides whether the page
    //      is available (access control)
    // -- Using the "Hidden" rule, you can hide a menu from potential auto display.
    // -- Using the "LocGroup" rule, you can make groupings of menu options.
    //
    // - Menus may be nested in other Menus with "submenus()" method.
    //
    // - Menus can define a page that recieves information from a parameter in the URL.
    // -- Requires two functions, parser (url string to object) and encoder (object to url string)
    def sitemap = SiteMap(
      Menu.i("Index") / "index",
      Menu.i("Chat") / "chat",
      Menu.i("Dev") / "static" / "dev" >> If(() => Props.mode == Props.RunModes.Development, S ? "Not Dev Mode"),
      Menu.i("Prod") / "static" / "prod" >> If(() => Props.mode == Props.RunModes.Production, S ? "Not Prod Mode"),
      Menu.i("Info") / "info" submenus(
        Menu.i("About") / "static" / "about" >> Hidden >> LocGroup("bottom"),
        Menu.i("Contact") / "static" / "contact",
        Menu.i("Feedback") / "static" / "feedback" >> LocGroup("bottom")
      ),
      Menu.i("Static") / "static" / **,
      // Menu for a parameterized page - need a way to turn the string path into a boxed object, and back again
      // with the 'parser' and 'encoder' functions.
      Menu.param[ParamInfo]("Param", "Param", s => Full(ParamInfo(s)), p => p.paramInfo) / "param"
    )

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemap)

    // where to search snippet
    LiftRules.addToPackages("code")

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))
  }
}
