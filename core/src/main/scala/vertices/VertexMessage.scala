/*
 * Copyright 2018 David Gregory and the Vertices project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
