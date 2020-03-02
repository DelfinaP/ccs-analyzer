******************************************************************************
*
* Author:  Girish Bhat and Gerald Luettgen (with comments by Rance Cleaveland)
* Date:    13 June 2000
*
* This file contains mu-calculus formulations of the properties used in the
* paper by Bhat, Cleaveland, Luettgen and Sims titled "Modeling and verifying
* * distributed systems using priorities: A case study".  The paper appeared
* in Software Concepts and Tools in 1996 and is available on line at
* http://www.cs.sunysb.edu/~rance/publications/1996.html#sct96.  The paper
* uses the following derived constructs:
*
*   [L]^\infty Phi = max X = (Phi /\ [L]X)
*
* [L]^\infty Phi holds along all paths whose transition labels come from set
* L.
*
*   <L>^* Phi = min X = (Phi \/ <L>X)
*
* This asserts that Phi eventually holds along some path whose transition
* labels are drawn from L.
*
*   even(Phi) = min X =
*                   max Y =
*                       Phi \/ (['tick:4]X /\ [-'tick:4]Y)
*
* This is a "fair eventuality" that only requires Phi to hold of states on
* paths containing infinitely many tick actions.
*
*   again(Phi) = max X = ([recovered:0]Phi) /\ [-]X
*
* This property asserts that Phi holds after every occurrence of
* "recovered:0".
*
* It should be noted that the properties in the paper do not contain priority
* values; e.g. "recovered:0" here is rendered as "recovered" there.  It is
* also the case that the formulas have been hand-translated into the L2
* fragment of the mu-calculus, because when the case study was done the CWB-NC
* could not do these translations automatically.  The properties here differ
* slightly in other respects; the differences are noted.
*
* Property names coincide with those used in the paper.

* failures-responded
*
* This property asserts that after the low-grade link fails for the first
* time, the failure is eventually either repaired or detected (along paths
* where the clock advances).  The formulation is as follows.
*
*   [-'fail_overfull:0, 'fail_wire:2]^\infty
*       (
*           ['fail_overfull:0, 'fail_wire:2] even(<'det:0, 'repaired:2>tt)
*       )

prop failures_responded =
not ( min A1 = ( not ( ['fail_overfull:0,'fail_wire:2]
                     not ( max D = ( min E1 = ( not 
                                               ( <'det:0,'repaired:2>tt ) /\ 
                                              ( <'tick:4>D \/ <-'tick:4>E1 ) 
                                              )
                                    ) 
                          )
                    ) \/ <-'fail_overfull:0,'fail_wire:2>A1
              ) 
    )

* failures-responded-again
*
* This property asserts that failures-responded holds throughout the system
* execution.  It's encoding is:
*
*   again(failures_responded)

prop failures_responded_again = 
not ( min C = ( not ( ['recovered:0] 
                      
  not ( min A1 = ( not ( ['fail_overfull:0,'fail_wire:2]
                      not ( max D = ( min E1 = ( not (<'det:0,'repaired:2>tt)
                                                  /\ 
                                                (<'tick:4>D \/ <-'tick:4>E1) 
                                                )
                                      ) 
                            )
                      ) 
                  \/ <-'fail_overfull:0,'fail_wire:2,'recovered:0>A1 
                ) 
      )

                    ) 
                 \/ <-> C  
               )
    )

* can-tick asserts that the clock can always eventually advance.  Encoding:
*
*   [-]^\infty <->^* <'tick:4>tt

prop can_tick = 
  not ( min A1 = ( not ( 
                     min B = ( <'tick:4>tt \/ <->B )
                    )  
                \/ <->A1 
              ) 
    )

* failures-possible
*
* This property asserts that a failure is possible initially.  Encoding:
*
*   <-'recovered:0>^* <'fail_overfull:0, 'fail_wire:2>tt

prop failures_possible = 
  min B = ( <'fail_overfull:0,'fail_wire:2>tt \/ <-'recovered:0>B )

* failures-possible-again
*
* This property asserts that the previous one holds through system execution.
* Encoding:
*
*   again(failures_possible)

prop failures_possible_again =  
not ( min C = ( not ( 
                     ['recovered:0] ( min A1 = 
                                      ( <'fail_overfull:0,'fail_wire:2>tt \/ 
                                        <-'recovered:0>A1
                                      ) 
                                    )
                    ) 
                \/ <-> C 
              ) 
    )

* no-false-alarms
*
* This property asserts that a failure can be detected only after an
* occurrence of a failure.  The encoding below is:
*
*   [-'fail_overfull:0, 'fail_wire:2, 'recovered:0]^/infty
*       (['det:0]ff \/ <'fail_overfull:0>tt)

prop no_false_alarms = 
not ( min A1 = ( not ( ['det:0]ff \/ <'fail_overfull:0>tt ) \/ 
                <-'fail_overfull:0,'fail_wire:2,'recovered:0>A1 
              ) 

    )

* no-false-alarms-again
*
* This property asserts that no false alarms can be observed throughout
* system execution.  Encoding:
*
*   again(no_false_alarms)

prop no_false_alarms_again = 
not ( min C = ( not ( ['recovered:0] 
                      not ( min B = ( not ( ['det:0]ff \/ 
                                            <'fail_overfull:0>tt 
                                          ) 
                                      \/
                                      <-'fail_overfull:0,'fail_wire:2,'recovered:0>B 
                                    )
                          )
                    ) 
                 \/ <-> C  
               )
    )

* eventually-silent
*
* This property asserts that after the low-grade link fails, the system will
* eventually be silent, provided a recovery does not take place.  The encoding 
* is:
*
*   [-]^\infty ['det:0]
*       even ([-'recovered:0]^\infty ['comm_out:2, 'stat_out:2]ff)
*
* This paper differs significantly from the one in the paper, which is:
*
*   [-]^\infty ['det:0]
*       even(([-]^\infty ['comm_out:2, 'stat_out:2]ff) \/ <'recovered:0>tt)
*
* The former property in fact appears to capture the spirit of the property
* more than the latter.

prop eventually_silent =
not ( min A1 = ( not (
                     ['det:0] (
                               not ( max D = ( min E1 = ( not ( 
                                 not ( min F = ( not 
                                                (['comm_out:2,'stat_out:2]ff)
                                                 \/
                                                 <-'recovered:0>F 
                                               ) 
                                     )
                              )
                              /\ ( <'tick:4>D \/ <-'tick:4>E1 ) 
                              ) ) )
    
                              )                             
                    )  
                \/ <->A1
              ) 
    )

* react-on-repair
*
* This property asserts that if whenever a failure is detected and repaired,
* then the system will eventually be reinitialized.  Encoding:
*
*   [-]^\infty ['det:0]
*       [-]^\infty [repaired:0] even(<'recovered:0>tt)
*
* This differs slightly from the property given in the paper, which is:
*
*   [-]^\infty ['det:0]
*       [-'recovered:0]^\infty [repaired:0] even(<'recovered:0>tt)

prop react_on_repair = 
not ( min A1 = ( not (
                     ['det:0] 
( 

not ( min B = ( not 
( [repaired:2] 
not ( max D = ( min E1 = ( not ( <'recovered:0>tt ) /\ 
                          ( <'tick:4>D \/ <-'tick:4>E1 ) ) ) )
)
\/ <->B ) )


)
                    )  \/ <->A1 ) )

* no-deadlock

prop no_deadlock = not ( min A1 = ( [-]ff  \/ <->A1 ) )

