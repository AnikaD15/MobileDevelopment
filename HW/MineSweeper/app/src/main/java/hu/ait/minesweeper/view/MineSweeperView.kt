package hu.ait.minesweeper.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.android.material.snackbar.Snackbar
import hu.ait.minesweeper.MainActivity
import hu.ait.minesweeper.R
import hu.ait.minesweeper.model.MineSweeperModel
import hu.ait.minesweeper.model.MineSweeperModel.EMPTY
import hu.ait.minesweeper.model.MineSweeperModel.MINE
import hu.ait.minesweeper.model.MineSweeperModel.fieldMatrix
import hu.ait.minesweeper.model.MineSweeperModel.populateField
import hu.ait.minesweeper.model.MineSweeperModel.size


class MineSweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    lateinit var paintBackground: Paint
    lateinit var paintLine: Paint
    lateinit var paintNum: Paint
    lateinit var bitmapMine: Bitmap
    lateinit var bitmapFlag: Bitmap

    var lostGame: Boolean = false
    var wonGame: Boolean = false
    var hasGameEnded: Boolean = false

    init {
        bitmapMine = BitmapFactory.decodeResource(
            context!!.resources,
            R.drawable.mine
        )

        bitmapFlag = BitmapFactory.decodeResource(
            context!!.resources,
            R.drawable.flag
        )

        paintBackground = Paint()
        paintBackground.color = Color.GRAY

        paintLine = Paint()
        paintLine.color = Color.WHITE

        paintNum = Paint()
        paintNum.color = Color.GREEN
        paintNum.textSize = 100f

        populateField()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        bitmapFlag = Bitmap.createScaledBitmap(
            bitmapFlag,
            width / size, height / size, false
        )

        bitmapMine = Bitmap.createScaledBitmap(
            bitmapMine,
            width / size, height / size, false
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawGameArea(canvas!!)

        drawPlayers(canvas!!)
    }

    private fun drawPlayers(canvas: Canvas) {
        for (r in 0..size - 1) {
            for (c in 0..size - 1) {
                if (MineSweeperModel.fieldMatrix[r][c].isFlagged) {
                    canvas?.drawBitmap(
                        bitmapFlag,
                        ((c * width) / size).toFloat(), ((r * height) / size).toFloat(), null
                    )
                } else if (MineSweeperModel.fieldMatrix[r][c].wasClicked) {
                    if (fieldMatrix[r][c].type == EMPTY) {
                        canvas?.drawText(
                            fieldMatrix[r][c].minesAround.toString(),
                            ((c * width) / size).toFloat(),
                            (((r+1) * height) / size).toFloat(),
                            paintNum
                        )
                    } else {
                        canvas?.drawBitmap(
                            bitmapMine,
                            ((c * width) / size).toFloat(),
                            ((r * height) / size).toFloat(),
                            null
                        )
                    }

                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN && !hasGameEnded) {

            val tY = event.x.toInt() / (width / size)
            val tX = event.y.toInt() / (height / size)

            if (tX < size && tY < size && !fieldMatrix[tX][tY].wasClicked) {

                if ((context as MainActivity).isFlagMode()) {
                    fieldMatrix[tX][tY].isFlagged = true
                    var flag = fieldMatrix[tX][tY].isFlagged
                } else {
                    fieldMatrix[tX][tY].wasClicked = true
                    fieldMatrix[tX][tY].isFlagged = false

                    if (fieldMatrix[tX][tY].type == MINE) {
                        (context as MainActivity).showMsg(context.getString(R.string.lose_msg))
                        lostGame = true
                    }
                }
            }

            wonGame = isWinner()
            if (wonGame) {
                (context as MainActivity).showMsg(context.getString(R.string.win_msg))
            }

            hasGameEnded = wonGame || lostGame

            Log.d("TAG_DEMO", "Touched coordinate ${tX}, ${tY}")
            Log.d("TAG_DEMO", "Flag mode ${fieldMatrix[tX][tY].isFlagged}")
            Log.d("TAG_DEMO", "Game ended: ${hasGameEnded}")
            invalidate()
        }

        return true
    }


    private fun drawGameArea(canvas: Canvas) {
        // border
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackground)

        // for loop to draw horizontal lines
        for (i in 1..4) {
            canvas.drawLine(
                0f,
                ((height * i) / size).toFloat(),
                width.toFloat(),
                ((height * i) / size).toFloat(),
                paintLine
            )
        }

        // for loop to draw vertical lines
        for (i in 1..4) {
            canvas.drawLine(
                ((width * i) / size).toFloat(),
                0f,
                ((width * i) / size).toFloat(),
                height.toFloat(),
                paintLine
            )
        }
    }

    private fun isWinner(): Boolean {
        var hasWon = true

        for (r in 0..size - 1) {
            for (c in 0..size - 1) {
                if (!MineSweeperModel.fieldMatrix[r][c].wasClicked && fieldMatrix[r][c].type == EMPTY) {
                    hasWon = false
                    return hasWon
                }

                if (MineSweeperModel.fieldMatrix[r][c].wasClicked && fieldMatrix[r][c].type == MINE) {
                    hasWon = false
                    return hasWon
                }

            }
        }

        return hasWon
    }

    fun resetField() {
        hasGameEnded = false
        lostGame = false
        wonGame = false

        fieldMatrix = Array(5) { Array(5) { MineSweeperModel.Field(EMPTY, 0, false, false) } }
        populateField()
        invalidate()
    }
}



