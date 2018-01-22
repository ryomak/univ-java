/*
 AIDemonProc.java
  すべての種類のデモン手続きのスーパークラス
*/

import java.util.*;

abstract
class AIDemonProc {

abstract
public
Object eval(
 AIFrameSystem inFrameSystem,
 AIFrame inFrame,
 String inSlotName,
 Enumeration inSlotValues,
 Object inOpts );

public
Object eval(
 AIFrameSystem inFrameSystem,
 AIFrame inFrame,
 String inSlotName,
 Enumeration inSlotValues )
{
 return eval( inFrameSystem, inFrame, inSlotName, inSlotValues, null );
}

}