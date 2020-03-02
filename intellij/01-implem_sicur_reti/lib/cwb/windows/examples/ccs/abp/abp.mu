

prop can_deadlock =
   min X = [-]ff \/ <->X

prop can_send = 
  min Y = <send>tt \/ <t>Y

prop can_receive = 
  min Y = <'receive>tt \/ <t>Y  

prop always_can_send_or_receive =
  AG (can_send \/ can_receive)

prop no_repeat = 
  AG ([send](not can_send) /\ ['receive](not can_receive))

prop no_repeat' =
  AG ([send] A([send]ff W <'receive>tt) /\ 
      ['receive] A(['receive]ff W <send>tt))

