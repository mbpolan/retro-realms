package com.mbpolan.ws.services

import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service

/** Service that provides task scheduling and execution.
  *
  * @author Mike Polan
  */
@Service
class Scheduler {

  @Autowired
  private var taskScheduler: TaskScheduler = _

  /** Submits a task for execution at a later time.
    *
    * @param f The function to execute.
    * @param delay The amount of milliseconds to wait starting from now until execution.
    * @return A [[Task]] that's been allocated for this work.
    */
  def schedule(f: () => Unit, delay: Long = 0L): Task = {
    val task = new Task() {
      override protected def doRun(): Unit = f()
    }

    taskScheduler.schedule(new Runnable() {
      override def run(): Unit = task.run()

    }, Date.from(LocalDateTime.now().plus(delay, ChronoUnit.MILLIS)
      .atZone(ZoneId.systemDefault()).toInstant))

    task
  }

  /** Schedules a task to be run at fixed periods of time.
    *
    * @param f The function to execute.
    * @param rate The amount of milliseconds between task executions.
    */
  def scheduleFixedRate(f: () => Unit, rate: Long = 0L): Unit = {
    taskScheduler.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = f()
    }, rate)
  }
}
