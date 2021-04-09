package com.example.huabei_competition.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.system.measureTimeMillis

class ParticleStream(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var particleList = mutableListOf<Particle>()
    var paint = Paint()
    var centerX: Float = 0f
    var centerY: Float = 0f
    private var animator = ValueAnimator.ofFloat(0f, 1f)
    var maxOffset = 300f
    var path = Path()
    private val pathMeasure = PathMeasure()// 用于测量扩散圆上某处的x,y
    private var pos = FloatArray(2)// 扩散圆上某点x,y
    private var tan = FloatArray(2)//扩散圆上某一点的切线
    var radius: Float = 200f
    val sum = 5000f
    var type: Int = 0
    var speedBound = IntArray(2)
    var paintColor: Int = Color.WHITE

    init {
        speedBound[0] = 10
        speedBound[1] = 2
        animator.duration = 5000
        animator.repeatCount = -1
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            if (type == 1)
                updateParticle()
            else
                updateParticle(it.animatedValue as Float)
            invalidate()
        }
    }

    /**
     * 瀑布式
     */
    private fun updateParticle(value: Float) {
        val random = Random()
        particleList.forEach {
            it.y += it.speed
            if (it.y - centerY >= it.maxOffset) {
                it.y = centerY
                it.x = random.nextInt((centerX).toInt()).toFloat()
                it.speed = (random.nextInt(speedBound[0]) + speedBound[1])
            }
            it.alpha = ((1f - (it.y - centerY) / it.maxOffset) * 225f).toInt()
        }
    }

    /**
     * 圆形发散式
     */
    private fun updateParticle() {
        val random = Random()
        particleList.forEach {
            if (it.offset > it.maxOffset) {
                it.offset = 0
                it.speed = (random.nextInt(speedBound[0]) + speedBound[1])
            }
            it.alpha = ((1f - it.offset / it.maxOffset) * 225f).toInt()
            it.x = (centerX + cos(it.angle) * (radius + it.offset)).toFloat()
            if (it.y > centerY) {
                it.y = (centerY + sin(it.angle) * (radius + it.offset)).toFloat()
            } else {
                it.y = (centerY - sin(it.angle) * (radius + it.offset)).toFloat()
            }
            it.offset += it.speed
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "onSizeChanged: ")
        paint.color = paintColor
        val random = Random()
        var nextX = 0f
        var nextY = 0f
        var speed = 0
        if (type == 1) {
            centerX = (w / 2).toFloat()
            centerY = (h / 2).toFloat()
            // 初始化圆形C
            path.addCircle(centerX, centerY, radius, Path.Direction.CW)
            pathMeasure.setPath(path, false)
            var angle: Double
            var offset = 0
            //初始化粒子
            for (i in 0..sum.toInt()) {
                pathMeasure.getPosTan(i / sum * pathMeasure.length, pos, tan)
                nextX = pos[0] + random.nextInt(6) - 3f
                nextY = pos[1] + random.nextInt((6)) - 3f
                speed = random.nextInt(speedBound[0]) + speedBound[1]
                angle = acos(((pos[0] - centerX) / radius).toDouble())
                offset = random.nextInt((radius / 10).toInt())
                val particle =
                        Particle(
                                x = nextX,
                                y = nextY,
                                radius = 2f,
                                speed = speed,
                                alpha = 225,
                                maxOffset = maxOffset,
                                angle = angle,
                                offset = offset
                        )
                particleList.add(particle)
            }
        } else {
            centerX = w.toFloat()
            centerY = 0f
            for (i in 0..sum.toInt()) {
                nextX = random.nextInt(centerX.toInt()).toFloat()
                nextY = random.nextInt(maxOffset.toInt()).toFloat()
                speed = random.nextInt(speedBound[0]) + speedBound[1]
                particleList.add(Particle(nextX, nextY, 2f, speed, 225, maxOffset))
            }
        }
//        animator.start()
    }


    fun start() {
        animator.repeatCount = -1
        animator.start()
    }

    fun stop() {
        animator.repeatCount = 1
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val time = measureTimeMillis {
            paint.isAntiAlias = true
            particleList.forEach {
                paint.alpha = it.alpha
                canvas.drawCircle(it.x, it.y, it.radius, paint)
            }
        }
        Log.d(TAG, "onDraw: $time")
    }
}

class Particle(
        var x: Float,
        var y: Float,
        var radius: Float,//粒子半径
        var speed: Int,
        var alpha: Int,
        var maxOffset: Float,
        var offset: Int = 0,//当前移动距离
        var angle: Double = 0.0// 粒子角度
)

private const val TAG = "MainActivity"