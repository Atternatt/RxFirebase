package extensions

import io.reactivex.FlowableEmitter
import io.reactivex.MaybeEmitter

/**
 * Created by marc on 30/7/17.
 */

infix fun <E : FlowableEmitter<*>> E.canEmitt(block: E.() -> Unit) {
    if (this.isCancelled.not()) {
        block()
    }
}

infix fun <E : MaybeEmitter<*>> E.canEmitt(block: E.() -> Unit) {
    if (this.isDisposed.not()) {
        block()
    }
}
