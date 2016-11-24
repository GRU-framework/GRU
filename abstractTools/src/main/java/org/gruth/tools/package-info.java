/**
 * This package is for a "lightweight" use of
 * GRU libraries (and so should be delivered in a separate jar).
 *
 * The main idea is to let programmers define a
 * "single test unit" that may gather different assertions.
 * A "single test unit" should be something linked to the
 * test of a single purpose
 * (example: test a given constructor or method evocation)
 *
 * <P> so:
 * <UL>
 *     <LI> such a test unit should be uniquely  identified.
 *     it's up to the developer to decide which
 *     combination of data is unique using:
 *     name of test, class, name of
 *     code being invoked, identification of parameters ,...
 *     Though there is no code in GRU to manage those tests
 *     results in a database and to keep a history of runs
 *     (and compare with previous executions of the same tests)
 *     developers are encouraged to write this kind of utility code
 *     (and, may be, share with other users of the library).
 *
 *     <LI>
 *      The core class is {@link org.gruth.tools.SingleTestReport}.
 *      Most of the time  only one instance of this class should
 *      be created for each "single test unit" (using one of the
 *      factory methods). This class may gather information
 *      about the class, the invoked code name, current object,
 *      invocation arguments,
 *      various assertions
 *      (instances of class {@link org.gruth.tools.SimpleAssertion}),
 *      caught Exception,
 *      and any data that the developer see fit to report.
 *
 *      <LI>
 *       once an instance of <TT>SingleTestReport</TT>
 *       if filled then it should be published.
 *       (by default the last unpublished instance is
 *       published when  a new one is created).
 *       Publication is handled by one or more {@link org.gruth.tools.SingleTestReport.Reporter}.
 *       (see the corresponding javadoc to learn how to write and
 *       deploy such a service).
 *
 * </UL>
 * @author bamade
 */
// Date: 12/05/15

package org.gruth.tools;