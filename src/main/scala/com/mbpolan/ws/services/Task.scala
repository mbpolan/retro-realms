package com.mbpolan.ws.services

/** High-level abstraction for a unit of work that can be scheduled.
  *
  * @author Mike Polan
  */
trait Task {

  /** Whether or not the task was canceled. */
  private var canceled = false

  /** Cancels the task. */
  def cancel(): Unit = canceled = true

  /** Executes the task if it has not already been canceled. */
  def run(): Unit = {
    if (!canceled) doRun()
  }

  /** Performs the work of the task. */
  protected def doRun(): Unit
}
