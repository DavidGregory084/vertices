package vertices

import io.circe.{ Decoder, Encoder }
import io.circe.generic.semiauto
import io.circe.derivation

sealed trait Request[A] {
  def body: A
}

object Request {
  implicit def decoder[A: Decoder]: Decoder[Request[A]] = semiauto.deriveDecoder[Request[A]]
  implicit def encoder[A: Encoder]: Encoder[Request[A]] = semiauto.deriveEncoder[Request[A]]
}

case class RpcRequest[A](body: A) extends Request[A]

object RpcRequest {
  implicit def decoder[A: Decoder]: Decoder[RpcRequest[A]] = derivation.deriveDecoder[RpcRequest[A]]
  implicit def encoder[A: Encoder]: Encoder[RpcRequest[A]] = derivation.deriveEncoder[RpcRequest[A]]
}

case class ClientStreamRequest[A](body: A) extends Request[A]

object ClientStreamRequest {
  implicit def decoder[A: Decoder]: Decoder[ClientStreamRequest[A]] = derivation.deriveDecoder[ClientStreamRequest[A]]
  implicit def encoder[A: Encoder]: Encoder[ClientStreamRequest[A]] = derivation.deriveEncoder[ClientStreamRequest[A]]
}

case class ServerStreamRequest[A](body: A, streamAddress: String) extends Request[A]

object ServerStreamRequest {
  implicit def decoder[A: Decoder]: Decoder[ServerStreamRequest[A]] = derivation.deriveDecoder[ServerStreamRequest[A]]
  implicit def encoder[A: Encoder]: Encoder[ServerStreamRequest[A]] = derivation.deriveEncoder[ServerStreamRequest[A]]
}

case class BidiStreamRequest[A](body: A, streamAddress: String) extends Request[A]

object BidiStreamRequest {
  implicit def decoder[A: Decoder]: Decoder[BidiStreamRequest[A]] = derivation.deriveDecoder[BidiStreamRequest[A]]
  implicit def encoder[A: Encoder]: Encoder[BidiStreamRequest[A]] = derivation.deriveEncoder[BidiStreamRequest[A]]
}
