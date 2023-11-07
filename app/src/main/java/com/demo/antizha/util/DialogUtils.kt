package com.demo.antizha.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.demo.antizha.ITimerState
import com.demo.antizha.R
import com.demo.antizha.interfaces.IClickListener
import com.demo.antizha.interfaces.IOneClickListener
import com.demo.antizha.ui.BaseDialog
import com.demo.antizha.ui.DownTimer
import com.demo.antizha.ui.ProgressDialogBar


class DialogUtils {
    companion object {
        private var progressDialogBar: ProgressDialogBar? = null

        class DialogDownTimer internal constructor(
            time: Long,
            interval: Long,
            val context: Context,
            val timerState: ITimerState) :
            DownTimer(time, interval) {
            override fun onTimeChang(j: Long) {
                if ((context as Activity).isFinishing) {
                    return
                }
                timerState.timeCount((j / 1000).toInt())
            }

            override fun onTimer() {
                timerState.timeOver()
            }
        }

        class delayEnableTimerState internal constructor(
            val button: Button,
            val buttonTitle: String) :
            ITimerState {
            override fun timeCount(i: Int) {
                val button = button
                button.text =
                    Html.fromHtml(buttonTitle + "&#160;&#160;&#160;<font color=#2946E6>(" + i + "s)</font>",
                        Html.FROM_HTML_MODE_LEGACY)
            }

            override fun timeOver() {
                button.isEnabled = true
                this.button.text = buttonTitle
                this.button.setTextColor(Color.parseColor("#2946E6"))
            }
        }

        fun showNormalDialog(context: Context?,
                             title: String?,
                             subTitle: String?,
                             cancelText: String?,
                             confirmText: String?,
                             iClickListener: IClickListener?): Dialog? {
            return showBtDialog(context,
                title,
                subTitle,
                cancelText,
                confirmText,
                -1,
                -1,
                iClickListener)
        }

        fun showBtDialog(context: Context?,
                         title: String?,
                         subTitle: String?,
                         cancelText: String?,
                         confirmText: String?,
                         cancelColor: Int,
                         confirmColor: Int,
                         iClickListener: IClickListener?): Dialog? {
            if (context == null) {
                return null
            }
            val baseDialog = BaseDialog(context, R.style.base_dialog_style)
            baseDialog.setContentView(R.layout.custom_bt_dialog)
            baseDialog.setGravityLayout(2)
            baseDialog.widthDialogdp = -2.0f
            baseDialog.heightDialogdp = -2.0f
            baseDialog.setCancelable(false)
            baseDialog.setCanceledOnTouchOutside(false)
            baseDialog.initOnCreate()
            baseDialog.show()
            val btnCancel = baseDialog.findViewById(R.id.cancel_btn) as Button
            val btnConfirm = baseDialog.findViewById(R.id.confirm_btn) as Button
            (baseDialog.findViewById(R.id.customdialog_title) as TextView).text = title
            (baseDialog.findViewById(R.id.customdialog_subtitle) as TextView).text = subTitle
            btnCancel.text = cancelText
            btnConfirm.text = confirmText
            if (cancelColor == -1) {
                btnCancel.setTextColor(-14072090)
            } else {
                btnCancel.setTextColor(cancelColor)
            }
            if (confirmColor == -1) {
                btnConfirm.setTextColor(-14072090)
            } else {
                btnConfirm.setTextColor(confirmColor)
            }
            btnCancel.setOnClickListener(object : View.OnClickListener {
                private var mBaseDialog: BaseDialog? = null
                override fun onClick(view: View?) {
                    iClickListener?.cancelBtn()
                    mBaseDialog?.dismiss()
                }

                init {
                    mBaseDialog = baseDialog
                }
            })
            btnConfirm.setOnClickListener(object : View.OnClickListener {
                private var mBaseDialog: BaseDialog? = null
                override fun onClick(view: View?) {
                    iClickListener?.clickOKBtn()
                    mBaseDialog?.dismiss()
                }

                init {
                    mBaseDialog = baseDialog
                }
            })
            return baseDialog
        }

        fun showBtTitleDialog(context: Context?,
                              title: String?,
                              subTitle: String?,
                              cancelText: String?,
                              confirmText: String?,
                              cancelColor: Int,
                              confirmColor: Int,
                              enableCancel: Boolean,
                              iClickListener: IClickListener?): Dialog? {
            if (context == null) {
                return null
            }
            val baseDialog = BaseDialog(context, R.style.base_dialog_style)
            baseDialog.setContentView(R.layout.custom_bt_title_dialog)
            baseDialog.setGravityLayout(2)
            baseDialog.widthDialogdp = -2.0f
            baseDialog.heightDialogdp = -2.0f
            baseDialog.setCancelable(enableCancel)
            baseDialog.setCanceledOnTouchOutside(enableCancel)
            baseDialog.initOnCreate()
            baseDialog.show()
            val btnCancel = baseDialog.findViewById(R.id.cancel_btn) as Button
            val btnConfirm = baseDialog.findViewById(R.id.confirm_btn) as Button
            (baseDialog.findViewById(R.id.customdialog_title) as TextView).text = title
            (baseDialog.findViewById(R.id.customdialog_subtitle) as TextView).text = subTitle
            btnCancel.text = cancelText
            btnConfirm.text = confirmText
            if (!TextUtils.isEmpty(subTitle)) {
                (baseDialog.findViewById(R.id.ll_subtit) as LinearLayout).visibility = View.VISIBLE
            }
            if (cancelColor == -1) {
                btnCancel.setTextColor(-14072090)
            } else {
                btnCancel.setTextColor(cancelColor)
            }
            if (confirmColor == -1) {
                btnConfirm.setTextColor(-14072090)
            } else {
                btnConfirm.setTextColor(confirmColor)
            }
            btnCancel.setOnClickListener(object : View.OnClickListener {
                private var mBaseDialog: BaseDialog? = null
                override fun onClick(view: View?) {
                    iClickListener?.cancelBtn()
                    mBaseDialog?.dismiss()
                }

                init {
                    mBaseDialog = baseDialog
                }
            })
            btnConfirm.setOnClickListener(object : View.OnClickListener {
                private var mBaseDialog: BaseDialog? = null
                override fun onClick(view: View?) {
                    iClickListener?.clickOKBtn()
                    mBaseDialog?.dismiss()
                }

                init {
                    mBaseDialog = baseDialog
                }
            })
            return baseDialog
        }

        fun showOneClickDialog(activity: Activity,
                               title: String,
                               subTitle: String,
                               buttonText: String,
                               iOneClickListener: IOneClickListener?): Dialog? {
            if (activity.isFinishing) {
                return null
            }
            val baseDialog = BaseDialog(activity, R.style.base_dialog_style)
            baseDialog.setContentView(R.layout.custom_dialog_one)
            baseDialog.setGravityLayout(2)
            baseDialog.widthDialog = (-2.0).toFloat()
            baseDialog.heightDialog = (-2.0).toFloat()
            baseDialog.setCancelable(false)
            baseDialog.setCanceledOnTouchOutside(false)
            baseDialog.initOnCreate()
            baseDialog.show()
            val subtitle = baseDialog.findViewById<View>(R.id.subtitle) as TextView
            val button = baseDialog.findViewById<View>(R.id.button) as Button
            button.text = buttonText
            (baseDialog.findViewById<View>(R.id.title) as TextView).text = title
            if (!TextUtils.isEmpty(subTitle)) {
                subtitle.visibility = View.VISIBLE
                subtitle.text = subTitle
            }
            button.setOnClickListener(object : View.OnClickListener {
                private var mBaseDialog: BaseDialog? = null
                override fun onClick(view: View?) {
                    iOneClickListener?.clickOKBtn()
                    mBaseDialog?.dismiss()
                }

                init {
                    mBaseDialog = baseDialog
                }
            })
            return baseDialog
        }

        fun showProgressDialog(str: String?, z: Boolean, activity: Activity?) {
            if (activity != null) {
                try {
                    if (!activity.isFinishing) {
                        if (progressDialogBar == null) {
                            progressDialogBar = ProgressDialogBar.create(activity)!!
                        }
                        progressDialogBar!!.setProgress(str)
                        progressDialogBar!!.setCanceledOnTouchOutside(false)
                        progressDialogBar!!.setCancelable(z)
                        progressDialogBar!!.show()
                    }
                } catch (unused: Exception) {
                }
            }
        }

        fun destroyProgressDialog() {
            try {
                if (progressDialogBar != null) {
                    if (progressDialogBar!!.isShowing) {
                        progressDialogBar!!.dismiss()
                    }
                    progressDialogBar = null
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        fun showInterlinkingDialog(activity: Activity?,
                                   title: String?,
                                   subTitle: String?,
                                   cancelText: String?,
                                   confirmText: String?,
                                   iClickListener: IClickListener?): Dialog? {
            return showInterlinkingDialog(activity,
                title,
                subTitle,
                cancelText,
                confirmText,
                -1,
                -1,
                iClickListener)
        }

        fun showInterlinkingDialog(activity: Activity?,
                                   title: String?,
                                   subTitle: String?,
                                   cancelText: String?,
                                   confirmText: String?,
                                   cancelColor: Int,
                                   confirmColor: Int,
                                   iClickListener: IClickListener?): Dialog? {
            return showInterlinkingDialog(activity,
                title,
                subTitle as CharSequence?,
                false,
                cancelText,
                confirmText,
                cancelColor,
                confirmColor,
                iClickListener)
        }

        fun showInterlinkingDialog(activity: Activity?,
                                   title: String?,
                                   subTitle: CharSequence?,
                                   interlinking: Boolean,
                                   cancelText: String?,
                                   confirmText: String?,
                                   cancelColor: Int,
                                   confirmColor: Int,
                                   iClickListener: IClickListener?): Dialog? {
            if (activity == null || activity.isFinishing) {
                return null
            }
            val baseDialog = BaseDialog(activity, R.style.base_dialog_style)
            baseDialog.setContentView(R.layout.custom_bt_dialog)
            baseDialog.setGravityLayout(2)
            baseDialog.widthDialogdp = -2.0f
            baseDialog.heightDialogdp = -2.0f
            baseDialog.setCancelable(false)
            baseDialog.setCanceledOnTouchOutside(false)
            baseDialog.initOnCreate()
            baseDialog.show()
            val tvTitle = baseDialog.findViewById<TextView>(R.id.customdialog_title)
            val tvSubTitle = baseDialog.findViewById<TextView>(R.id.customdialog_subtitle)
            val btCancel = baseDialog.findViewById<Button>(R.id.cancel_btn)
            val btConfirm = baseDialog.findViewById<Button>(R.id.confirm_btn)
            if (interlinking) {
                tvSubTitle.movementMethod = LinkMovementMethod.getInstance()
                tvSubTitle.text = subTitle
            } else {
                tvSubTitle.text = subTitle
            }
            if (TextUtils.isEmpty(subTitle)) {
                tvSubTitle.visibility = View.GONE
            }
            tvTitle.text = title
            btCancel.text = cancelText
            btConfirm.text = confirmText
            if (cancelColor == -1) {
                btCancel.setTextColor(-14072090)
            } else {
                btCancel.setTextColor(cancelColor)
            }
            if (confirmColor == -1) {
                btConfirm.setTextColor(-14072090)
            } else {
                btConfirm.setTextColor(confirmColor)
            }
            btCancel.setOnClickListener {
                iClickListener?.cancelBtn()
                baseDialog.dismiss()
            }
            btConfirm.setOnClickListener {
                iClickListener?.clickOKBtn()
                baseDialog.dismiss()
            }
            return baseDialog
        }

        fun showDialogAutoClose(activity: Activity?,
                                finishActivity: Boolean,
                                delayClose: Int,
                                title: String?,
                                resID: Int): Dialog? {
            if (activity == null || activity.isFinishing) {
                return null
            }
            val baseDialog = BaseDialog(activity, R.style.base_dialog_style)
            baseDialog.setContentView(R.layout.custom_iv_h_dialog)
            baseDialog.setGravityLayout(2)
            baseDialog.widthDialog = (-2.0).toFloat()
            baseDialog.heightDialog = (-2.0).toFloat()
            baseDialog.setCancelable(true)
            baseDialog.setCanceledOnTouchOutside(true)
            baseDialog.initOnCreate()
            baseDialog.show()
            baseDialog.findViewById<ImageView>(R.id.iv_img).setBackgroundResource(resID)
            baseDialog.findViewById<TextView>(R.id.tv_title).text = title
            Handler(Looper.getMainLooper()).postDelayed({
                baseDialog.dismiss()
                if (finishActivity)
                    activity.finish()
            }, (1000 * delayClose).toLong())
            return baseDialog
        }

        @Synchronized
        private fun createDownTimer(context: Context, i: Int, iTimerState: ITimerState) {
            synchronized(DialogUtils::class.java) {
                DialogDownTimer((i * 1000).toLong(),
                    1000L,
                    context,
                    iTimerState)
            }
        }

        fun createButtonDownTimer(activity: Activity, i: Int, button: Button, str: String) {
            createDownTimer(activity, i, delayEnableTimerState(button, str))
        }

        fun showOneTimeDialog(activity: Activity?,
                              i: Int,
                              phone: String,
                              title: String,
                              subtitle: String,
                              confirmText: String,
                              iClickListener: IClickListener?): Dialog {
            val baseDialog = BaseDialog(activity!!, R.style.base_dialog_style)
            baseDialog.setContentView(R.layout.custom_dialog_one_time)
            baseDialog.setGravityLayout(2)
            baseDialog.setWidthDialog(-2.0)
            baseDialog.setHeightDialog(-2.0)
            baseDialog.setCancelable(false)
            baseDialog.setCanceledOnTouchOutside(false)
            baseDialog.initOnCreate()
            baseDialog.show()
            val tvTitle = baseDialog.findViewById<TextView>(R.id.title)
            val tvSubTitle = baseDialog.findViewById<TextView>(R.id.subtitle)
            val btConfirm = baseDialog.findViewById<Button>(R.id.button)
            tvTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY))
            if (!TextUtils.isEmpty(subtitle)) {
                tvSubTitle.setVisibility(View.VISIBLE)
                tvSubTitle.setText(subtitle)
            }
            tvTitle.setOnClickListener(View.OnClickListener { view ->
                val intent = Intent("android.intent.action.DIAL")
                intent.data = Uri.parse("tel:$phone")
                activity.startActivity(intent)
            })
            btConfirm.setOnClickListener(View.OnClickListener { view ->
                iClickListener?.clickOKBtn()
                baseDialog.dismiss()
            })
            if (i > 0) {
                btConfirm.setText(Html.fromHtml(confirmText + "&#160;&#160;&#160;<font color=#2946E6>(" + i + "s)</font>",
                    Html.FROM_HTML_MODE_LEGACY))
                btConfirm.setEnabled(false)
                btConfirm.setTextColor(Color.parseColor("#999999"))
                createButtonDownTimer(activity, i, btConfirm, confirmText)
            } else {
                btConfirm.setText(confirmText)
                btConfirm.setTextColor(Color.parseColor("#2946E6"))
            }
            return baseDialog
        }
    }
}