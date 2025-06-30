package equipo.closet.closetvirtual.utils

import android.animation.*
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import android.content.Context
import androidx.recyclerview.widget.RecyclerView

object AnimationHelper {

    // Animaciones básicas mejoradas
    fun applySlideInRightAnimation(view: View, context: Context, delay: Long = 0) {
        val animation = AnimationUtils.loadAnimation(context, equipo.closet.closetvirtual.R.anim.slide_in_right)
        animation.startOffset = delay
        view.startAnimation(animation)
    }

    fun applyFadeInAnimation(view: View, context: Context, delay: Long = 0) {
        val animation = AnimationUtils.loadAnimation(context, equipo.closet.closetvirtual.R.anim.fade_in)
        animation.startOffset = delay
        view.startAnimation(animation)
    }

    fun applySlideInBottomAnimation(view: View, context: Context, delay: Long = 0) {
        val animation = AnimationUtils.loadAnimation(context, equipo.closet.closetvirtual.R.anim.slide_in_bottom)
        animation.startOffset = delay
        view.startAnimation(animation)
    }

    // Nuevas animaciones avanzadas con ObjectAnimator
    fun animateCardEntrance(view: View, delay: Long = 0) {
        view.alpha = 0f
        view.translationY = 100f
        view.scaleX = 0.8f
        view.scaleY = 0.8f

        val animatorSet = AnimatorSet()

        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        val slideUp = ObjectAnimator.ofFloat(view, "translationY", 100f, 0f)
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1f)

        animatorSet.playTogether(fadeIn, slideUp, scaleX, scaleY)
        animatorSet.duration = 600
        animatorSet.interpolator = OvershootInterpolator(0.5f)
        animatorSet.startDelay = delay
        animatorSet.start()
    }

    fun animateGridItemsSequentially(views: List<View>) {
        views.forEachIndexed { index, view ->
            animateCardEntrance(view, index * 100L)
        }
    }

    fun animateButtonPress(view: View, onAnimationEnd: (() -> Unit)? = null) {
        val scaleDown = AnimatorSet()
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.95f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f)
        scaleDown.playTogether(scaleDownX, scaleDownY)
        scaleDown.duration = 100
        scaleDown.interpolator = AccelerateDecelerateInterpolator()

        val scaleUp = AnimatorSet()
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1f)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f)
        scaleUp.playTogether(scaleUpX, scaleUpY)
        scaleUp.duration = 100
        scaleUp.interpolator = AccelerateDecelerateInterpolator()

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd?.invoke()
            }
        })
        animatorSet.start()
    }

    fun animateCounterUpdate(view: View, fromValue: Int, toValue: Int) {
        val valueAnimator = ValueAnimator.ofInt(fromValue, toValue)
        valueAnimator.duration = 500
        valueAnimator.interpolator = DecelerateInterpolator()

        // Animación de escala simultánea
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f)
        scaleX.duration = 500
        scaleY.duration = 500

        valueAnimator.addUpdateListener { animator ->
            if (view is android.widget.TextView) {
                view.text = animator.animatedValue.toString()
            }
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(valueAnimator, scaleX, scaleY)
        animatorSet.start()
    }

    fun animateSearchBarExpand(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f)
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0.7f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, fadeIn)
        animatorSet.duration = 300
        animatorSet.interpolator = OvershootInterpolator(0.3f)
        animatorSet.start()
    }

    fun animateHeaderSlideIn(view: View) {
        view.translationY = -200f
        view.alpha = 0f

        val slideIn = ObjectAnimator.ofFloat(view, "translationY", -200f, 0f)
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(slideIn, fadeIn)
        animatorSet.duration = 700
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.start()
    }

    fun animateCardHover(view: View) {
        val elevationAnimator = ObjectAnimator.ofFloat(view, "elevation", view.elevation, view.elevation + 8f)
        val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.02f)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.02f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(elevationAnimator, scaleXAnimator, scaleYAnimator)
        animatorSet.duration = 200
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    fun animateCardUnhover(view: View, originalElevation: Float) {
        val elevationAnimator = ObjectAnimator.ofFloat(view, "elevation", view.elevation, originalElevation)
        val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", view.scaleX, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", view.scaleY, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(elevationAnimator, scaleXAnimator, scaleYAnimator)
        animatorSet.duration = 200
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    fun animateOutfitCreationFlow(views: List<View>) {
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationX = if (index % 2 == 0) -300f else 300f

            val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            val slideIn = ObjectAnimator.ofFloat(view, "translationX", view.translationX, 0f)

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(fadeIn, slideIn)
            animatorSet.duration = 500
            animatorSet.startDelay = index * 150L
            animatorSet.interpolator = DecelerateInterpolator()
            animatorSet.start()
        }
    }

    fun animateFloatingActionButton(view: View) {
        // Animación de entrada tipo "bounce"
        view.scaleX = 0f
        view.scaleY = 0f
        view.alpha = 0f

        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f)
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, fadeIn)
        animatorSet.duration = 400
        animatorSet.interpolator = OvershootInterpolator(1.2f)
        animatorSet.startDelay = 500
        animatorSet.start()
    }

    // Animación para transiciones entre fragmentos
    fun createSlideTransition(enteringView: View, exitingView: View?) {
        // Salida del fragmento anterior
        exitingView?.let { view ->
            val slideOut = ObjectAnimator.ofFloat(view, "translationX", 0f, -view.width.toFloat())
            val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)

            val exitSet = AnimatorSet()
            exitSet.playTogether(slideOut, fadeOut)
            exitSet.duration = 300
            exitSet.start()
        }

        // Entrada del nuevo fragmento
        enteringView.translationX = enteringView.width.toFloat()
        enteringView.alpha = 0f

        val slideIn = ObjectAnimator.ofFloat(enteringView, "translationX", enteringView.width.toFloat(), 0f)
        val fadeIn = ObjectAnimator.ofFloat(enteringView, "alpha", 0f, 1f)

        val enterSet = AnimatorSet()
        enterSet.playTogether(slideIn, fadeIn)
        enterSet.duration = 300
        enterSet.interpolator = DecelerateInterpolator()
        enterSet.startDelay = 100
        enterSet.start()
    }

    // Animación de pulso para elementos importantes
    fun animatePulse(view: View, duration: Long = 1000) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.7f, 1f)

        // Aplicar repeatMode y repeatCount a cada ObjectAnimator
        listOf(scaleX, scaleY, alpha).forEach { animator ->
            animator.repeatMode = ValueAnimator.REVERSE
            animator.repeatCount = ValueAnimator.INFINITE
            animator.duration = duration
            animator.interpolator = AccelerateDecelerateInterpolator()
        }

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, alpha)
        animatorSet.start()
    }
}