import sbt._

import java.{util => ju}
import scala.collection.jcl.Conversions._
import org.apache.ivy.plugins.resolver._

trait GenerateChecksums extends BasicManagedProject {
  override def ivySbt = {
    def setChecksums(resolver: DependencyResolver): Unit = resolver match {
      case r: ChainResolver =>
        r.getResolvers foreach {
          case child: DependencyResolver => setChecksums(child)
        }
      case r: RepositoryResolver =>
        r.setChecksums("sha1,md5")
    }

    val i = super.ivySbt
    i.withIvy { ivy =>
      ivy.getSettings.getResolvers.toList foreach {
        case r: DependencyResolver => setChecksums(r)
      }
    }
    i
  }

  private implicit def juCollection2Iterable[A](c: ju.Collection[A]): Iterable[A] = {
    val list = new ju.ArrayList[A](c.size)
    list.addAll(c)
    list
  }
}
