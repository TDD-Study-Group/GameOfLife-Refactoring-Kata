package org.sammancoaching


class MyWorld(val width: Int, val height: Int) {
    fun replaceWith(nextGeneration: ByteArray) {
        System.arraycopy(nextGeneration, 0, currentGeneration, 0, width * height)
    }

    var currentGeneration: ByteArray = ByteArray(width * height)

}

class ConwayGame(val width: Int, val height: Int) {

    private val size = width * height
    var currentGeneration: ByteArray = ByteArray(size)
    var currentGenerationNEW = MyWorld(width, height)

    /**
     *
     * Iterates the game one step forward
     *
     */
    fun iterate() {
        // create next generation
        val nextGeneration = ByteArray(size)
        for (i in 0 until width) {
            for (j in 0 until height) {
                val type = isAlive(i, j, currentGeneration)
                setAliveAt(type, nextGeneration, j, i)
            }
        }

        // replace generation
        System.arraycopy(nextGeneration, 0, currentGeneration, 0, size)
        currentGenerationNEW.replaceWith(nextGeneration)
    }

    fun setAliveAt(i: Int, j: Int) {
        setAliveAt(1, currentGeneration, j, i)
    }

    private fun setAliveAt(type: Int, next: ByteArray, j: Int, i: Int) {
        if (type > 0) {
            setAliveAt(next, j, i)
        } else {
            setDeadAt(next, j, i)
        }
    }

    private fun setDeadAt(next: ByteArray, j: Int, i: Int) {
        val pos = j * width + i
        if (pos >= 0 && pos < size - 1) {
            next[pos] = 0
        }
    }

    private fun setAliveAt(next: ByteArray, j: Int, i: Int) {
        val pos = j * width + i
        if (pos >= 0 && pos < size - 1)
            next[pos] = 1
    }

    protected fun isAlive(x: Int, y: Int, d: ByteArray): Int {
        // Count neighbours
        val livingNeighbours = countLivingNeighbours(x, y, d)

        //dead
        if (d.isCellDead(x, y)) {
            if (livingNeighbours == 3) { //becomes alive.
                return 1
            } else return 0
            //still dead
        } else { //live
            if (livingNeighbours < 2 || livingNeighbours > 3) { //Dies
                return 0
            } else return 1
            //lives
        }
    }

    private fun ByteArray.isCellDead(x: Int, y: Int) = this[y * width + x] == 0.toByte()

    private fun ByteArray.isCellAlive(x: Int, y: Int) = this[y * width + x] == 1.toByte()

    private fun countLivingNeighbours(x: Int, y: Int, d: ByteArray): Int {
        var livingNeighbours = 0
        for (i in x - 1..x + 1) {
            for (j in y - 1..y + 1) {
                if (isOnGrid(i, j) && !isSamePosition(i, j, x, y)) {
                    if (d.isCellAlive(i, j)) {
                        livingNeighbours++
                    }
                }
            }
        }
        return livingNeighbours
    }

    private fun isOnGrid(x: Int, y: Int) =
        y * width + x >= 0 && y * width + x < width * height - 1

    private fun isSamePosition(i: Int, j: Int, x: Int, y: Int) = j == y && i == x

    fun data(): Grid {
        return PrintableData(width, height, currentGeneration)
    }
}

class PrintableData(val width: Int, val height: Int, val data: ByteArray) : Grid {
    override fun width(): Int {
        return width
    }

    override fun height(): Int {
        return height
    }

    override fun contentAt(x: Int, y: Int): String {
        val pos = y * width + x
        val alive: Byte = 1
        if (data[pos] == alive) {
            return "*"
        } else {
            return "."
        }
    }

}
