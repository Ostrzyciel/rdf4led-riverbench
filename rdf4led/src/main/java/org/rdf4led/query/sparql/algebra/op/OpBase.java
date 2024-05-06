/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rdf4led.query.sparql.algebra.op;

import org.rdf4led.query.sparql.algebra.Op;

/**
 * OpBase.java
 *
 * <p>Modified from Jena
 *
 * <p>Author : Le Tuan Anh Contact: anh.le@deri.org anh.letuan@insight-centre.org
 *
 * <p>10 Sep 2015
 */
public abstract class OpBase<Node> implements Op<Node> {
  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equalTo(Op<Node> other);

  @Override
  public final boolean equals(Object other) {
    if (this == other) return true;

    if (!(other instanceof Op)) return false;

    return equalTo((Op<Node>) other);
  }

  static final int HashBasicGraphPattern = 0xB1;

  static final int HashGroup = 0xB2;

  static final int HashUnion = 0xB3;

  static final int HashLeftJoin = 0xB4;

  static final int HashDistinct = 0xB5;

  static final int HashReduced = 0xB5;

  static final int HashToList = 0xB6;

  static final int HashNull = 0xB7;

  static final int HashSequence = 0xB8;

  static final int HashLabel = 0xB9;

  static final int HashTriple = 0xBA;

  static final int HashQuad = 0xBB;
}