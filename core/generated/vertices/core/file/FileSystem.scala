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

  /**
   *  Contains a broad set of operations for manipulating files on the file system.
   *  <p>
   *  A (potential) blocking and non blocking version of each operation is provided.
   *  <p>
   *  The non blocking versions take a handler which is called when the operation completes or an error occurs.
   *  <p>
   *  The blocking versions are named {@code xxxBlocking} and return the results, or throw exceptions directly.
   *  In many cases, depending on the operating system and file system some of the potentially blocking operations
   *  can return quickly, which is why we provide them, but it's highly recommended that you test how long they take to
   *  return in your particular application before using them on an event loop.
   *  <p>
   *  Please consult the documentation for more information on file system support.
   * @author <a href="http://tfox.org">Tim Fox</a>
   */
case class FileSystem(val unwrap: JavaFileSystem) extends AnyVal {
  /**
   *  Copy a file from the path {@code from} to path {@code to}, asynchronously.
   *  <p>
   *  The copy will fail if the destination already exists.
   * @param from  the path to copy from
   * @param to  the path to copy to
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def copy(from: String, to: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.copy(from, to, handler)
    }.map(_ => ())

  /**
   *  Copy a file from the path {@code from} to path {@code to}, asynchronously.
   * @param from    the path to copy from
   * @param to      the path to copy to
   * @param options options describing how the file should be copied
   * @param handler the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def copy(from: String, to: String, options: CopyOptions): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.copy(from, to, options, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #copy(String, String, Handler)}
   */
  def copyBlocking(from: String, to: String): FileSystem =
    FileSystem(unwrap.copyBlocking(from, to))

  /**
   *  Copy a file from the path {@code from} to path {@code to}, asynchronously.
   *  <p>
   *  If {@code recursive} is {@code true} and {@code from} represents a directory, then the directory and its contents
   *  will be copied recursively to the destination {@code to}.
   *  <p>
   *  The copy will fail if the destination if the destination already exists.
   * @param from  the path to copy from
   * @param to  the path to copy to
   * @param recursive
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def copyRecursive(from: String, to: String, recursive: Boolean): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.copyRecursive(from, to, recursive, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #copyRecursive(String, String, boolean, Handler)}
   */
  def copyRecursiveBlocking(from: String, to: String, recursive: Boolean): FileSystem =
    FileSystem(unwrap.copyRecursiveBlocking(from, to, recursive))

  /**
   *  Move a file from the path {@code from} to path {@code to}, asynchronously.
   *  <p>
   *  The move will fail if the destination already exists.
   * @param from  the path to copy from
   * @param to  the path to copy to
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def move(from: String, to: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.move(from, to, handler)
    }.map(_ => ())

  /**
   *  Move a file from the path {@code from} to path {@code to}, asynchronously.
   * @param from    the path to copy from
   * @param to      the path to copy to
   * @param options options describing how the file should be copied
   * @param handler the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def move(from: String, to: String, options: CopyOptions): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.move(from, to, options, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #move(String, String, Handler)}
   */
  def moveBlocking(from: String, to: String): FileSystem =
    FileSystem(unwrap.moveBlocking(from, to))

  /**
   *  Truncate the file represented by {@code path} to length {@code len} in bytes, asynchronously.
   *  <p>
   *  The operation will fail if the file does not exist or {@code len} is less than {@code zero}.
   * @param path  the path to the file
   * @param len  the length to truncate it to
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def truncate(path: String, len: Long): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.truncate(path, len, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #truncate(String, long, Handler)}
   */
  def truncateBlocking(path: String, len: Long): FileSystem =
    FileSystem(unwrap.truncateBlocking(path, len))

  /**
   *  Change the permissions on the file represented by {@code path} to {@code perms}, asynchronously.
   *  <p>
   *  The permission String takes the form rwxr-x--- as
   *  specified <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
   * @param path  the path to the file
   * @param perms  the permissions string
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def chmod(path: String, perms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.chmod(path, perms, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #chmod(String, String, Handler) }
   */
  def chmodBlocking(path: String, perms: String): FileSystem =
    FileSystem(unwrap.chmodBlocking(path, perms))

  /**
   *  Change the permissions on the file represented by {@code path} to {@code perms}, asynchronously.<p>
   *  The permission String takes the form rwxr-x--- as
   *  specified in {<a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>}.
   *  <p>
   *  If the file is directory then all contents will also have their permissions changed recursively. Any directory permissions will
   *  be set to {@code dirPerms}, whilst any normal file permissions will be set to {@code perms}.
   * @param path  the path to the file
   * @param perms  the permissions string
   * @param dirPerms  the directory permissions
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def chmodRecursive(path: String, perms: String, dirPerms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.chmodRecursive(path, perms, dirPerms, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #chmodRecursive(String, String, String, Handler)}
   */
  def chmodRecursiveBlocking(path: String, perms: String, dirPerms: String): FileSystem =
    FileSystem(unwrap.chmodRecursiveBlocking(path, perms, dirPerms))

  /**
   *  Change the ownership on the file represented by {@code path} to {@code user} and {code group}, asynchronously.
   * @param path  the path to the file
   * @param user  the user name, {@code null} will not change the user name
   * @param group  the user group, {@code null} will not change the user group name
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def chown(path: String, user: String, group: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.chown(path, user, group, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #chown(String, String, String, Handler)}
   */
  def chownBlocking(path: String, user: String, group: String): FileSystem =
    FileSystem(unwrap.chownBlocking(path, user, group))

  /**
   *  Obtain properties for the file represented by {@code path}, asynchronously.
   *  <p>
   *  If the file is a link, the link will be followed.
   * @param path  the path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def props(path: String): Task[FileProps] =
    Task.handle[FileProps] { handler =>
      unwrap.props(path, handler)
    }

  /**
   *  Blocking version of {@link #props(String, Handler)}
   */
  def propsBlocking(path: String): FileProps =
    unwrap.propsBlocking(path)

  /**
   *  Obtain properties for the link represented by {@code path}, asynchronously.
   *  <p>
   *  The link will not be followed.
   * @param path  the path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def lprops(path: String): Task[FileProps] =
    Task.handle[FileProps] { handler =>
      unwrap.lprops(path, handler)
    }

  /**
   *  Blocking version of {@link #lprops(String, Handler)}
   */
  def lpropsBlocking(path: String): FileProps =
    unwrap.lpropsBlocking(path)

  /**
   *  Create a hard link on the file system from {@code link} to {@code existing}, asynchronously.
   * @param link  the link
   * @param existing  the link destination
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def link(link: String, existing: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.link(link, existing, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #link(String, String, Handler)}
   */
  def linkBlocking(link: String, existing: String): FileSystem =
    FileSystem(unwrap.linkBlocking(link, existing))

  /**
   *  Create a symbolic link on the file system from {@code link} to {@code existing}, asynchronously.
   * @param link  the link
   * @param existing  the link destination
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def symlink(link: String, existing: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.symlink(link, existing, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #link(String, String, Handler)}
   */
  def symlinkBlocking(link: String, existing: String): FileSystem =
    FileSystem(unwrap.symlinkBlocking(link, existing))

  /**
   *  Unlinks the link on the file system represented by the path {@code link}, asynchronously.
   * @param link  the link
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def unlink(link: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.unlink(link, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #unlink(String, Handler)}
   */
  def unlinkBlocking(link: String): FileSystem =
    FileSystem(unwrap.unlinkBlocking(link))

  /**
   *  Returns the path representing the file that the symbolic link specified by {@code link} points to, asynchronously.
   * @param link  the link
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def readSymlink(link: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.readSymlink(link, handler)
    }

  /**
   *  Blocking version of {@link #readSymlink(String, Handler)}
   */
  def readSymlinkBlocking(link: String): String =
    unwrap.readSymlinkBlocking(link)

  /**
   *  Deletes the file represented by the specified {@code path}, asynchronously.
   * @param path  path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def delete(path: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.delete(path, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #delete(String, Handler)}
   */
  def deleteBlocking(path: String): FileSystem =
    FileSystem(unwrap.deleteBlocking(path))

  /**
   *  Deletes the file represented by the specified {@code path}, asynchronously.
   *  <p>
   *  If the path represents a directory and {@code recursive = true} then the directory and its contents will be
   *  deleted recursively.
   * @param path  path to the file
   * @param recursive  delete recursively?
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def deleteRecursive(path: String, recursive: Boolean): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.deleteRecursive(path, recursive, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #deleteRecursive(String, boolean, Handler)}
   */
  def deleteRecursiveBlocking(path: String, recursive: Boolean): FileSystem =
    FileSystem(unwrap.deleteRecursiveBlocking(path, recursive))

  /**
   *  Create the directory represented by {@code path}, asynchronously.
   *  <p>
   *  The operation will fail if the directory already exists.
   * @param path  path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def mkdir(path: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.mkdir(path, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #mkdir(String, Handler)}
   */
  def mkdirBlocking(path: String): FileSystem =
    FileSystem(unwrap.mkdirBlocking(path))

  /**
   *  Create the directory represented by {@code path}, asynchronously.
   *  <p>
   *  The new directory will be created with permissions as specified by {@code perms}.
   *  <p>
   *  The permission String takes the form rwxr-x--- as specified
   *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
   *  <p>
   *  The operation will fail if the directory already exists.
   * @param path  path to the file
   * @param perms  the permissions string
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def mkdir(path: String, perms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.mkdir(path, perms, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #mkdir(String, String, Handler)}
   */
  def mkdirBlocking(path: String, perms: String): FileSystem =
    FileSystem(unwrap.mkdirBlocking(path, perms))

  /**
   *  Create the directory represented by {@code path} and any non existent parents, asynchronously.
   *  <p>
   *  The operation will fail if the directory already exists.
   * @param path  path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def mkdirs(path: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.mkdirs(path, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #mkdirs(String, Handler)}
   */
  def mkdirsBlocking(path: String): FileSystem =
    FileSystem(unwrap.mkdirsBlocking(path))

  /**
   *  Create the directory represented by {@code path} and any non existent parents, asynchronously.
   *  <p>
   *  The new directory will be created with permissions as specified by {@code perms}.
   *  <p>
   *  The permission String takes the form rwxr-x--- as specified
   *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
   *  <p>
   *  The operation will fail if the directory already exists.<p>
   * @param path  path to the file
   * @param perms  the permissions string
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def mkdirs(path: String, perms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.mkdirs(path, perms, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #mkdirs(String, String, Handler)}
   */
  def mkdirsBlocking(path: String, perms: String): FileSystem =
    FileSystem(unwrap.mkdirsBlocking(path, perms))

  /**
   *  Read the contents of the directory specified by {@code path}, asynchronously.
   *  <p>
   *  The result is an array of String representing the paths of the files inside the directory.
   * @param path  path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def readDir(path: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.readDir(path, handler)
    }

  /**
   *  Blocking version of {@link #readDir(String, Handler)}
   */
  def readDirBlocking(path: String): List[String] =
    unwrap.readDirBlocking(path)

  /**
   *  Read the contents of the directory specified by {@code path}, asynchronously.
   *  <p>
   *  The parameter {@code filter} is a regular expression. If {@code filter} is specified then only the paths that
   *  match  @{filter}will be returned.
   *  <p>
   *  The result is an array of String representing the paths of the files inside the directory.
   * @param path  path to the directory
   * @param filter  the filter expression
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def readDir(path: String, filter: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.readDir(path, filter, handler)
    }

  /**
   *  Blocking version of {@link #readDir(String, String, Handler)}
   */
  def readDirBlocking(path: String, filter: String): List[String] =
    unwrap.readDirBlocking(path, filter)

  /**
   *  Reads the entire file as represented by the path {@code path} as a {@link Buffer}, asynchronously.
   *  <p>
   *  Do not use this method to read very large files or you risk running out of available RAM.
   * @param path  path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def readFile(path: String): Task[Buffer] =
    Task.handle[Buffer] { handler =>
      unwrap.readFile(path, handler)
    }

  /**
   *  Blocking version of {@link #readFile(String, Handler)}
   */
  def readFileBlocking(path: String): Buffer =
    unwrap.readFileBlocking(path)

  /**
   *  Creates the file, and writes the specified {@code Buffer data} to the file represented by the path {@code path},
   *  asynchronously.
   * @param path  path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def writeFile(path: String, data: Buffer): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.writeFile(path, data, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #writeFile(String, Buffer, Handler)}
   */
  def writeFileBlocking(path: String, data: Buffer): FileSystem =
    FileSystem(unwrap.writeFileBlocking(path, data))

  /**
   *  Open the file represented by {@code path}, asynchronously.
   *  <p>
   *  The file is opened for both reading and writing. If the file does not already exist it will be created.
   * @param path  path to the file
   * @param options options describing how the file should be opened
   * @return a reference to this, so the API can be used fluently
   */
  def open(path: String, options: OpenOptions): Task[AsyncFile] =
    Task.handle[JavaAsyncFile] { handler =>
      unwrap.open(path, options, handler)
    }.map(out => AsyncFile(out))

  /**
   *  Blocking version of {@link #open(String, io.vertx.core.file.OpenOptions, Handler)}
   */
  def openBlocking(path: String, options: OpenOptions): AsyncFile =
    AsyncFile(unwrap.openBlocking(path, options))

  /**
   *  Creates an empty file with the specified {@code path}, asynchronously.
   * @param path  path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def createFile(path: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.createFile(path, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #createFile(String, Handler)}
   */
  def createFileBlocking(path: String): FileSystem =
    FileSystem(unwrap.createFileBlocking(path))

  /**
   *  Creates an empty file with the specified {@code path} and permissions {@code perms}, asynchronously.
   * @param path  path to the file
   * @param perms  the permissions string
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def createFile(path: String, perms: String): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.createFile(path, perms, handler)
    }.map(_ => ())

  /**
   *  Blocking version of {@link #createFile(String, String, Handler)}
   */
  def createFileBlocking(path: String, perms: String): FileSystem =
    FileSystem(unwrap.createFileBlocking(path, perms))

  /**
   *  Determines whether the file as specified by the path {@code path} exists, asynchronously.
   * @param path  path to the file
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def exists(path: String): Task[Boolean] =
    Task.handle[java.lang.Boolean] { handler =>
      unwrap.exists(path, handler)
    }.map(out => out: Boolean)

  /**
   *  Blocking version of {@link #exists(String, Handler)}
   */
  def existsBlocking(path: String): Boolean =
    unwrap.existsBlocking(path)

  /**
   *  Returns properties of the file-system being used by the specified {@code path}, asynchronously.
   * @param path  path to anywhere on the filesystem
   * @param handler  the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def fsProps(path: String): Task[FileSystemProps] =
    Task.handle[FileSystemProps] { handler =>
      unwrap.fsProps(path, handler)
    }

  /**
   *  Blocking version of {@link #fsProps(String, Handler)}
   */
  def fsPropsBlocking(path: String): FileSystemProps =
    unwrap.fsPropsBlocking(path)

  /**
   *  Creates a new directory in the default temporary-file directory, using the given
   *  prefix to generate its name, asynchronously.
   * 
   *  <p>
   *  As with the {@code File.createTempFile} methods, this method is only
   *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
   *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
   *  </p>
   * @param prefix  the prefix string to be used in generating the directory's name;
   *                 may be {@code null}
   * @param handler the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def createTempDirectory(prefix: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.createTempDirectory(prefix, handler)
    }

  /**
   *  Blocking version of {@link #createTempDirectory(String, Handler)}
   */
  def createTempDirectoryBlocking(prefix: String): String =
    unwrap.createTempDirectoryBlocking(prefix)

  /**
   *  Creates a new directory in the default temporary-file directory, using the given
   *  prefix to generate its name, asynchronously.
   *  <p>
   *  The new directory will be created with permissions as specified by {@code perms}.
   *  </p>
   *  The permission String takes the form rwxr-x--- as specified
   *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
   * 
   *  <p>
   *  As with the {@code File.createTempFile} methods, this method is only
   *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
   *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
   *  </p>
   * @param prefix  the prefix string to be used in generating the directory's name;
   *                 may be {@code null}
   * @param perms   the permissions string
   * @param handler the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def createTempDirectory(prefix: String, perms: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.createTempDirectory(prefix, perms, handler)
    }

  /**
   *  Blocking version of {@link #createTempDirectory(String, String, Handler)}
   */
  def createTempDirectoryBlocking(prefix: String, perms: String): String =
    unwrap.createTempDirectoryBlocking(prefix, perms)

  /**
   *  Creates a new directory in the directory provided by the path {@code path}, using the given
   *  prefix to generate its name, asynchronously.
   *  <p>
   *  The new directory will be created with permissions as specified by {@code perms}.
   *  </p>
   *  The permission String takes the form rwxr-x--- as specified
   *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
   * 
   *  <p>
   *  As with the {@code File.createTempFile} methods, this method is only
   *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
   *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
   *  </p>
   * @param dir     the path to directory in which to create the directory
   * @param prefix  the prefix string to be used in generating the directory's name;
   *                 may be {@code null}
   * @param perms   the permissions string
   * @param handler the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def createTempDirectory(dir: String, prefix: String, perms: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.createTempDirectory(dir, prefix, perms, handler)
    }

  /**
   *  Blocking version of {@link #createTempDirectory(String, String, String, Handler)}
   */
  def createTempDirectoryBlocking(dir: String, prefix: String, perms: String): String =
    unwrap.createTempDirectoryBlocking(dir, prefix, perms)

  /**
   *  Creates a new file in the default temporary-file directory, using the given
   *  prefix and suffix to generate its name, asynchronously.
   * 
   *  <p>
   *  As with the {@code File.createTempFile} methods, this method is only
   *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
   *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
   *  </p>
   * @param prefix  the prefix string to be used in generating the directory's name;
   *                 may be {@code null}
   * @param suffix  the suffix string to be used in generating the file's name;
   *                 may be {@code null}, in which case "{@code .tmp}" is used
   * @param handler the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def createTempFile(prefix: String, suffix: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.createTempFile(prefix, suffix, handler)
    }

  /**
   *  Blocking version of {@link #createTempFile(String, String, Handler)}
   */
  def createTempFileBlocking(prefix: String, suffix: String): String =
    unwrap.createTempFileBlocking(prefix, suffix)

  /**
   *  Creates a new file in the directory provided by the path {@code dir}, using the given
   *  prefix and suffix to generate its name, asynchronously.
   * 
   *  <p>
   *  As with the {@code File.createTempFile} methods, this method is only
   *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
   *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
   *  </p>
   * @param prefix  the prefix string to be used in generating the directory's name;
   *                 may be {@code null}
   * @param suffix  the suffix string to be used in generating the file's name;
   *                 may be {@code null}, in which case "{@code .tmp}" is used
   * @param handler the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def createTempFile(prefix: String, suffix: String, perms: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.createTempFile(prefix, suffix, perms, handler)
    }

  /**
   *  Blocking version of {@link #createTempFile(String, String, String, Handler)}
   */
  def createTempFileBlocking(prefix: String, suffix: String, perms: String): String =
    unwrap.createTempFileBlocking(prefix, suffix, perms)

  /**
   *  Creates a new file in the directory provided by the path {@code dir}, using the given
   *  prefix and suffix to generate its name, asynchronously.
   *  <p>
   *  The new directory will be created with permissions as specified by {@code perms}.
   *  </p>
   *  The permission String takes the form rwxr-x--- as specified
   *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
   * 
   *  <p>
   *  As with the {@code File.createTempFile} methods, this method is only
   *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
   *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
   *  </p>
   * @param dir     the path to directory in which to create the directory
   * @param prefix  the prefix string to be used in generating the directory's name;
   *                 may be {@code null}
   * @param suffix  the suffix string to be used in generating the file's name;
   *                 may be {@code null}, in which case "{@code .tmp}" is used
   * @param perms   the permissions string
   * @param handler the handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def createTempFile(dir: String, prefix: String, suffix: String, perms: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.createTempFile(dir, prefix, suffix, perms, handler)
    }

  /**
   *  Blocking version of {@link #createTempFile(String, String, String, String, Handler)}
   */
  def createTempFileBlocking(dir: String, prefix: String, suffix: String, perms: String): String =
    unwrap.createTempFileBlocking(dir, prefix, suffix, perms)
}
object FileSystem {
  implicit def javaFileSystemToVerticesFileSystem(j: JavaFileSystem): FileSystem = apply(j)
  implicit def verticesFileSystemToJavaFileSystem(v: FileSystem): JavaFileSystem = v.unwrap


}
