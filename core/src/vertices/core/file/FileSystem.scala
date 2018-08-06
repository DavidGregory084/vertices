package vertices
package core.file

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.CopyOptions
import io.vertx.core.file.FileProps
import io.vertx.core.file.FileSystemProps
import io.vertx.core.file.OpenOptions
import io.vertx.core.file.{ AsyncFile => JavaAsyncFile }
import io.vertx.core.file.{ FileSystem => JavaFileSystem }
import java.lang.Boolean
import java.lang.String
import java.lang.Void
import java.util.List
import monix.eval.Task

import scala.language.implicitConversions

case class FileSystem(val unwrap: JavaFileSystem) extends AnyVal {
  // Async handler method
  def copy(from: String, to: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.copy(from, to, handler)
    }.map(_ => ())

  // Async handler method
  def copy(from: String, to: String, options: CopyOptions): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.copy(from, to, options, handler)
    }.map(_ => ())

  // Wrapper method
  def copyBlocking(from: String, to: String): FileSystem =
    FileSystem(unwrap.copyBlocking(from, to))

  // Async handler method
  def copyRecursive(from: String, to: String, recursive: Boolean): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.copyRecursive(from, to, recursive, handler)
    }.map(_ => ())

  // Wrapper method
  def copyRecursiveBlocking(from: String, to: String, recursive: Boolean): FileSystem =
    FileSystem(unwrap.copyRecursiveBlocking(from, to, recursive))

  // Async handler method
  def move(from: String, to: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.move(from, to, handler)
    }.map(_ => ())

  // Async handler method
  def move(from: String, to: String, options: CopyOptions): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.move(from, to, options, handler)
    }.map(_ => ())

  // Wrapper method
  def moveBlocking(from: String, to: String): FileSystem =
    FileSystem(unwrap.moveBlocking(from, to))

  // Async handler method
  def truncate(path: String, len: Long): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.truncate(path, len, handler)
    }.map(_ => ())

  // Wrapper method
  def truncateBlocking(path: String, len: Long): FileSystem =
    FileSystem(unwrap.truncateBlocking(path, len))

  // Async handler method
  def chmod(path: String, perms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.chmod(path, perms, handler)
    }.map(_ => ())

  // Wrapper method
  def chmodBlocking(path: String, perms: String): FileSystem =
    FileSystem(unwrap.chmodBlocking(path, perms))

  // Async handler method
  def chmodRecursive(path: String, perms: String, dirPerms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.chmodRecursive(path, perms, dirPerms, handler)
    }.map(_ => ())

  // Wrapper method
  def chmodRecursiveBlocking(path: String, perms: String, dirPerms: String): FileSystem =
    FileSystem(unwrap.chmodRecursiveBlocking(path, perms, dirPerms))

  // Async handler method
  def chown(path: String, user: String, group: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.chown(path, user, group, handler)
    }.map(_ => ())

  // Wrapper method
  def chownBlocking(path: String, user: String, group: String): FileSystem =
    FileSystem(unwrap.chownBlocking(path, user, group))

  // Async handler method
  def props(path: String): Task[FileProps] =
    Task.handle[FileProps] { handler =>
      unwrap.props(path, handler)
    }

  // Standard method
  def propsBlocking(path: String): FileProps =
    unwrap.propsBlocking(path)

  // Async handler method
  def lprops(path: String): Task[FileProps] =
    Task.handle[FileProps] { handler =>
      unwrap.lprops(path, handler)
    }

  // Standard method
  def lpropsBlocking(path: String): FileProps =
    unwrap.lpropsBlocking(path)

  // Async handler method
  def link(link: String, existing: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.link(link, existing, handler)
    }.map(_ => ())

  // Wrapper method
  def linkBlocking(link: String, existing: String): FileSystem =
    FileSystem(unwrap.linkBlocking(link, existing))

  // Async handler method
  def symlink(link: String, existing: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.symlink(link, existing, handler)
    }.map(_ => ())

  // Wrapper method
  def symlinkBlocking(link: String, existing: String): FileSystem =
    FileSystem(unwrap.symlinkBlocking(link, existing))

  // Async handler method
  def unlink(link: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.unlink(link, handler)
    }.map(_ => ())

  // Wrapper method
  def unlinkBlocking(link: String): FileSystem =
    FileSystem(unwrap.unlinkBlocking(link))

  // Async handler method
  def readSymlink(link: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.readSymlink(link, handler)
    }

  // Standard method
  def readSymlinkBlocking(link: String): String =
    unwrap.readSymlinkBlocking(link)

  // Async handler method
  def delete(path: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.delete(path, handler)
    }.map(_ => ())

  // Wrapper method
  def deleteBlocking(path: String): FileSystem =
    FileSystem(unwrap.deleteBlocking(path))

  // Async handler method
  def deleteRecursive(path: String, recursive: Boolean): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.deleteRecursive(path, recursive, handler)
    }.map(_ => ())

  // Wrapper method
  def deleteRecursiveBlocking(path: String, recursive: Boolean): FileSystem =
    FileSystem(unwrap.deleteRecursiveBlocking(path, recursive))

  // Async handler method
  def mkdir(path: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.mkdir(path, handler)
    }.map(_ => ())

  // Wrapper method
  def mkdirBlocking(path: String): FileSystem =
    FileSystem(unwrap.mkdirBlocking(path))

  // Async handler method
  def mkdir(path: String, perms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.mkdir(path, perms, handler)
    }.map(_ => ())

  // Wrapper method
  def mkdirBlocking(path: String, perms: String): FileSystem =
    FileSystem(unwrap.mkdirBlocking(path, perms))

  // Async handler method
  def mkdirs(path: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.mkdirs(path, handler)
    }.map(_ => ())

  // Wrapper method
  def mkdirsBlocking(path: String): FileSystem =
    FileSystem(unwrap.mkdirsBlocking(path))

  // Async handler method
  def mkdirs(path: String, perms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.mkdirs(path, perms, handler)
    }.map(_ => ())

  // Wrapper method
  def mkdirsBlocking(path: String, perms: String): FileSystem =
    FileSystem(unwrap.mkdirsBlocking(path, perms))

  // Async handler method
  def readDir(path: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.readDir(path, handler)
    }

  // Standard method
  def readDirBlocking(path: String): List[String] =
    unwrap.readDirBlocking(path)

  // Async handler method
  def readDir(path: String, filter: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.readDir(path, filter, handler)
    }

  // Standard method
  def readDirBlocking(path: String, filter: String): List[String] =
    unwrap.readDirBlocking(path, filter)

  // Async handler method
  def readFile(path: String): Task[Buffer] =
    Task.handle[Buffer] { handler =>
      unwrap.readFile(path, handler)
    }

  // Standard method
  def readFileBlocking(path: String): Buffer =
    unwrap.readFileBlocking(path)

  // Async handler method
  def writeFile(path: String, data: Buffer): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.writeFile(path, data, handler)
    }.map(_ => ())

  // Wrapper method
  def writeFileBlocking(path: String, data: Buffer): FileSystem =
    FileSystem(unwrap.writeFileBlocking(path, data))

  // Async handler method
  def open(path: String, options: OpenOptions): Task[AsyncFile] =
    Task.handle[JavaAsyncFile] { handler =>
      unwrap.open(path, options, handler)
    }.map(out => AsyncFile(out))

  // Wrapper method
  def openBlocking(path: String, options: OpenOptions): AsyncFile =
    AsyncFile(unwrap.openBlocking(path, options))

  // Async handler method
  def createFile(path: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.createFile(path, handler)
    }.map(_ => ())

  // Wrapper method
  def createFileBlocking(path: String): FileSystem =
    FileSystem(unwrap.createFileBlocking(path))

  // Async handler method
  def createFile(path: String, perms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.createFile(path, perms, handler)
    }.map(_ => ())

  // Wrapper method
  def createFileBlocking(path: String, perms: String): FileSystem =
    FileSystem(unwrap.createFileBlocking(path, perms))

  // Async handler method
  def exists(path: String): Task[Boolean] =
    Task.handle[java.lang.Boolean] { handler =>
      unwrap.exists(path, handler)
    }.map(out => out: Boolean)

  // Standard method
  def existsBlocking(path: String): Boolean =
    unwrap.existsBlocking(path)

  // Async handler method
  def fsProps(path: String): Task[FileSystemProps] =
    Task.handle[FileSystemProps] { handler =>
      unwrap.fsProps(path, handler)
    }

  // Standard method
  def fsPropsBlocking(path: String): FileSystemProps =
    unwrap.fsPropsBlocking(path)
}
object FileSystem {
  implicit def javaFileSystemToVerticesFileSystem(j: JavaFileSystem): FileSystem = apply(j)
  implicit def verticesFileSystemToJavaFileSystem(v: FileSystem): JavaFileSystem = v.unwrap


}
