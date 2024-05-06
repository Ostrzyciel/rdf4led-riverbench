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

package org.rdf4led.query.path;


import org.rdf4led.common.Transform;
import org.rdf4led.common.iterator.Iter;
import org.rdf4led.graph.Graph;
import org.rdf4led.graph.Triple;
import org.rdf4led.query.QueryContext;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class PathEngine<Node> {
  private final boolean doingRDFSmember;
  private final boolean doingListMember;

  private final Graph<Node> graph;

  private QueryContext<Node> queryContext;

  public Node ANY;

  protected PathEngine(Graph graph, QueryContext<Node> queryContext) {
    boolean doingRDFSmember$ = false;
    boolean doingListMember$ = false;

    this.doingRDFSmember = doingRDFSmember$;

    this.doingListMember = doingListMember$;

    this.graph = graph;

    this.queryContext = queryContext;

    ANY = queryContext.dictionary().getNodeAny();
  }

  protected final Iter<Node> eval(Path<Node> path, Node node) {
    return PathEval.eval$(graph, node, path, this);
  }

  protected final void eval(Path<Node> path, Node node, Collection<Node> output) {
    PathEval.eval$(graph, node, path, this, output);
  }

  // protected final void eval(Path path, Node node, Collection<Node> output)

  protected abstract void flipDirection();

  protected abstract boolean direction();

  protected abstract Collection<Node> collector();

  // protected abstract void doZero(Path pathStep, Node node, Collection<Node>
  // output) ;
  // protected abstract void doOne(Path pathStep, Node node, Collection<Node>
  // output) ;

  // --- Where we touch the graph
  // Because for SP? or ?PO, no duplicates occur, so works for both strategies.
  protected final Iterator<Node> doOne(Node node, Node property) {
    Iterator<Node> iter2 = null;

    if (direction()) {
      Iter<Triple<Node>> iter1 =
          Iter.iter(graphFind(node, property, queryContext.dictionary().getNodeAny()));

      iter2 = iter1.map(selectObject);
    } else {
      Iter<Triple<Node>> iter1 = Iter.iter(graphFind(ANY, property, node));

      iter2 = iter1.map(selectSubject);
    }

    return iter2;
  }

  protected abstract void doSeq(
      Path<Node> pathStepLeft, Path<Node> pathStepRight, Node node, Collection<Node> output);

  protected abstract void doAlt(
      Path<Node> pathStepLeft, Path<Node> pathStepRight, Node node, Collection<Node> output);

  // path*
  protected abstract void doZeroOrMore(Path<Node> pathStep, Node node, Collection<Node> output);

  // path+
  protected abstract void doOneOrMore(Path<Node> pathStep, Node node, Collection<Node> output);

  // path?
  protected abstract void doZeroOrOne(Path<Node> pathStep, Node node, Collection<Node> output);

  protected abstract void doNegatedPropertySet(
      P_NegPropSet<Node> pathNotOneOf, Node node, Collection<Node> output);

  // path{*} : default implementation
  protected void doZeroOrMoreN(Path<Node> pathStep, Node node, Collection<Node> output) {
    doZeroOrMore(pathStep, node, output);
  }

  // path{+} : default implementation
  protected void doOneOrMoreN(Path<Node> pathStep, Node node, Collection<Node> output) {
    doOneOrMore(pathStep, node, output);
  }

  protected abstract void doZero(Path<Node> path, Node node, Collection<Node> output);

  // {N,M} and variations

  protected abstract void doFixedLengthPath(
      Path<Node> pathStep, Node node, long fixedLength, Collection<Node> output);

  protected abstract void doMultiLengthPath(
      Path<Node> pathStep, Node node, long min, long max, Collection<Node> output);

  protected final void fill(Iterator<Node> iter, Collection<Node> output) {
    for (; iter.hasNext(); ) output.add(iter.next());
  }

  protected static long dec(long x) {
    return (x <= 0) ? x : x - 1;
  }

  protected Transform<Triple<Node>, Node> selectSubject =
      new Transform<Triple<Node>, Node>() {
        @Override
        public Node convert(Triple<Node> triple) {
          return triple.getSubject();
        }
      };

  protected Transform<Triple<Node>, Node> selectPredicate =
      new Transform<Triple<Node>, Node>() {
        @Override
        public Node convert(Triple<Node> triple) {
          return triple.getPredicate();
        }
      };

  protected Transform<Triple<Node>, Node> selectObject =
      new Transform<Triple<Node>, Node>() {
        @Override
        public Node convert(Triple<Node> triple) {
          return triple.getObject();
        }
      };

  protected Iterator<Node> stepExcludeForwards(Node node, List<Node> excludedNodes) {
    Iter<Triple<Node>> iter1 = forwardLinks(node, excludedNodes);

    Iter<Node> r1 = iter1.map(selectObject);

    return r1;
  }

  protected Iterator<Node> stepExcludeBackwards(Node node, List<Node> excludedNodes) {
    Iter<Triple<Node>> iter1 = backwardLinks(node, excludedNodes);
    Iter<Node> r1 = iter1.map(selectSubject);
    return r1;
  }

  protected Iter<Triple<Node>> forwardLinks(Node x, Collection<Node> excludeProperties) {
    Iter<Triple<Node>> iter1 = Iter.iter(graphFind(x, ANY, ANY));

    if (excludeProperties != null) {
      iter1 = iter1.filter(new PathEvaluator.FilterExclude(excludeProperties));
    }

    return iter1;
  }

  protected Iter<Triple<Node>> backwardLinks(Node x, Collection<Node> excludeProperties) {
    Iter<Triple<Node>> iter1 = Iter.iter(graphFind(ANY, ANY, x));

    if (excludeProperties != null) {
      iter1 = iter1.filter(new PathEvaluator.FilterExclude(excludeProperties));
    }

    return iter1;
  }

  protected Iterator<Triple<Node>> graphFind(Node s, Node p, Node o) {
    return graphFind(graph, s, p, o);
  }

  //    private static Binding binding = BindingFactory.binding() ;
  //    private static Node RDFSmember = RDFS.Nodes.member ;
  //    private static Node ListMember = ListPFunction.nListMember ;

  private /*static*/ Iterator<Triple<Node>> graphFind(Graph graph, Node s, Node p, Node o) {
    // This is the only place this is called.
    // It means we can add property functions here.

    // Fast-path common cases.
    //        if ( doingRDFSmember && RDFSmember.equals(p) )
    //        {
    //            return GraphContainerUtils.rdfsMember(graph, s, o) ;
    //        }

    //        if ( doingListMember && ListMember.equals(p) )
    //        {
    //            return GraphList.listMember(graph, s, o) ;
    //        }
    // Potentially just allow the cases above.
    // return graph.find(s, p, o) ;
    return graphFind2(graph, s, p, o, queryContext);
  }

  /* As general as possible property function inclusion */
  private Iterator<Triple<Node>> graphFind2(
      Graph<Node> graph, Node s, Node p, Node o, QueryContext<Node> queryContext) {
    // Not all property functions make sense in property path
    // For example, ones taking list arguments only make sense at
    // the start or finish, and then only in simple paths
    // (e.g. ?x .../propertyFunction ?z)
    // which would have been packaged by the optimizer.
    //        if ( p != null && p.isURI() && registry != null )
    //        {
    //            PropertyFunctionFactory f = registry.get(p.getURI()) ;
    //
    //            if ( f != null )
    //                return graphFindWorker(graph, s, f, p, o, context) ;
    //        }

    return graph.find(s, p, o);
  }

  //    private Iterator<Triple> graphFindWorker(Graph graph, Node s, PropertyFunctionFactory f,
  // Node p, Node o, QueryContext<Node> queryContext)
  //    {
  //        // Expensive?
  //        PropertyFunction pf = f.create(p.getURI()) ;
  //
  //        PropFuncArg sv = arg(s, "S") ;
  //
  //        PropFuncArg ov = arg(o, "O") ;
  //
  //        QueryIterator r = QueryIterRoot.create(new ExecutionContext(context, graph, null, null))
  // ;
  //
  //        QueryIterator qIter = pf.exec(r, sv, p, ov, new ExecutionContext(ARQ.getContext(),
  // graph, null, null)) ;
  //
  //        if ( ! qIter.hasNext() )
  //        {
  //            return Iter.nullIterator() ;
  //        }
  //
  //        List<Triple> array = new ArrayList<>() ;
  //
  //        for ( ; qIter.hasNext() ; )
  //        {
  //            Binding b = qIter.next() ;
  //            Node st = value(sv, b) ;
  //            Node ot = value(ov, b) ;
  //            array.add(Triple.create(st, p, ot)) ;
  //        }
  //        // Materialise so the inner QueryIterators are used up.
  //        return array.iterator() ;
  //    }

  //    private static PropFuncArg arg(Node x, String name)
  //    {
  //        if ( x == null || ANY.equals(x) )
  //        { return new PropFuncArg(Var.alloc(name)) ; }
  //        return new PropFuncArg(x) ;
  //    }

  //    private static Node value(PropFuncArg arg, Binding b) {
  //        Node x = arg.getArg() ;
  //        if ( !Var.isVar(x) )
  //            return x ;
  //        return b.get(Var.alloc(x)) ;
  //    }
}
