
* Milner states that the scheduler should have the following
* properties:
* 1) It must perform a1,a2,a3 cyclically starting with a1
* 2) It must perform ai and bi alternately for each i

* If cycle_broken is false and perform_a1_first is true
* then property 1 holds.

prop Break1 = <a1>(min X = <a1>tt \/ <a3>tt \/ <-a2>X)

prop Break2 = <a2>(min X = <a1>tt \/ <a2>tt \/ <-a3>X)

prop Break3 = <a3>(min X = <a2>tt \/ <a3>tt \/ <-a1>X)

prop Cycle_broken = 
  min Y = (Break1 \/ Break2 \/ Break3 \/ <->Y)

prop Perform_a1_first = 
  min X = (<a1>tt \/ <t>X)

* The following ensure property 2.
* Formula Repeat_ai is true if the system can perform two ai 
* actions without a bi action between them.  The scheduler should 
* never get to a state satisfying any of these formulas.

prop Repeat_a1 = <a1>(min X = <a1>tt \/ <-b1>X)

prop Repeat_a2 = <a2>(min X = <a2>tt \/ <-b2>X)

prop Repeat_a3 = <a3>(min X = <a3>tt \/ <-b3>X)

prop Repeat_reachable = 
  min Y = (Repeat_a1 \/ Repeat_a2 \/ Repeat_a3 \/ <->Y)

