package equipo.closet.closetvirtual

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val ivBack = findViewById<android.widget.ImageView>(R.id.ivBack)
        ivBack.setOnClickListener {
            finish()
        }

        val etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)

        etFechaNacimiento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                { _, year, monthOfYear, dayOfMonth ->
                    val dat = "$dayOfMonth-${monthOfYear + 1}-$year"
                    etFechaNacimiento.setText(dat)
                }, year, month, day)
            datePickerDialog.show()
        }
    }
}
