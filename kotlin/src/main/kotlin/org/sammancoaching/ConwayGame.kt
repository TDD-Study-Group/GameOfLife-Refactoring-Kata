package org.sammancoaching


class MyWorld(val width: Int, val height: Int) {
    var generation: ByteArray = ByteArray(width * height)

    fun replaceWith(nextGeneration: ByteArray) {
        System.arraycopy(nextGeneration, 0, generation, 0, width * height)
    }

    fun setAlive(self: Coordinate) {
        if (isOnGrid(self)) {
            generation[self.y * width + self.x] = 1
        }
    }

    private fun isOnGrid(self: Coordinate): Boolean =
        self.y * width + self.x >= 0 && self.y * width + self.x < width * height - 1

}

class ConwayGame(val width: Int, val height: Int) {

    private val livingCells = mutableSetOf<Coordinate>()

    private val size = width * height
    private var currentGeneration: ByteArray = ByteArray(size)
    var currentGenerationNEW = MyWorld(width, height)

    /**
     *
     * Iterates the game one step forward
     *
     */
    fun iterate() {
        // create next generation
        val nextGeneration = ByteArray(size)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val self = Coordinate(x, y)
                val type = isAliveInNextGeneration(currentGeneration, self)
                setStateAt(type, nextGeneration, self)
            }
        }

        // replace generation
        System.arraycopy(nextGeneration, 0, currentGeneration, 0, size)
        currentGenerationNEW.replaceWith(nextGeneration)
    }

    fun setAliveAt(i: Int, j: Int) {
        val self = Coordinate(i, j)
        setStateAt(1, currentGeneration, self)
        currentGenerationNEW.setAlive(self)
    }

    private fun setStateAt(state: Int, next: ByteArray, self: Coordinate) {
        if (state > 0) {
            setAliveAt(next, self)
        } else {
            setDeadAt(next, self)
        }
    }

    private fun setAliveAt(next: ByteArray, self: Coordinate) {
        if (isOnGrid(self)) {
            next.setAlive(self)
        }
    }

    private fun ByteArray.setAlive(self: Coordinate) {
        this[self.y * width + self.x] = 1
    }

    private fun setDeadAt(next: ByteArray, self: Coordinate) {
        if (isOnGrid(self)) {
            next.setDead(self)
        }
    }

    private fun ByteArray.setDead(self: Coordinate) {
        this[self.y * width + self.x] = 0
    }

    protected fun isAliveInNextGeneration(d: ByteArray, self: Coordinate): Int {
        val livingNeighbours = d.countLivingNeighbours(self)

        if (d.isCellDead(self)) {
            if (livingNeighbours == 3) { //becomes alive.
                return 1
            } else return 0
            //still dead
        } else {
            if (livingNeighbours < 2 || livingNeighbours > 3) { //Dies
                return 0
            } else return 1
            //lives
        }
    }

    private fun ByteArray.isCellDead(self: Coordinate) = this[self.y * width + self.x] == 0.toByte()

    private fun ByteArray.isCellAlive(self: Coordinate) = this[self.y * width + self.x] == 1.toByte()

    private fun ByteArray.countLivingNeighbours(self: Coordinate): Int {
        var livingNeighbours = 0
        for (i in self.x - 1..self.x + 1) {
            for (j in self.y - 1..self.y + 1) {
                val coordinate = Coordinate(i, j)

                if (isOnGrid(coordinate) && coordinate != self) {
                    if (isCellAlive(coordinate)) {
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

data class Coordinate(val x: Int, val y: Int)

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
