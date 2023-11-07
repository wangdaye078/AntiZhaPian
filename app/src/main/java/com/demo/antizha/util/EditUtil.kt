package com.demo.antizha.util

import android.text.Editable
import com.demo.antizha.interfaces.IEditAfterListener


class EditUtil {
    class TextWatcher internal constructor(val listener: IEditAfterListener) :
        android.text.TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            listener.editLength(editable.toString().length)
        }

        override fun beforeTextChanged(charSequence: CharSequence, i2: Int, i3: Int, i4: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i2: Int, i3: Int, i4: Int) {}
    }

}