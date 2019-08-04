package com.catcher.base.exception

open class BusinessException(msg: String) : RuntimeException(msg)

class ExecutionFailedException(msg: String) : BusinessException(msg)