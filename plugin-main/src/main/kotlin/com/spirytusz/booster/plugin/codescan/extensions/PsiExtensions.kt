package com.spirytusz.booster.plugin.codescan.extensions

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor

inline fun <reified ChildType : PsiElement> PsiElement.getChildOfTypeAsList(
    recursiveSearch: Boolean = true
): List<ChildType> {
    val result = mutableListOf<ChildType>()
    acceptChildren(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is ChildType) {
                result.add(element)
            }
            if (recursiveSearch) {
                element.acceptChildren(this)
            }
        }
    })
    return result
}

inline fun <reified ParentType : PsiElement> PsiElement.getDirectParentOfType(
    recursiveSearch: Boolean = true
): ParentType? {
    val parent = this.parent
    if (parent is ParentType) {
        return parent
    }
    if (!recursiveSearch) {
        return null
    }
    var searchParent = parent
    while (searchParent != null && searchParent !is ParentType) {
        searchParent = searchParent.parent
    }
    return searchParent as? ParentType
}