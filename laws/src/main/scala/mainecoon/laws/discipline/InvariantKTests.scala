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
package laws
package discipline

import cats.{Eq, ~>}
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Prop}
import org.typelevel.discipline.Laws
import cats.laws.discipline._

trait InvariantKTests[F[_[_]]] extends Laws {
  def laws: InvariantKLaws[F]

  def invariantK[A[_], B[_], C[_]](implicit
                                                          ArbFA: Arbitrary[F[A]],
                                                          ArbAB: Arbitrary[A ~> B],
                                                          ArbBA: Arbitrary[B ~> A],
                                                          ArbBC: Arbitrary[B ~> C],
                                                          ArbCB: Arbitrary[C ~> B],
                                                          EqFA: Eq[F[A]],
                                                          EqFC: Eq[F[C]]
                                                         ): RuleSet = {
    new DefaultRuleSet(
      name = "invariantK",
      parent = None,
      "invariant identity" -> forAll(laws.invariantIdentity[A] _),
      "invariant composition" -> forAll(laws.invariantComposition[A, B, C] _))
  }
}

object InvariantTests {
  def apply[F[_[_]]: InvariantK]: InvariantKTests[F] =
    new InvariantKTests[F] { def laws: InvariantKLaws[F] = InvariantKLaws[F] }
}