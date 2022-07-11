package com.felipe.appExample.ui

interface ViewSnippet<I, O, P : ViewSnippet.Page<I, O>> {
    interface Page<I, O> {
        val ids: Set<I>?

        fun getIdAtPosition(index: Int): I?
        fun getObjectAtPosition(index: Int): O?
        fun getObjectById(id: I): O?
        fun getViewIndex(id: I): Int
        val itemsOnPage: Int
        val size: Int

        fun put(index: Int, id: I, `object`: O)
        fun moveId(fromPos: Int, toPos: Int, id: I, nextPageOrPrevious: Page<I, O>?)
    }

    fun getPage(numPage: Int): P?
    fun getIdAtViewIndex(index: Int): I?
    fun getIdViewIndex(id: I): Int
    fun getObjectAtViewIndex(index: Int): O?
    val size: Int

    fun reload()
    fun move(fromPos: Int, toPos: Int, id: I)
    val itemsOnPage: Int

    // TODO: Remove?!
    fun checkNeedLoad(position: Int, isAscending: Boolean)
    fun setBlacklist(blacklist: Array<I>?)

    // void removeIDs(int parentPos, I[] childsByType);
    fun loadPage(numPage: Int): P? //Page<I> getPage(int numPage, boolean isGoingTop);
}