package com.felipe.appExample.ui

class DefaultPage<I, O>(pageSize: Int, from: Int, ids: Array<I>?, items: Array<O>?) :
    ViewSnippet.Page<I, O> {
    override val itemsOnPage: Int
    private val mIndices: MutableMap<I, Int>
    private val mIdentifiers: MutableMap<Int, I?>
    private val mObjects: MutableMap<I, O>
    override val ids: Set<I> get() = mIndices.keys

    // private final Class<I> mIdClazz;
    init {
        var from = from
        require(!(pageSize < 1 || ids == null || items == null)) { "invalid arguments" }
        require(
            ids.size == items.size
        ) { "invalid array sizes" }
        itemsOnPage = pageSize
        mIndices = HashMap(itemsOnPage)
        mObjects = HashMap(itemsOnPage)
        mIdentifiers = HashMap(itemsOnPage)
        for (i in ids.indices) put(from++, ids[i], items[i])
    }

    override fun getIdAtPosition(index: Int): I? {
        return mIdentifiers[index]
    }

    override fun getObjectAtPosition(index: Int): O? {
        val id = mIdentifiers[index]
        return if (id == null) null else mObjects[id]
    }

    override fun getObjectById(id: I): O? {
        return mObjects[id]
    }

    override fun getViewIndex(id: I): Int {
        val index = mIndices[id]
        return index ?: -1
    }

    override val size: Int
        get() = mIndices.size

    override fun put(index: Int, id: I, `object`: O) {
        mIdentifiers[index] = id
        mIndices[id] = index
        mObjects[id] = `object`
    }

    override fun moveId(
        fromPos: Int,
        toPos: Int,
        id: I,
        nextPageOrPrevious: ViewSnippet.Page<I, O>?
    ) {
        var nextPageOrPrevious = nextPageOrPrevious
        if (nextPageOrPrevious === this) nextPageOrPrevious = null
        val copy: MutableMap<Int, I?> = HashMap()
        var from = -1
        for (value in mIndices.values)
            if (from == -1 || value <= from)
                from = value
        var accelerate = false
        val sizeFinal = mIndices.size + from
        for (i in from until sizeFinal) {
            if (i in fromPos..toPos) {
                if (!accelerate) {
                    var newId: I? = null
                    if (nextPageOrPrevious != null &&
                        nextPageOrPrevious.getIdAtPosition(toPos).also { newId = it } != null
                    ) {
                        copy[fromPos] = newId
                        accelerate = true
                        nextPageOrPrevious.moveId(fromPos, toPos, id, this)
                        continue
                    }
                }
                if (i == toPos) {
                    copy[toPos] = id
                    continue
                }
                val cId = mIdentifiers[i + 1]
                copy[i] = cId
                continue
            } else if (i in toPos..fromPos) {
                if (!accelerate) {
                    var newId: I? = null
                    if (nextPageOrPrevious != null && nextPageOrPrevious.getIdAtPosition(
                            toPos
                        ).also { newId = it } != null
                    ) {
                        copy[fromPos] = newId
                        accelerate = true
                        nextPageOrPrevious.moveId(fromPos, toPos, id, this)
                        continue
                    }
                }
                if (i == toPos) {
                    copy[toPos] = id
                    continue
                }
                val cId = mIdentifiers[i - 1]
                copy[i] = cId
                continue
            }
            copy[i] = mIdentifiers[i]
        }
        mIdentifiers.clear()
        mIndices.clear()
        for (integer in copy.keys) {
            val cId = copy[integer]
            mIdentifiers[integer] = cId
            mIndices[cId!!] = integer
        }
    }
}