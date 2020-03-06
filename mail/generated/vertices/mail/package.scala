package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.MailMessage
import io.vertx.ext.mail.MailResult
import java.lang.String

package object mail {
  implicit class VertxMailClientOps(val target: MailClient) extends AnyVal {
    /**
     *  send a single mail via MailClient
     * @param email         MailMessage object containing the mail text, from/to, attachments etc
     * @param resultHandler will be called when the operation is finished or it fails
     *                       (may be null to ignore the result)
     * @return this MailClient instance so the method can be used fluently
     */
    def sendMailL(email: MailMessage): Task[MailResult] =
      Task.handle[MailResult] { resultHandler =>
        target.sendMail(email, resultHandler)
      }
  }


}