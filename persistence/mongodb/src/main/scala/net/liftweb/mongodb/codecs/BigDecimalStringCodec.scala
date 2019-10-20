/*
 * Copyright 2019 WorldWide Conferencing, LLC
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

package net.liftweb.mongodb
package codecs

import scala.math.BigDecimal

import org.bson.{BsonReader, BsonWriter}
import org.bson.codecs._
import org.bson.types.Decimal128

/**
 * A Codec for BigDecimal instances that saves the value as a String.
 */
case class BigDecimalStringCodec() extends Codec[BigDecimal] {
  override def encode(writer: BsonWriter, value: BigDecimal, encoderContext: EncoderContext): Unit = {
    writer.writeString(value.toString)
  }

  override def decode(reader: BsonReader, decoderContext: DecoderContext): BigDecimal = {
    BigDecimal(reader.readString)
  }

  override def getEncoderClass(): Class[BigDecimal] = classOf[BigDecimal]
}