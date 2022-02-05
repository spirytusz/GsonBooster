package com.spirytusz.booster.processor.scan.ksp.data

import com.google.devtools.ksp.symbol.KSNode

interface IKsNodeOwner {

    val target: KSNode?
}