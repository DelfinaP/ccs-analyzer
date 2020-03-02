
prop can_send = 
  min Y = <send>tt \/ <t>Y

prop can_receive = 
  min Y = <'receive>tt \/ <t>Y  

prop always_can_send_or_receive =
  max X = (can_send \/ can_receive) /\ [-]X

prop no_repeat = 
  max X = ([send](not can_send) /\ 
           ['receive](not can_receive) /\ 
           [-]X)

prop can_deadlock = 
  min X = [-]ff \/ <->X