package com.juliasoft.sensors.formatter

trait ResultFormatter[A]:

  def format(): A
