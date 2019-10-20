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

package net.liftweb
package mongodb
package record
package testmodels

import net.liftweb.common._
import net.liftweb.mongodb.record.field._

import com.mongodb._

case class TestCaseClass(a: String, b: Int)

class CaseClassTest private () extends MongoRecord[CaseClassTest] with ObjectIdPk[CaseClassTest] {

  def meta = CaseClassTest

  object caseclassfield extends CaseClassField[CaseClassTest, TestCaseClass](this)
}

object CaseClassTest extends CaseClassTest with MongoMetaRecord[CaseClassTest]
