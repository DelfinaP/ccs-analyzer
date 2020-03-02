
prop can_deadlock = 
  min Y = [-]ff \/ <->Y

prop can_send = 
  min Y = <send>tt \/ <i>Y

prop can_receive = 
  min Y = <receive>tt \/ <t>Y  

prop always_can_send_or_receive =
  max X = (min Y = <send>tt \/ <receive>tt \/ <->Y) /\ [-]X

* p |= may_diverge if p diverges{s} is true for some s in (Act - {i})*

prop may_diverge = 
  min X = (max Y = <i>Y) \/ <->X

prop no_repeat = 
  max X = ([send](not can_send) /\ 
           [receive](not can_receive) /\ 
           [-]X)

 