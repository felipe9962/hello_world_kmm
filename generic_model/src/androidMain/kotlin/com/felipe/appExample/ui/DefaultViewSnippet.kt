package com.felipe.appExample.ui

import java.util.LinkedHashMap

abstract class DefaultViewSnippet<I, O, P : ViewSnippet.Page<I, O>>(
    val maxSize: Int,
    val pageMaxSize: Int
) : ViewSnippet<I, O, P> {
    protected var MAX_SIZE: Int = 0
    override val itemsOnPage: Int
    override var size = 0
    private var OFFSET = 0
    private var blackList: Array<I>? = null
    protected val PAGES: MutableMap<Int, P?> = object : LinkedHashMap<Int, P?>(
        0,
        0.75f,
        true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, P?>): Boolean {
            return size > MAX_SIZE
        }

        override operator fun get(key: Int): P? {
            val page = super.get(key)
            return page ?: loadPage(key)
        }
    }

    init {
        require(!(pageMaxSize < 0 || maxSize < 0)) { "invalid size" }
        itemsOnPage = pageMaxSize
        MAX_SIZE = maxSize
        OFFSET = 0
    }

    fun init(): DefaultViewSnippet<I, O, P> {
        loadPage(0)
        loadPage(1)
        size = countFromDataSource
        return this
    }

    override fun getIdViewIndex(id: I): Int {
        var pos = -1
        for (value in PAGES.values) {
            if (value == null) continue
            pos = value.getViewIndex(id)
            if (pos != -1) {
                pos += OFFSET
                break
            }
        }
        return pos
    }

    override fun getIdAtViewIndex(index: Int): I? {
        if (index < 0) return null
        val p = PAGES[index / itemsOnPage] ?: return null
        return p.getIdAtPosition(index)
    }

    override fun getPage(pageNum: Int): P? {
        val p = PAGES[pageNum]
        return p ?: loadPage(pageNum)
    }

    override fun move(fromPos: Int, toPos: Int, id: I) {
        val p = PAGES[fromPos / itemsOnPage] ?: return
        val page2 = PAGES[toPos / itemsOnPage]
        p.moveId(fromPos, toPos, id, page2)
    }

    override fun checkNeedLoad(position: Int, isAscending: Boolean) {
        if (position % (MAX_SIZE / 2) != 0) return
        if (!isAscending && getIdAtViewIndex(position + MAX_SIZE - 1) == null) loadPage(position / MAX_SIZE + 1) else if (isAscending && getIdAtViewIndex(
                Math.abs(position - MAX_SIZE)
            ) == null
        ) {
            loadPage(position / MAX_SIZE - 1)
        }
    }

    override fun reload() {
        println("RELOADING VIEW SNIPPET")
        var ks = PAGES.keys.toTypedArray()
        PAGES.clear()
        if (ks.size == 0) ks = arrayOf(0)
        for (page in ks) {
            val p: P? = loadPage(page)
            PAGES[page] = p
        }
        size = countFromDataSource
    }

    protected abstract val countFromDataSource: Int

    override fun setBlacklist(blacklist: Array<I>?) {
        blackList = blacklist
        reload()
    }

    override fun getObjectAtViewIndex(index: Int): O? {
        var obj: O? = null
        for (value in PAGES.values) {
            obj = value!!.getObjectAtPosition(index)
            if (obj != null) {
                break
            }
        }
        return obj
    }
}