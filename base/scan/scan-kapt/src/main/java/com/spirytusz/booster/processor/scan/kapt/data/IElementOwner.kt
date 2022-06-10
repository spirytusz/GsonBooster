package com.spirytusz.booster.processor.scan.kapt.data

import javax.lang.model.element.Element

interface IElementOwner {

    val target: Element?
}