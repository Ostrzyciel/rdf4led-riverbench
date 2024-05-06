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

package org.rdf4led.query.expr.aggs;

import org.rdf4led.query.expr.Expr;
import org.rdf4led.query.expr.aggs.acc.Acc;
import org.rdf4led.common.mapping.Mapping;

/**
 * The null aggregate (which can't be written in SPARQL) calculates nothering but does help remember
 * the group key
 */
public class AggNull<Node> extends AggregatorBase<Node> {
  public AggNull() {}

  @Override
  public Aggregator<Node> copy(Expr<Node> expr) {
    return this;
  }

  @Override
  public String toString() {
    return "aggnull()";
  }

  @Override
  public Acc<Node> createAccumulator() {
    return createAccNull();
  }

  @Override
  public Node getValueEmpty() {
    return null;
  }

  @Override
  public Expr<Node> getExpr() {
    return null;
  }

  @Override
  public int hashCode() {
    return HC_AggNull;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;

    return (other instanceof AggNull);
  }

  public Acc<Node> createAccNull() {
    return new AccNull();
  }

  // ---- Accumulator
  private class AccNull implements Acc<Node> {
    private int nBindings = 0;

    public AccNull() {}

    @Override
    public Node getValue() {
      return null;
    }

    @Override
    public boolean updateArrival(Mapping<Node> mapping) {
      nBindings++;

      return true;
    }

    @Override
    public boolean updateExpiry(Mapping<Node> mapping) {
      nBindings--;

      return true;
    }
  }
}
