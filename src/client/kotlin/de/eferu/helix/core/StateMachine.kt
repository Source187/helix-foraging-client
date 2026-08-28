package de.eferu.helix.core

import de.eferu.helix.HelixClient

abstract class StateMachine<S : Enum<S>>(
  protected val name: String,
  initial: S,
) {
    var state: S = initial
        protected set

    private var entered = false

    fun tick() {
        if (!entered) {
            enter(state)
            entered = true
        }
        update(state)
    }

    fun transition(to: S) {
        if (to == state) return
        debug("transition $state -> $to")
        exit(state)
        state = to
        enter(state)
    }

    protected fun debug(message: String) {
        if (de.eferu.helix.modules.DebugModule.debugEnabled) {
            HelixClient.logger.debug("[$name] $message")
        }
    }

    protected abstract fun enter(state: S)
    protected abstract fun update(state: S)
    protected abstract fun exit(state: S)
}
