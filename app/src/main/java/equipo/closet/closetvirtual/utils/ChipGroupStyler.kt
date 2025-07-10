package equipo.closet.closetvirtual.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import equipo.closet.closetvirtual.R

/**
 * Objeto utilitario para aplicar estilos y animaciones a los ChipGroups y Chips.
 * Este objeto proporciona métodos para estilizar los chips con diferentes temas
 * y añadir animaciones de aparición y clic.
 */
object ChipGroupStyler {

    /**
     * Define los estilos predefinidos para los Chips.
     */
    enum class ChipStyle {
        ELEGANT_PURPLE, // Estilo púrpura elegante
        SOFT_GRAY,      // Estilo gris suave
        VIBRANT_COLORS, // Estilo con colores vibrantes
        MINIMALIST      // Estilo minimalista
    }

    /**
     * Aplica un estilo a todos los Chips dentro de un ChipGroup.
     *
     * @param context El contexto de la aplicación.
     * @param chipGroup El ChipGroup al que se le aplicará el estilo.
     * @param style El estilo a aplicar (por defecto ELEGANT_PURPLE).
     * @param isCheckable Indica si los chips deben ser seleccionables (por defecto true).
     */
    fun styleChipGroup(
        context: Context,
        chipGroup: ChipGroup,
        style: ChipStyle = ChipStyle.ELEGANT_PURPLE,
        isCheckable: Boolean = true
    ) {
        // Itera sobre cada vista en el ChipGroup
        chipGroup.forEach { view ->
            // Si la vista es un Chip, aplica el estilo
            if (view is Chip) {
                styleChip(context, view, style, isCheckable)
            }
        }
        
        // Anima la aparición del ChipGroup
        animateChipGroupAppearance(chipGroup)
    }

    /**
     * Aplica un estilo específico a un solo Chip.
     *
     * @param context El contexto de la aplicación.
     * @param chip El Chip al que se le aplicará el estilo.
     * @param style El estilo a aplicar.
     * @param isCheckable Indica si el chip debe ser seleccionable.
     */
    fun styleChip(
        context: Context,
        chip: Chip,
        style: ChipStyle,
        isCheckable: Boolean
    ) {
        // Aplica el estilo según el tipo de estilo seleccionado
        when (style) {
            ChipStyle.ELEGANT_PURPLE -> applyElegantPurpleStyle(context, chip, isCheckable)
            ChipStyle.SOFT_GRAY -> applySoftGrayStyle(context, chip, isCheckable)
            ChipStyle.VIBRANT_COLORS -> applyVibrantStyle(context, chip, isCheckable)
            ChipStyle.MINIMALIST -> applyMinimalistStyle(context, chip, isCheckable)
        }
        
        // Configura propiedades comunes del Chip
        chip.elevation = 4f // Elevación del chip
        chip.chipCornerRadius = 20f // Radio de las esquinas del chip
        chip.chipStrokeWidth = 2f // Ancho del borde del chip
        chip.isCheckable = isCheckable // Habilita/deshabilita la capacidad de selección
        
        // Configura la animación al hacer clic en el chip
        setupChipClickAnimation(chip)
    }

    /**
     * Aplica el estilo "Elegant Purple" a un Chip.
     *
     * @param context El contexto de la aplicación.
     * @param chip El Chip al que se le aplicará el estilo.
     * @param isCheckable Indica si el chip es seleccionable.
     */
    private fun applyElegantPurpleStyle(context: Context, chip: Chip, isCheckable: Boolean) {
        // Obtiene los colores desde los recursos
        val purpleColor = ContextCompat.getColor(context, R.color.bottom_nav_active_background)
        val purpleDark = ContextCompat.getColor(context, R.color.purple_700)
        val whiteColor = ContextCompat.getColor(context, R.color.white)
        val grayLight = ContextCompat.getColor(context, R.color.gray_light)
        
        // Si el chip es seleccionable, define los estados de color para los diferentes estados (seleccionado/no seleccionado)
        if (isCheckable) {
            // Lista de estados de color para el fondo del chip
            val backgroundColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked), // Estado seleccionado
                    intArrayOf(-android.R.attr.state_checked) // Estado no seleccionado
                ),
                intArrayOf(purpleColor, grayLight) // Colores correspondientes a los estados
            )
            
            // Lista de estados de color para el texto del chip
            val textColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(whiteColor, purpleDark)
            )
            
            // Lista de estados de color para el borde del chip
            val strokeColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(purpleDark, purpleColor)
            )
            
            // Aplica los estados de color al chip
            chip.chipBackgroundColor = backgroundColorStateList
            chip.setTextColor(textColorStateList)
            chip.chipStrokeColor = strokeColorStateList
        } else {
            // Si el chip no es seleccionable, aplica un color fijo
            chip.chipBackgroundColor = ColorStateList.valueOf(purpleColor)
            chip.setTextColor(whiteColor)
            chip.chipStrokeColor = ColorStateList.valueOf(purpleDark)
        }
    }

    /**
     * Aplica el estilo "Soft Gray" a un Chip.
     *
     * @param context El contexto de la aplicación.
     * @param chip El Chip al que se le aplicará el estilo.
     * @param isCheckable Indica si el chip es seleccionable.
     */
    private fun applySoftGrayStyle(context: Context, chip: Chip, isCheckable: Boolean) {
        // Obtiene los colores desde los recursos
        val grayMedium = ContextCompat.getColor(context, R.color.gray_medium)
        val grayDark = ContextCompat.getColor(context, R.color.gray_dark)
        val whiteColor = ContextCompat.getColor(context, R.color.white)
        val grayLight = ContextCompat.getColor(context, R.color.gray_light)
        
        // Si el chip es seleccionable, define los estados de color
        if (isCheckable) {
            val backgroundColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(grayDark, whiteColor)
            )
            
            val textColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(whiteColor, grayDark)
            )
            
            val strokeColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(grayDark, grayMedium)
            )
            
            chip.chipBackgroundColor = backgroundColorStateList
            chip.setTextColor(textColorStateList)
            chip.chipStrokeColor = strokeColorStateList
        } else {
            // Si el chip no es seleccionable, aplica un color fijo
            chip.chipBackgroundColor = ColorStateList.valueOf(grayMedium)
            chip.setTextColor(whiteColor)
            chip.chipStrokeColor = ColorStateList.valueOf(grayDark)
        }
    }

    /**
     * Aplica el estilo "Vibrant Colors" a un Chip.
     *
     * @param context El contexto de la aplicación.
     * @param chip El Chip al que se le aplicará el estilo.
     * @param isCheckable Indica si el chip es seleccionable.
     */
    private fun applyVibrantStyle(context: Context, chip: Chip, isCheckable: Boolean) {
        // Obtiene los colores desde los recursos
        val tealColor = ContextCompat.getColor(context, R.color.teal_200)
        val tealDark = ContextCompat.getColor(context, R.color.teal_700)
        val whiteColor = ContextCompat.getColor(context, R.color.white)
        val grayLight = ContextCompat.getColor(context, R.color.gray_light)
        
        // Si el chip es seleccionable, define los estados de color
        if (isCheckable) {
            val backgroundColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(tealColor, whiteColor)
            )
            
            val textColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(tealDark, tealDark)
            )
            
            val strokeColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(tealDark, tealColor)
            )
            
            chip.chipBackgroundColor = backgroundColorStateList
            chip.setTextColor(textColorStateList)
            chip.chipStrokeColor = strokeColorStateList
        } else {
            // Si el chip no es seleccionable, aplica un color fijo
            chip.chipBackgroundColor = ColorStateList.valueOf(tealColor)
            chip.setTextColor(tealDark)
            chip.chipStrokeColor = ColorStateList.valueOf(tealDark)
        }
    }

    /**
     * Aplica el estilo "Minimalist" a un Chip.
     *
     * @param context El contexto de la aplicación.
     * @param chip El Chip al que se le aplicará el estilo.
     * @param isCheckable Indica si el chip es seleccionable.
     */
    private fun applyMinimalistStyle(context: Context, chip: Chip, isCheckable: Boolean) {
        // Obtiene los colores desde los recursos
        val backgroundDark = ContextCompat.getColor(context, R.color.background_dark)
        val whiteColor = ContextCompat.getColor(context, R.color.white)
        val grayMedium = ContextCompat.getColor(context, R.color.gray_medium)
        
        // Si el chip es seleccionable, define los estados de color
        if (isCheckable) {
            val backgroundColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(backgroundDark, whiteColor)
            )
            
            val textColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(whiteColor, backgroundDark)
            )
            
            val strokeColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(backgroundDark, grayMedium)
            )
            
            chip.chipBackgroundColor = backgroundColorStateList
            chip.setTextColor(textColorStateList)
            chip.chipStrokeColor = strokeColorStateList
        } else {
            // Si el chip no es seleccionable, aplica un color fijo
            chip.chipBackgroundColor = ColorStateList.valueOf(backgroundDark)
            chip.setTextColor(whiteColor)
            chip.chipStrokeColor = ColorStateList.valueOf(grayMedium)
        }
    }

    /**
     * Añade un nuevo Chip estilizado a un ChipGroup.
     *
     * @param context El contexto de la aplicación.
     * @param chipGroup El ChipGroup al que se añadirá el chip.
     * @param text El texto que se mostrará en el chip.
     * @param style El estilo a aplicar al chip (por defecto ELEGANT_PURPLE).
     * @param isCheckable Indica si el chip debe ser seleccionable (por defecto true).
     * @return El Chip creado y añadido.
     */
    fun addStyledChip(
        context: Context,
        chipGroup: ChipGroup,
        text: String,
        style: ChipStyle = ChipStyle.ELEGANT_PURPLE,
        isCheckable: Boolean = true
    ): Chip {
        // Crea un nuevo Chip y configura su texto y estado inicial para la animación
        val chip = Chip(context).apply {
            this.text = text
            alpha = 0f // Invisible al inicio
            scaleX = 0.8f // Escala reducida al inicio
            scaleY = 0.8f
        }
        
        // Estiliza el chip y lo añade al ChipGroup
        styleChip(context, chip, style, isCheckable)
        chipGroup.addView(chip)
        
        // Anima la aparición del chip
        animateChipAppearance(chip)
        
        return chip
    }

    /**
     * Añade un Chip para representar un color de prenda a un ChipGroup.
     * Este método permite personalizar el color de fondo, texto y borde del chip
     * basándose en un color de prenda específico.
     *
     * @param context El contexto de la aplicación.
     * @param chipGroup El ChipGroup al que se añadirá el chip.
     * @param text El texto que se mostrará en el chip.
     * @param garmentColor El color de la prenda (entero de color).
     * @param isCheckable Indica si el chip debe ser seleccionable (por defecto true).
     * @return El Chip creado y añadido.
     */
    fun addGarmentColorChip(
        context: Context,
        chipGroup: ChipGroup,
        text: String,
        garmentColor: Int,
        isCheckable: Boolean = true
    ): Chip {
        // Crea un nuevo Chip y configura su texto y estado inicial para la animación
        val chip = Chip(context).apply {
            this.text = text
            alpha = 0f // Invisible al inicio
            scaleX = 0.8f // Escala reducida al inicio
            scaleY = 0.8f
        }
        
        // Obtiene los colores blanco y gris claro desde los recursos
        val whiteColor = ContextCompat.getColor(context, R.color.white)
        val grayLight = ContextCompat.getColor(context, R.color.gray_light)
        
        // Si el chip es seleccionable, define los estados de color para el color de la prenda
        if (isCheckable) {
            val backgroundColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(garmentColor, whiteColor) // Fondo: color de prenda si seleccionado, blanco si no
            )
            
            val textColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(whiteColor, garmentColor) // Texto: blanco si seleccionado, color de prenda si no
            )
            
            val strokeColorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(garmentColor, garmentColor) // Borde: siempre el color de la prenda
            )
            
            // Aplica los estados de color al chip
            chip.chipBackgroundColor = backgroundColorStateList
            chip.setTextColor(textColorStateList)
            chip.chipStrokeColor = strokeColorStateList
        } else {
            // Si el chip no es seleccionable, aplica un color fijo
            chip.chipBackgroundColor = ColorStateList.valueOf(garmentColor)
            chip.setTextColor(whiteColor)
            chip.chipStrokeColor = ColorStateList.valueOf(garmentColor)
        }
        
        // Configura propiedades comunes del Chip
        chip.elevation = 4f
        chip.chipCornerRadius = 20f
        chip.chipStrokeWidth = 2f
        chip.isCheckable = isCheckable
        
        // Configura la animación al hacer clic, añade el chip y anima su aparición
        setupChipClickAnimation(chip)
        chipGroup.addView(chip)
        animateChipAppearance(chip)
        
        return chip
    }

    /**
     * Configura una animación de clic para un Chip.
     * Cuando se hace clic en el chip, este se encoge ligeramente y luego vuelve a su tamaño original.
     *
     * @param chip El Chip al que se le aplicará la animación.
     */
    private fun setupChipClickAnimation(chip: Chip) {
        chip.setOnClickListener { view ->
            // Animaciones para encoger el chip
            val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f)
            val scaleDown2 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f)
            // Animaciones para expandir el chip de nuevo
            val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f)
            val scaleUp2 = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f)
            
            // Duración de las animaciones
            scaleDown.duration = 100
            scaleDown2.duration = 100
            scaleUp.duration = 100
            scaleUp2.duration = 100
            
            // Conjunto de animaciones para reproducir juntas
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(scaleDown, scaleDown2) // Reproduce encogimiento
            animatorSet.playTogether(scaleUp, scaleUp2)     // Reproduce expansión
            animatorSet.interpolator = AccelerateDecelerateInterpolator() // Interpolador para un movimiento suave
            animatorSet.start() // Inicia la animación
        }
    }

    /**
     * Anima la aparición de un ChipGroup, haciéndolo desvanecerse.
     *
     * @param chipGroup El ChipGroup a animar.
     */
    private fun animateChipGroupAppearance(chipGroup: ChipGroup) {
        chipGroup.alpha = 0f // Inicia invisible
        chipGroup.animate()
            .alpha(1f) // Anima a visible
            .setDuration(300) // Duración de la animación
            .setInterpolator(AccelerateDecelerateInterpolator()) // Interpolador
            .start() // Inicia la animación
    }

    /**
     * Anima la aparición de un solo Chip, haciéndolo desvanecerse y escalar.
     *
     * @param chip El Chip a animar.
     */
    private fun animateChipAppearance(chip: Chip) {
        chip.animate()
            .alpha(1f) // Anima a visible
            .scaleX(1f) // Anima a escala normal en X
            .scaleY(1f) // Anima a escala normal en Y
            .setDuration(250) // Duración de la animación
            .setInterpolator(AccelerateDecelerateInterpolator()) // Interpolador
            .start() // Inicia la animación
    }

    /**
     * Anima la aparición escalonada de los Chips dentro de un ChipGroup.
     * Cada chip aparece con un pequeño retraso respecto al anterior.
     *
     * @param chipGroup El ChipGroup cuyos chips se animarán.
     * @param delayBetweenChips El retraso en milisegundos entre la aparición de cada chip (por defecto 100ms).
     */
    fun animateChipsStaggered(chipGroup: ChipGroup, delayBetweenChips: Long = 100) {
        chipGroup.forEachIndexed { index, view ->
            if (view is Chip) {
                view.alpha = 0f // Invisible al inicio
                view.scaleX = 0.8f // Escala reducida al inicio
                view.scaleY = 0.8f
                
                view.animate()
                    .alpha(1f) // Anima a visible
                    .scaleX(1f) // Anima a escala normal en X
                    .scaleY(1f) // Anima a escala normal en Y
                    .setStartDelay(index * delayBetweenChips) // Retraso escalonado
                    .setDuration(250) // Duración de la animación
                    .setInterpolator(AccelerateDecelerateInterpolator()) // Interpolador
                    .start() // Inicia la animación
            }
        }
    }
}
