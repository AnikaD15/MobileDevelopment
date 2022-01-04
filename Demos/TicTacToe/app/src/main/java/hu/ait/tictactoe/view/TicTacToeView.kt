package hu.ait.tictactoe.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import hu.ait.tictactoe.MainActivity
import hu.ait.tictactoe.model.TicTacToeModel


import android.app.Activity
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.widget.Chronometer
import hu.ait.tictactoe.R


class TicTacToeView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    lateinit var paintBackGround: Paint
    lateinit var paintLine: Paint
    lateinit var paintText: Paint
    lateinit var paintCross: Paint
    lateinit var paintCircle: Paint
    lateinit var timer: Chronometer

    init {

        timer = Chronometer(context)

        paintBackGround = Paint()
        paintBackGround.color = Color.CYAN
        paintBackGround.style = Paint.Style.FILL

        paintLine = Paint()
        paintLine.color = Color.WHITE
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 5f

        paintCross = Paint()
        paintCross.color = Color.BLUE
        paintCross.style = Paint.Style.STROKE
        paintCross.strokeWidth = 5f

        paintCircle = Paint()
        paintCircle.color = Color.RED
        paintCircle.style = Paint.Style.STROKE
        paintCircle.strokeWidth = 5f

        paintText = Paint()

        paintText.color = Color.RED

    }

    // called when view has final size
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        paintText.textSize = height / 3f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackGround)

        //canvas?.drawBitmap(bitmapBackground,
        // 0f, 0f, null)

        drawGameArea(canvas!!)

        drawPlayers(canvas!!)

        // 0, 0 is the bottom left corner of letter
        // canvas?.drawText("A", 0f, height/3f, paintText)
    }


    private fun drawGameArea(canvas: Canvas) {
        // border
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintLine)
        // two horizontal lines
        canvas.drawLine(
            0f, (height / 3).toFloat(), width.toFloat(), (height / 3).toFloat(),
            paintLine
        )
        canvas.drawLine(
            0f, (2 * height / 3).toFloat(), width.toFloat(),
            (2 * height / 3).toFloat(), paintLine
        )

        // two vertical lines
        canvas.drawLine(
            (width / 3).toFloat(), 0f, (width / 3).toFloat(), height.toFloat(),
            paintLine
        )
        canvas.drawLine(
            (2 * width / 3).toFloat(), 0f, (2 * width / 3).toFloat(), height.toFloat(),
            paintLine
        )
    }

    private fun drawPlayers(canvas: Canvas) {
        for (i in 0..2) {
            for (j in 0..2) {
                if (TicTacToeModel.getFieldContent(i, j) == TicTacToeModel.CIRCLE) {
                    val centerX = (i * width / 3 + width / 6).toFloat()
                    val centerY = (j * height / 3 + height / 6).toFloat()
                    val radius = height / 6 - 2

                    canvas.drawCircle(centerX, centerY, radius.toFloat(), paintCircle)
                } else if (TicTacToeModel.getFieldContent(i, j) == TicTacToeModel.CROSS) {
                    canvas.drawLine(
                        (i * width / 3).toFloat(), (j * height / 3).toFloat(),
                        ((i + 1) * width / 3).toFloat(),
                        ((j + 1) * height / 3).toFloat(), paintCross
                    )

                    canvas.drawLine(
                        ((i + 1) * width / 3).toFloat(), (j * height / 3).toFloat(),
                        (i * width / 3).toFloat(), ((j + 1) * height / 3).toFloat(), paintCross
                    )
                }
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("TAG_DEMO", "onTouch event occurred")

        (context as MainActivity).startTimer()
        if (event?.action == MotionEvent.ACTION_DOWN) {

            val tX = event.x.toInt() / (width / 3)
            val tY = event.y.toInt() / (height / 3)

            if (tX < 3 && tY < 3 && TicTacToeModel.getFieldContent(tX, tY) ==
                TicTacToeModel.EMPTY
            ) {
                TicTacToeModel.setFieldContent(tX, tY, TicTacToeModel.getNextPlayer())
                TicTacToeModel.changeNextPlayer()
                invalidate()

                //((MainActivity)getContext()).showTextMessage("The next player is...")
                var nextMessage = context.getString(R.string.text_next_player, "O")
                if (TicTacToeModel.getNextPlayer() == TicTacToeModel.CROSS) {
                    nextMessage = context.getString(R.string.text_next_player, "X")
                }

                // view displayed in activity
                // context is root of activity
                // indicates which activity you are in
                // can be cast as MainActivity if it is the main activity
                (context as MainActivity).showTextMessage(nextMessage)

            }

            if (isCircleWinner()) {
                (context as MainActivity).showMessage("WINNER is O")
            }

            if (isCrossWinner()) {
                (context as MainActivity).showMessage("WINNER is X")
            }
        }

        if(!isCrossWinner() && !isCircleWinner()){
                (context as MainActivity).restartTimer()
        }
        else{
            resetGame()
        }

        return true
    }

    // system calls function before drawing view
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = View.MeasureSpec.getSize(widthMeasureSpec)
        val h = View.MeasureSpec.getSize(heightMeasureSpec)
        val d = if (w == 0) h else if (h == 0) w else if (w < h) w else h
        setMeasuredDimension(d, d)
    }

    private fun isCircleWinner(): Boolean {
        var threeInRow: Boolean = true
        Log.d("TAG_DEMO", "Checking if circle is winner")
        // check row by row
        for (r in 0..2) {
            for (c in 0..2) {
                var currPlayer = TicTacToeModel.getFieldContent(r, c)
                if (currPlayer != TicTacToeModel.CIRCLE) {
                    threeInRow = false
                    break
                }
            }

            // if three in row found, return true to end loop early
            if (threeInRow) return threeInRow
            if (r != 2) threeInRow = true
        }

        // check if three in row for any row
        // if false reset boolean
        if (threeInRow) {
            return threeInRow
        } else {
            threeInRow = true
        }
//
//        // check column by column
        for (c in 0..2) {
            for (r in 0..2) {
                if (TicTacToeModel.getFieldContent(r, c) != TicTacToeModel.CIRCLE) {
                    threeInRow = false
                    break
                }
            }
//
//            // if three in row found, return true to end loop early
            if (threeInRow) return threeInRow
            if (c != 2) threeInRow = true
        }
//
//        // check if three in row for any column
//        // if false reset boolean
        if (threeInRow) {
            return threeInRow
        } else {
            threeInRow = true
        }
//
//        // check left to right diagonally
        for (r in 0..2) {
            if (TicTacToeModel.getFieldContent(r, r) != TicTacToeModel.CIRCLE) {
                threeInRow = false
                break
            }
        }
//
//        // check if three in row for diagonal
//        // if false reset boolean
        if (threeInRow) {
            return threeInRow
        } else {
            threeInRow = true
        }
//
//        // check right to left diagonal
        for (r in 0..2) {
            if (r == 1 && TicTacToeModel.getFieldContent(r, r) != TicTacToeModel.CIRCLE) {
                threeInRow = false
                break
            }

            if (r == 0 && TicTacToeModel.getFieldContent(r, r+2) != TicTacToeModel.CIRCLE) {
                threeInRow = false
                break
            }

            if (r == 2 && TicTacToeModel.getFieldContent(r, r-2) != TicTacToeModel.CIRCLE) {
                threeInRow = false
                break
            }
        }

        return threeInRow
    }

    private fun isCrossWinner(): Boolean {
        var threeInRow: Boolean = true
        Log.d("TAG_DEMO", "Checking if cross is winner")
        // check row by row
        for (r in 0..2) {
            for (c in 0..2) {
                var currPlayer = TicTacToeModel.getFieldContent(r, c)
                if (currPlayer != TicTacToeModel.CROSS) {
                    threeInRow = false
                    break
                }
            }

            // if three in row found, return true to end loop early
            if (threeInRow) return threeInRow
            if (r != 2) threeInRow = true
        }

        // check if three in row for any row
        // if false reset boolean
        if (threeInRow) {
            return threeInRow
        } else {
            threeInRow = true
        }
//
//        // check column by column
        for (c in 0..2) {
            for (r in 0..2) {
                if (TicTacToeModel.getFieldContent(r, c) != TicTacToeModel.CROSS) {
                    threeInRow = false
                    break
                }
            }
//
//            // if three in row found, return true to end loop early
            if (threeInRow) return threeInRow
            if (c != 2) threeInRow = true
        }
//
//        // check if three in row for any column
//        // if false reset boolean
        if (threeInRow) {
            return threeInRow
        } else {
            threeInRow = true
        }
//
//        // check left to right diagonally
        for (r in 0..2) {
            if (TicTacToeModel.getFieldContent(r, r) != TicTacToeModel.CROSS) {
                threeInRow = false
                break
            }
        }
//
//        // check if three in row for diagonal
//        // if false reset boolean
        if (threeInRow) {
            return threeInRow
        } else {
            threeInRow = true
        }
//
//        // check right to left diagonal
        for (r in 0..2) {
            if (r == 1 && TicTacToeModel.getFieldContent(r, r) != TicTacToeModel.CROSS) {
                threeInRow = false
                break
            }

            if (r == 0 && TicTacToeModel.getFieldContent(r, r+2) != TicTacToeModel.CROSS) {
                threeInRow = false
                break
            }

            if (r == 2 && TicTacToeModel.getFieldContent(r, r-2) != TicTacToeModel.CROSS) {
                threeInRow = false
                break
            }
        }

        return threeInRow
    }



    public fun resetGame() {
        TicTacToeModel.resetModel()

        (context as MainActivity).showTextMessage(
            context.getString(R.string.text_next_player, "O")
        )

        (context as MainActivity).resetTimer()

        invalidate()
    }

}