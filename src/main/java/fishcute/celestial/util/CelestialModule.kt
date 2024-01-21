package fishcute.celestial.util

import celestialexpressions.*
import celestialexpressions.Function

val module = Module("celestial",
    VariableList(hashMapOf(
        "a" to {1.0}
    )),
    FunctionList(hashMapOf(
        "print" to Function({arr -> Util.print(arr[0])}, 1)
    ))
);