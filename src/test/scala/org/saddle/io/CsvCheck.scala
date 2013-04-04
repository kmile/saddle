/**
 * Copyright (c) 2013 Saddle Development Team
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
 **/

package org.saddle.io

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification
import java.io._
import org.saddle.{na, Index, Vec, Frame}

class CsvCheck extends Specification with ScalaCheck {
  "csv string parsing works" in {
    val data =
      """a,"b,c,d",e
        |1,2,3
        |4,5,6,
        |7, "8",    "9",   """.stripMargin

    val buf = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes("UTF-8"))))

    val src = new CsvSource {
      def reset() {}
      def readLine = buf.readLine()
    }

    val frame = CsvParser.parse(identity)(src).toFrame
    val expect = Frame(Vec("1","4","7"), Vec("2", "5", "8"), Vec("3", "6", "9")).setColIndex(Index("a", "b,c,d","e"))

    frame must_== expect
  }

  "csv int parsing works" in {
    val data =
      """a,"b,c,d",e
        |1,2,3
        |4,5,"test",
        |7, "8",    "9",   """.stripMargin

    val buf = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes("UTF-8"))))

    val src = new CsvSource {
      def reset() {}
      def readLine = buf.readLine()
    }

    val frame = CsvParser.parse(CsvParser.parseInt)(src).toFrame
    val expect = Frame(Vec(1, 4, 7), Vec(2, 5, 8), Vec(3, na.to[Int], 9)).setColIndex(Index("a", "b,c,d","e"))

    frame must_== expect
  }

  "csv fails on irregular row" in {
    val data =
      """a,"b,c,d",e
        |1,2,3
        |4,5,
        |7, "8",    "9",   """.stripMargin

    val buf = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes("UTF-8"))))

    val src = new CsvSource {
      def reset() {}
      def readLine = buf.readLine()
    }

    CsvParser.parse(CsvParser.parseInt)(src).toFrame must throwAn[ArrayIndexOutOfBoundsException]
  }
}