
prop can_deadlock =
  min Y = [-]ff \/ <->Y

prop mutual_exclusion_violation = 
  min X = <p1_enter_cs>(min Y = <p2_enter_cs>tt \/ <-p1_exit_cs>Y) \/
          <p2_enter_cs>(min Z = <p1_enter_cs>tt \/ <-p2_exit_cs>Z) \/
          <-p1_enter_cs,p2_enter_cs>X


