package code
package lib

import net.liftweb._
import http._
import util._
import common._
import java.util.Date

/**
 * A factory for generating new instances of Date.  You can create
 * factories for each kind of thing you want to vend in your application.
 * An example is a payment gateway.  You can change the default implementation,
 * or override the default implementation on a session, request or current call
 * stack basis.
 */
object DependencyFactory extends Factory {
  // I guess this is just one of the bindings for the injection...
  // Some form of voodoo happens which tells this Factory that if it's asked for a Date object to
  //    just call the Helpers.now function, which returns "new Date"
  implicit object time extends FactoryMaker(Helpers.now _)

  /**
   * objects in Scala are lazily created.  The init()
   * method creates a List of all the objects.  This
   * results in all the objects getting initialized and
   * registering their types with the dependency injector
   */
  // I guess based off the comment above, because the binding is an "object"
  //    but we never refer to it outside of here, then we need to force it to
  //    be instantiated internally when we use the DependencyFactory for the
  //    first time...
  private def init() {
    List(time)
  }
  init()
}

// Time will tell what this stuff is... Hmm...
/*
/**
 * Examples of changing the implementation
 */
sealed abstract class Changer {
  def changeDefaultImplementation() {
    DependencyFactory.time.default.set(() => new Date())
  }

  def changeSessionImplementation() {
    DependencyFactory.time.session.set(() => new Date())
  }

  def changeRequestImplementation() {
    DependencyFactory.time.request.set(() => new Date())
  }

  def changeJustForCall(d: Date) {
    DependencyFactory.time.doWith(d) {
      // perform some calculations here
    }
  }
}
*/
