package hu.ait.minesweeper.model

import android.graphics.Point
import java.util.*

object MineSweeperModel {
    data class Field(
        var type: Short, var minesAround: Int,
        var isFlagged: Boolean, var wasClicked: Boolean
    )

    public val EMPTY: Short = 0
    public val MINE: Short = 2
    public val size: Int = 5

    // positions of mines stored
    var minePositions = mutableSetOf<Point>()

    // empty matrix
    var fieldMatrix: Array<Array<Field>> = Array(size) { Array(size) { Field(EMPTY, 0, false, false) } }

    fun generateMines() {
        for (m in 0..2) {
            val rand = Random(System.currentTimeMillis())
            var x = rand.nextInt(size)
            var y = rand.nextInt(size)

            while(minePositions.contains(Point(x, y))){
                x = rand.nextInt(size)
                y = rand.nextInt(size)
            }

            var minePos = Point(x, y)
            minePositions.add(minePos)
            fieldMatrix[x][y].type = MINE
        }
    }

    fun populateField() {
        generateMines()

        for (mine in minePositions){
            // row by row determine mine count
            for(r in mine.x - 1 .. mine.x + 1){
                for(c in mine.y - 1 .. mine.y + 1){
                    if (r in 0 until size && c in 0 until size && fieldMatrix[r][c].type!= MINE){
                        fieldMatrix[r][c].minesAround+=1
                    }
                }
            }
        }
    }
}