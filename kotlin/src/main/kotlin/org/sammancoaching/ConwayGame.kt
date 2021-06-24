package org.sammancoaching


class MyWorld(val width: Int, val height: Int) {
    fun replaceWith(nextGeneration: ByteArray) {
        System.arraycopy(nextGeneration, 0, generation, 0, width * height)
    }

    var generation: ByteArray = ByteArray(width * height)

    fun setDead(x: Int, y: Int) {
        if (isOnGrid(x,y)) {
            generation[y * width + x] = 0
        }
    }

    fun setAlive(x: Int, y: Int) {
        if (isOnGrid(x,y)) {
            generation[y * width + x] = 1
        }
    }

    private fun isOnGrid(x: Int, y: Int): Boolean =
        y * width + x >= 0 && y * width + x < width * height - 1

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
                val type = isAlive(i, j, currentGeneration, Coordinate(i, j))
                setAliveAt(type, nextGeneration, j, i)
            }
        }

        // replace generation
        System.arraycopy(nextGeneration, 0, currentGeneration, 0, size)
        currentGenerationNEW.replaceWith(nextGeneration)
    }

    fun setAliveAt(i: Int, j: Int) {
        setAliveAt(1, currentGeneration, j, i)
        currentGenerationNEW.setAlive(i, j)
    }

    private fun setAliveAt(type: Int, next: ByteArray, j: Int, i: Int) {
        if (type > 0) {
            setAliveAt(next, j, i)
        } else {
            setDeadAt(next, j, i)
        }
    }

    private fun setDeadAt(next: ByteArray, j: Int, i: Int) {
        if (isOnGrid(Coordinate(i, j))) {
            next.setDead(j, i)
        }
    }

    private fun ByteArray.setDead(j: Int, i: Int) {
        this[j * width + i] = 0
    }

    private fun setAliveAt(next: ByteArray, j: Int, i: Int) {
        val pos = j * width + i
        if (pos >= 0 && pos < size - 1)
            next[pos] = 1
    }

    protected fun isAlive(x: Int, y: Int, d: ByteArray, self: Coordinate): Int {
        // Count neighbours
        val livingNeighbours = countLivingNeighbours(d, self)

        //dead
        if (d.isCellDead(x, y, self)) {
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

    private fun ByteArray.isCellDead(x: Int, y: Int, self: Coordinate) = this[self.y * width + self.x] == 0.toByte()

    private fun ByteArray.isCellAlive(self: Coordinate) = this[self.y * width + self.x] == 1.toByte()

    private fun countLivingNeighbours(d: ByteArray, self: Coordinate): Int {
        var livingNeighbours = 0
        for (i in self.x - 1..self.x + 1) {
            for (j in self.y - 1..self.y + 1) {
                val coordinate = Coordinate(i, j)

                if (isOnGrid(coordinate) && coordinate != self) {
                    if (d.isCellAlive(coordinate)) {
                        livingNeighbours++
                    }
                }
            }
        }
        return livingNeighbours
    }

    private fun isOnGrid(coordinate: Coordinate): Boolean =
        coordinate.y * width + coordinate.x >= 0 && coordinate.y * width + coordinate.x < width * height - 1

    fun data(): Grid {
        return PrintableData(width, height, currentGeneration)
    }
}

data class Coordinate(val x: Int, val y:Int)

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
