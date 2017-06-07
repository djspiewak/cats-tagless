/*
 * Copyright 2017 Kailuo Wang
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

package mainecoon
package tests


import cats.laws.discipline.SerializableTests
import cats.laws.discipline.InvariantTests
import autoInvariantTests._
import cats.functor.Invariant
import cats.kernel.Eq
import org.scalacheck.{Arbitrary, Cogen}
import cats.laws.discipline.eq._

class autoInvariantTests extends MainecoonTestSuite {

  checkAll("SimpleAlg[Option]", InvariantTests[SimpleAlg].invariant[Float, String, Int])
  checkAll("Invariant[SimpleAlg]", SerializableTests.serializable(Invariant[SimpleAlg]))

  test("non effect method correctly delegated") {
    val doubleAlg = AlgWithNonEffectMethodFloat.imap(_.toDouble)(_.toFloat)
    doubleAlg.foo("big") should be(3d)
    doubleAlg.foo2("big") should be("gib")
    doubleAlg.foo3(3d) should be("3.0")
  }
}

object autoInvariantTests {
  @autoInvariant
  trait SimpleAlg[T] {
    def foo(a: T): T
  }

  @autoInvariant
  trait AlgWithNonEffectMethod[T] {
    def foo(a: String): T
    def foo2(a: String): String
    def foo3(a: T): String
  }

  object AlgWithNonEffectMethodFloat extends AlgWithNonEffectMethod[Float] {
    def foo(a: String): Float = a.length.toFloat
    def foo2(a: String): String = a.reverse
    def foo3(a: Float): String = a.toString
  }

  implicit def eqForSimpleAlg[T: Arbitrary](implicit eqT: Eq[T]): Eq[SimpleAlg[T]] =
    Eq.by[SimpleAlg[T], T => T] { p =>
      (s: T) => p.foo(s)
    }

  implicit def arbitrarySimpleAlg[T](implicit cS: Cogen[T],
                                     FI: Arbitrary[T]): Arbitrary[SimpleAlg[T]] =
    Arbitrary {
      for {
        f <- Arbitrary.arbitrary[T => T]
      } yield new SimpleAlg[T] {
        def foo(i: T): T = f(i)
      }
    }
}
