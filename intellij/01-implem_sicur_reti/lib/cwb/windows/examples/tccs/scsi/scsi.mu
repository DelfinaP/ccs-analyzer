******************************************************************************
*
* Author:  Rance Cleaveland (with help from Girish Bhat and Gerald Luettgen)
* Date:    13 June 2000
*
* Properties for SCSI model rendered in mu-calculus.  The properties are
* based on those in the paper "A Practical Approach to Implementing
* Real-Time Semantics".  Property numbers correspond to numbers given in the
* paper, which can be found on-line at
*         www.cs.sunysb.edu/~rance/publications/1999.html.
* That paper used the following templates.
*
* fair-follows(alpha, beta, L, Phi) =
*    max X = (
*      [alpha](
*        max Y = (
*          min Z = (
*             Phi
*          /\ [beta]X
*          /\ [L]Y
*          /\ [-beta,L]Z
*          )
*        )
*      )
*      /\ [-alpha]X
*    )
*
* between(alpha, beta, Phi) =
*    max X = (
*      [alpha](
*        max Y = (
*           Phi
*        /\ [beta]X
*        /\ [-beta]Y
*        )
*      )
*      /\ [-alpha]X
*    )
*
* In a couple of cases the formulas below differ from those given in the
* paper.  The discrepancies are due to slight changes made to the models after 
* the paper was written (and to a typo in the paper).  The differences are
* noted.

* The first property asserts that every phase of the bus protocol is always
* reachable.

prop Property1 =
    max X = (
       (min Y1 = <@begin_MsgIn  >tt \/ <->Y1)
    /\ (min Y2 = <@begin_MsgOut >tt \/ <->Y2)
    /\ (min Y3 = <@begin_DataIn >tt \/ <->Y3)
    /\ (min Y4 = <@begin_DataOut>tt \/ <->Y4)
    /\ (min Y5 = <@begin_Command>tt \/ <->Y5)
    /\ (min Y6 = <@begin_Status >tt \/ <->Y6)
    /\ [-]X
    )

* The second property asserts that when a phase is begun, it eventually ends,
* provided the system does not engage in an infinite sequence of @obs_setATN
* and @obsplace actions.
*
* The formula here corresponds to
*
*   fair-follows(
*       @begin_Phase,
*       @end_Phase,
*       {@obs_setATN,@obsplace},
*       [@begin_Phase]ff)
*
* The one in the paper is
*
*   fair-follows(
*       @begin_Phase,
*       @end_Phase,
*       {@obs_setATN},
*       <->tt)
*
* The <->tt in the paper appears to be a typo.

prop Property2 =
    max X = (
      [@begin_Phase](
        max Y = (
          min Z = (
             [@begin_Phase]ff
          /\ [@end_Phase]X
          /\ [@obs_setATN,@obsplace]Y
          /\ [-@end_Phase,@obs_setATN,@obsplace]Z
          )
        )
      )
      /\ [-@begin_Phase]X
    )

* The next property asserts that the signals REQ and ACK do not change
* betweeen information transfer phases.  This is encoded as:
*
*   between(
*       @end_Phase,
*       @begin_Phase,
*       [@obs_setREQ, @obs_relREQ, @obs_setACK, @obs_relACK]ff)
*
* This is the same as in the paper.

prop Property3 =
    max X = (
      [@end_Phase](
        max Y = (
           [@obs_setREQ, @obs_relREQ, @obs_setACK, @obs_relACK]ff
        /\ [@begin_Phase]X
        /\ [-@begin_Phase]Y
        )
      )
      /\ [-@end_Phase]X
    )


* The next property asserts that signal BSY is on and signal SEL off during an 
* information transfer phase.  It is encoded as:
*
*   between(
*       @begin_Phase,
*       @end_Phase,
*       [@obs_setBSY, @obs_relBSY, @obs_setSEL, @obs_relSEL]ff)
*
* This is the same as in the paper.

prop Property4 =
    max X = (
      [@begin_Phase](
        max Y = (
           [@obs_setBSY, @obs_relBSY, @obs_setSEL, @obs_relSEL]ff
        /\ [@end_Phase]X
        /\ [-@end_Phase]Y
        )
      )
      /\ [-@begin_Phase]X
    )

* This property asserts that whenever a device sends ("places") a message, it
* is read before another message is placed, provided the ATN signal is not
* continually set or the noid message continually observed.  It is encoded as
*
*   fair-follows(
*       @obsplace,
*       @obsread,
*       {@obs_setATN},
*       [@obs_place]ff)
*
* This is the same as the formula used in the paper.

prop Property5 =
    max X = (
      [@obs_place](
        max Y = (
          min Z = (
             [@obs_place]ff
          /\ [@obs_read]X
          /\ [@obs_setATN]Y
          /\ [-@obs_read,@obs_setATN]Z
          )
        )
      )
      /\ [-@obs_place]X
    )

* This property asserts that whenever the ATN signal is set, the protocol
* eventually either enters the MessageOut phase or ends a phase, provided the
* ATN signal is not continually set or a message continually sent.  The
* formula is encoded as follows.
*
*   fair-follows(
*       @obs_setATN,
*       {@begin_MsgOut, @end_Phase},
*       {@obs_setATN, @obsplace},
*       tt)
*
* This formula requires beta to be a set of actions, rather than a single
* action, as is reported in the paper.  It is substantially different from the
* one reported in the paper, which is
*
*   fair-follows(
*       @obs_setATN,
*       @begin_MsgOut,
*       {},
*       [obs_setATN]ff)
*
* The one in the paper appears to have been checked against an older version
* of the protocol.

prop Property6 =
    max X = (
      [@obs_setATN](
        max Y = (
          min Z = (
             [@begin_MsgOut,@end_Phase]X
          /\ [@obs_setATN,@obsplace]Y
          /\ [-@obs_setATN,@begin_MsgOut,@obsplace,@end_Phase]Z
          )
        )
      )
      /\ [-@obs_setATN]X
    )

* This is the standard deadlock formula:  some transition is possible in every 
* reachable state.

prop NoDeadlock =
    max X = (<->tt /\ [-]X)
