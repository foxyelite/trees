package RedBlackTree

import Tree

class RedBlackTree<K : Comparable<K>, V> : Tree<K, V>, Iterable<Pair<K, V>> {

    var root: Node<K, V>? = null

    private fun findNode(key: K, cur: Node<K, V>? = root): Node<K, V>? = when {

        cur == null || key == cur.key -> cur
        key < cur.key -> findNode(key, cur.left)
        else -> findNode(key, cur.right)

    }

    override fun find(key: K): Pair<K, V>? {

        val result = findNode(key) ?: return null
        return Pair(result.key, result.value)

    }

    override fun insert(key: K, value: V) {

        var parent: Node<K, V>? = null
        var cur: Node<K, V>? = root

        while (cur != null) {
            parent = cur
            when {
                key < cur.key -> cur = cur.left
                key > cur.key -> cur = cur.right
                key == cur.key -> {
                    cur.value = value
                    return
                }
            }
        }

        if (parent == null) {
            root = Node(key, value, parent, true)
            return
        }

        if (key < parent.key) {
            parent.left = Node(key, value, parent, false)
            fixInsert(parent.left)
        } else {
            parent.right = Node(key, value, parent, false)
            fixInsert(parent.right)
        }

    }

    private fun fixInsert(node: Node<K, V>?) {

        var uncle: Node<K, V>?
        var cur: Node<K, V>? = node

        while (cur?.parent?.isBlack == false) {
            if (cur.parent == cur.parent?.parent?.left) {
                uncle = cur.uncle()
                when {
                    uncle?.isBlack == false -> {
                        cur.parent?.isBlack = true
                        uncle.isBlack = true
                        cur.parent?.parent?.isBlack = false
                        cur = cur.parent?.parent
                    }
                    cur == cur.parent?.right -> {
                        cur = cur.parent
                        if (cur!!.parent?.parent == null) {
                            root = cur.parent
                        }
                        cur.leftRotate()
                    }
                    cur == cur.parent?.left -> {
                        if (cur.parent?.parent?.parent == null) {
                            root = cur.parent
                        }
                        cur.parent?.parent?.rightRotate()
                    }
                }
            } else {
                uncle = cur.uncle()
                when {
                    uncle?.isBlack == false -> {
                        cur.parent?.isBlack = true
                        uncle.isBlack = true
                        cur.parent?.parent?.isBlack = false
                        cur = cur.parent?.parent
                    }
                    cur == cur.parent?.left -> {
                        cur = cur.parent
                        if (cur!!.parent?.parent == null) {
                            root = cur.parent
                        }
                        cur.rightRotate()
                    }
                    cur == cur.parent?.right -> {
                        if (cur.parent?.parent?.parent == null) {
                            root = cur.parent
                        }
                        cur.parent?.parent?.leftRotate()
                    }
                }
            }
        }

        root?.isBlack = true

    }

    override fun delete(key: K) {

        val node = findNode(key) ?: return
        deleteNode(node)

    }

    private fun deleteNode(node: Node<K, V>) {

        when {
            (node.right != null && node.left != null) -> { // Case Node have two non-null children
                val next = min(node.right)
                node.key = next!!.key
                node.value = next.value
                deleteNode(next)
                return
            }
            (node == root && node.isLeaf()) -> { // Case Node is Root & Leaf
                root = null
                return
            }
            (!node.isBlack && node.isLeaf()) -> { // Case Node is Red & Leaf
                if (node == node.parent!!.left)
                    node.parent!!.left = null
                else
                    node.parent!!.right = null
                return
            }
            (node.isBlack && node.left != null && !node.left!!.isBlack) -> { // Node have left non-null child
                node.key = node.left!!.key
                node.value = node.left!!.value
                node.left = null
                return
            }
            (node.isBlack && node.right != null && !node.right!!.isBlack) -> { // Node have right non-null child
                node.key = node.right!!.key
                node.value = node.right!!.value
                node.right = null
                return
            }
            else -> {
                deleteCase1(node)
            }
        }

        if (node == node.parent!!.left) {
            node.parent!!.left = null
        } else {
            node.parent!!.right = null
        }

    }

    private fun deleteCase1(node: Node<K, V>) {

        if (node.parent != null) {
            deleteCase2(node)
        }

    }

    private fun deleteCase2(node: Node<K, V>) {

        val sibling = node.sibling()

        if (!sibling!!.isBlack) { // Sibling is Red
            if (node == node.parent!!.left) {
                node.parent!!.leftRotate()
            } else if (node == node.parent!!.right) {
                node.parent!!.rightRotate()
            }
            if (root == node.parent) {
                root = node.parent!!.parent
            }
        }

        deleteCase3(node)

    }

    private fun deleteCase3(node: Node<K, V>) {

        val sibling = node.sibling()

        val a: Boolean = sibling!!.left == null || sibling.left!!.isBlack // Left Child of sibling is Black?
        val b: Boolean = sibling.right == null || sibling.right!!.isBlack // Right Child of sibling is Black?

        if (a && b && sibling.isBlack && node.parent!!.isBlack) { // Parent is Black
            sibling.isBlack = false
            deleteCase1(node.parent!!)
        } else {
            deleteCase4(node)
        }

    }

    private fun deleteCase4(node: Node<K, V>) {

        val sibling = node.sibling()

        val a: Boolean = sibling!!.left == null || sibling.left!!.isBlack // Left Child of sibling is Black?
        val b: Boolean = sibling.right == null || sibling.right!!.isBlack // Right Child of sibling is Black?

        if (a && b && sibling.isBlack && !node.parent!!.isBlack) { // Parent is Red
            sibling.isBlack = false
            node.parent!!.isBlack = true
        } else {
            deleteCase5(node)
        }

    }

    private fun deleteCase5(node: Node<K, V>) {

        val sibling = node.sibling()

        val a: Boolean = sibling!!.left == null || sibling.left!!.isBlack // Left Child of sibling is Black?
        val b: Boolean = sibling.right == null || sibling.right!!.isBlack // Right Child of sibling is Black?

        if (sibling.isBlack) {
            if (sibling.left?.isBlack == false && b && node == node.parent?.left) {
                sibling.rightRotate()
            } else if (sibling.right?.isBlack == false && a && node == node.parent?.right) {
                sibling.leftRotate()
            }
        }

        deleteCase6(node)

    }

    private fun deleteCase6(node: Node<K, V>) {

        val sibling = node.sibling()

        if (node == node.parent!!.left) { // Node is Left Child of its Parent
            sibling?.right?.isBlack = true
            node.parent?.leftRotate()
        } else {
            sibling?.left?.isBlack = true
            node.parent?.rightRotate()
        }

        if (root == node.parent)
            root = node.parent!!.parent

    }

    private fun min(cur: Node<K, V>?): Node<K, V>? = if (cur?.left == null) cur else min(cur.left)

    private fun max(cur: Node<K, V>?): Node<K, V>? = if (cur?.right == null) cur else max(cur.right)

    private fun next(cur: Node<K, V>?): Node<K, V>? {

        var next = cur ?: return null

        if (next.right != null) {
            return min(next.right!!)
        } else if (next == next.parent?.right) {
            while (next == next.parent?.right) {
                next = next.parent!!
            }
        }

        return next.parent

    }


    override fun iterator(): Iterator<Pair<K, V>> {

        return (object : Iterator<Pair<K, V>> {

            var cur: Node<K, V>? = min(root)
            var prev: Node<K, V>? = min(root)
            var last: Node<K, V>? = max(root)

            override fun hasNext(): Boolean = cur != null && cur!!.key <= last!!.key

            override fun next(): Pair<K, V> {
                prev = cur
                cur = next(cur)
                return Pair(prev!!.key, prev!!.value)
            }
        })

    }

}

