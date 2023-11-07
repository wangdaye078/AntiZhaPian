package com.demo.antizha.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import cn.qqtheme.framework.entity.City
import cn.qqtheme.framework.entity.County
import cn.qqtheme.framework.entity.Province
import com.demo.antizha.BuildConfig
import com.demo.antizha.UserInfoBean
import com.demo.antizha.databinding.ActivityPersonaInfolBinding
import com.demo.antizha.util.*
import com.demo.antizha.util.NotchUtils.dp2px
import com.hjq.toast.ToastUtils
import qiu.niorgai.StatusBarCompat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class PersonalInfoAddActivity : BaseActivity() {
    companion object {
        const val pageBase = "Base"
        const val pageArea = "Area"
        const val pageAreaDetail = "AreaDetail"

        //前，后字符数，一共字符数，星号数，总长
        val etSet = arrayOf(arrayOf(1, 1, 2, 16, 18), arrayOf(3, 2, 5, 6, 11))
    }

    private var pageType: String = ""
    private var adcode: String = ""
    private var provinces: ArrayList<Province> = ArrayList()
    private lateinit var infoBinding: ActivityPersonaInfolBinding

    inner class AddressListener : AddressBean.HiAddressListener() {
        override fun onAddressPicked(province: Province?, city: City?, county: County?) {
            infoBinding.etArea.text = "${province?.name}.${city?.name}.${county?.name}"
            if (county != null) {
                adcode = county.areaId
            }
        }
    }

    /*AddressPicker新版本代码
    override fun onAddressPicked(
        province: ProvinceEntity?,
        city: CityEntity?,
        county: CountyEntity?
    ) {
        personaInfolBinding.etArea.text = "${province?.name}.${city?.name}.${county?.name}"
        if (county != null) {
            adcode = county.code
        }
    }
    */
    override fun initPage() {
        infoBinding = ActivityPersonaInfolBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)
        StatusBarCompat.translucentStatusBar(this as Activity, true, false)
        pageType = intent.getStringExtra("from_page_type").toString()
        infoBinding.etIdNum.tag = 0
        infoBinding.etPhoneNum.tag = 1
        initPages()


        infoBinding.piTitle.ivBack.setOnClickListener {
            finish()
        }
        infoBinding.etArea.setOnClickListener {
            /*
            val picker = AddressPicker(this)
            picker.setAddressMode(AddressMode.PROVINCE_CITY_COUNTY)
            picker.setOnAddressPickedListener(this)
            picker.cancelView.setTextColor(ContextCompat.getColor(this, R.color.colorGray))
            picker.okView.setTextColor(ContextCompat.getColor(this, R.color.black))
            picker.topLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlue1))
            val wheelLayout: LinkageWheelLayout = picker.wheelLayout
            val colorGray: Int = ContextCompat.getColor(this, R.color.colorGray)
            wheelLayout.firstWheelView.selectedTextColor = colorGray
            wheelLayout.secondWheelView.selectedTextColor = colorGray
            wheelLayout.thirdWheelView.selectedTextColor = colorGray
            wheelLayout.firstWheelView.indicatorColor = colorGray
            wheelLayout.secondWheelView.indicatorColor = colorGray
            wheelLayout.thirdWheelView.indicatorColor = colorGray
            wheelLayout.firstWheelView.layoutParams =
                LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.25F)
            wheelLayout.secondWheelView.layoutParams =
                LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.5F)
            wheelLayout.thirdWheelView.layoutParams =
                LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.25F)
            val region: String = personaInfolBinding.etArea.text.toString()
            if (!TextUtils.isEmpty(region)) {
                val regions = TextUtils.split(region, "\\.")
                if (regions.size == 3) {
                    picker.setDefaultValue(regions[0], regions[1], regions[2])
                }
            }
            picker.show()
            */
            val picker =
                AddressBean.createAddressPicker(
                    this,
                    infoBinding.etArea.text.toString(),
                    false,
                    AddressListener()
                )
            picker.show()
        }
        infoBinding.etAccountNum.setOnClickListener {
            if (infoBinding.etAccountNum.text.toString().isEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("关于账号ID")
                builder.setMessage(
                    "必须先使用官方软件注册一个账号，然后在手机的/data/data/com.hicorenational.antifraud/shared_prefs/note_national.xml" +
                            "中找到类似提示的字符串，当然这些可以在虚拟机中执行，如果在手机里操作的话，你还得有root权限，当然你也可以选择不填，只是部分功能会受限，" +
                            "你也可以随机生成一个，某些功能可能也能用"
                )
                builder.setPositiveButton("确定") { _, _ ->
                    infoBinding.etAccountNum.inputType = InputType.TYPE_CLASS_TEXT
                }
                builder.setNegativeButton("取消") { _, _ ->
                }
                builder.setNeutralButton("随机生成") { _, _ ->
                    var account = ""
                    val strtemplate = "1111aaaa-aaaa-aaaa-aaaa-aaaa11111111"
                    val numMap = "1234567890"
                    val hexMap = "1234567890abcdefabcdef"
                    for (element in strtemplate) {
                        when (element) {
                            '1' -> account += numMap[(numMap.indices).random()]
                            'a' -> account += hexMap[(hexMap.indices).random()]
                            '-' -> account += "-"
                        }
                    }
                    infoBinding.etAccountNum.setText(account)
                }
                builder.show()
            }
        }
        infoBinding.btnClearpermiss.setOnClickListener {
            SpUtils.setValue(SpUtils.primissAuto, false)
            SpUtils.setValue(SpUtils.primissPower, false)
            SpUtils.setValue(SpUtils.primissLock, false)
            var isClosed = AppUtil.checkPermission(this, false)
            if (isClosed)
                isClosed = PictureUtil.checkPermission(this, false)
            //if (isClosed)
            //    isClosed = "检测下一个"
            if (isClosed)
                ToastUtils.show("权限已经全部关闭")
        }
        val editFocusChangeListener = OnEditFocusChangeListener()
        infoBinding.etIdNum.onFocusChangeListener = editFocusChangeListener
        infoBinding.etPhoneNum.onFocusChangeListener = editFocusChangeListener
        infoBinding.btnConfirm.setOnClickListener {
            when (pageType) {
                pageBase -> {
                    val name: String = infoBinding.etName.text.toString()
                    val id: String = editCompleteToSimplify(infoBinding.etIdNum, false)
                    val mobileNumber: String = editCompleteToSimplify(infoBinding.etPhoneNum, false)
                    if (!TextUtils.isEmpty(id) && !stringIsUserID(id) && id.length != 2) {
                        Toast.makeText(this@PersonalInfoAddActivity, "身份证格式不对", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    if (!TextUtils.isEmpty(mobileNumber) && !stringIsMobileNumber(mobileNumber) && mobileNumber.length != 5) {
                        Toast.makeText(this@PersonalInfoAddActivity, "电话格式不对", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    val accountId: String = infoBinding.etAccountNum.text.toString()
                    var changed = false
                    if (name != UserInfoBean.name || id != UserInfoBean.id || mobileNumber != UserInfoBean.mobileNumber) {
                        UserInfoBean.name = name
                        UserInfoBean.id = id
                        UserInfoBean.mobileNumber = mobileNumber
                        changed = true
                    }
                    if (accountId != UserInfoBean.accountId) {
                        UserInfoBean.accountId = accountId
                        changed = true
                    }
                    val appVersion: String = infoBinding.etAppVersion.text.toString()
                    if (appVersion != UserInfoBean.version) {
                        UserInfoBean.version = appVersion
                        changed = true
                    }
                    val imei = Settings.System.getString(
                        applicationContext?.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                    val crcimei = AESUtil.byteArray2HexStr(CRC64.digest(imei.toByteArray()).bytes)
                    val useorigimei = infoBinding.sbOriginalimei.isChecked
                    if (useorigimei && imei != UserInfoBean.imei) {
                        UserInfoBean.imei = imei
                        UserInfoBean.useorigimei = useorigimei
                        changed = true
                    }
                    if (!useorigimei && crcimei != UserInfoBean.imei) {
                        UserInfoBean.imei = crcimei
                        UserInfoBean.useorigimei = useorigimei
                        changed = true
                    }
                    if (changed)
                        UserInfoBean.commit()
                }
                pageArea -> {
                    val region: String = infoBinding.etArea.text.toString()
                    if (region != UserInfoBean.region) {
                        UserInfoBean.region = region
                        UserInfoBean.adcode = adcode
                        UserInfoBean.commit()
                    }
                }
                pageAreaDetail -> {
                    val address: String = infoBinding.etAddress.text.toString()
                    if (address != UserInfoBean.addr) {
                        UserInfoBean.addr = address
                        UserInfoBean.commit()
                    }
                }
            }
            finish()
        }
    }

    private fun stringIsEmail(str: String): Boolean {
        return Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$").matcher(str)
            .matches()
    }

    private fun stringIsUserID(str: String): Boolean {
        return Pattern.compile("^[1-6][0-9X]$").matcher(str).matches()
    }

    private fun stringIsMobileNumber(str: String): Boolean {
        return Pattern.compile("^1[0-9]{4}$").matcher(str).matches()
    }

    private fun editSimplifyToComplete(v: View, resetText: Boolean): String {
        val tag = v.tag as Int
        var str = (v as EditText).text.toString()
        if (str.length == etSet[tag][2]) {
            str = str.substring(
                0,
                etSet[tag][0]
            ) + "*".repeat(etSet[tag][3]) + str.substring(str.length - etSet[tag][1])
            if (resetText)
                v.setText(str)
        }
        return str
    }

    private fun editCompleteToSimplify(v: View, resetText: Boolean): String {
        val tag = v.tag as Int
        var str = (v as EditText).text.toString()
        if (str.length == etSet[tag][4]) {
            str = str.substring(0, etSet[tag][0]) + str.substring(str.length - etSet[tag][1])
            if (resetText)
                v.setText(str)
        }
        return str
    }

    inner class OnEditFocusChangeListener : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (hasFocus)
                editCompleteToSimplify(v!!, true)
            else
                editSimplifyToComplete(v!!, true)
        }
    }

    private fun initPages() {
        when (pageType) {
            pageBase -> {
                infoBinding.piTitle.tvTitle.text = "基础信息"
                infoBinding.clBaseCont.visibility = View.VISIBLE
                infoBinding.etName.setText(UserInfoBean.name)
                infoBinding.etIdNum.setText(UserInfoBean.id)
                editSimplifyToComplete(infoBinding.etIdNum, true)
                infoBinding.etPhoneNum.setText(UserInfoBean.mobileNumber)
                editSimplifyToComplete(infoBinding.etPhoneNum, true)
                infoBinding.etAccountNum.setText(UserInfoBean.accountId)
                infoBinding.etAppVersion.setText(UserInfoBean.version)
                infoBinding.sbOriginalimei.isChecked = UserInfoBean.useorigimei
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                infoBinding.tvApktime.text = "编译时间 ${sdf.format(Date(BuildConfig.BUILD_TIME))}"
                if (TextUtils.isEmpty(UserInfoBean.accountId))
                    infoBinding.etAccountNum.inputType = InputType.TYPE_NULL

                val params: ViewGroup.MarginLayoutParams =
                    infoBinding.btnConfirm.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = dp2px(350F)
                infoBinding.btnConfirm.layoutParams = params
                infoBinding.btnConfirm.requestLayout()
            }
            pageArea -> {
                infoBinding.piTitle.tvTitle.text = "地址"
                infoBinding.clAreaCont.visibility = View.VISIBLE
                infoBinding.etArea.text = UserInfoBean.region
                if (provinces.size == 0)
                    Handler(Looper.getMainLooper()).postDelayed({
                        provinces = AddressBean.getProvince()
                    }, 10)
            }
            pageAreaDetail -> {
                infoBinding.piTitle.tvTitle.text = "详细地址"
                infoBinding.clAreaDetailContent.visibility = View.VISIBLE
                infoBinding.etAddress.setText(UserInfoBean.addr)
            }
        }
    }
}