package equipo.closet.closetvirtual.utils

import android.view.View
import android.view.animation.AnimationUtils
import android.content.Context

object AnimationHelper {

    fun applySlideInRightAnimation(view: View, context: Context) {
        val animation = AnimationUtils.loadAnimation(context, equipo.closet.closetvirtual.R.anim.slide_in_right)
        view.startAnimation(animation)
    }

    fun applyFadeInAnimation(view: View, context: Context) {
        val animation = AnimationUtils.loadAnimation(context, equipo.closet.closetvirtual.R.anim.fade_in)
        view.startAnimation(animation)
    }

    fun applySlideInBottomAnimation(view: View, context: Context) {
        val animation = AnimationUtils.loadAnimation(context, equipo.closet.closetvirtual.R.anim.slide_in_bottom)
        view.startAnimation(animation)
    }
}
