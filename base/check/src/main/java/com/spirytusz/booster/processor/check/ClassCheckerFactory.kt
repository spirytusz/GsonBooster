package com.spirytusz.booster.processor.check

import com.google.auto.service.AutoService
import com.spirytusz.booster.processor.base.check.ClassChecker
import com.spirytusz.booster.processor.base.log.MessageLogger

@AutoService(ClassChecker.Factory::class)
internal class ClassCheckerFactory : ClassChecker.Factory {
    override fun create(logger: MessageLogger): ClassChecker {
        return ClassCheckerImpl(logger)
    }
}