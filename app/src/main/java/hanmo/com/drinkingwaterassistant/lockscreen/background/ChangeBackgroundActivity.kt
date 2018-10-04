package hanmo.com.drinkingwaterassistant.lockscreen.background

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.WindowManager
import com.jakewharton.rxbinding2.view.clicks
import com.theartofdev.edmodo.cropper.CropImage
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.util.DLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_changebackground.*
import org.jetbrains.anko.contentView
import permissions.dispatcher.*


/**
 * Created by hanmo on 2018. 9. 11..
 */
@RuntimePermissions
class ChangeBackgroundActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private val READ_REQUEST_CODE = 42

    companion object {

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, ChangeBackgroundActivity::class.java)
            return intent
        }
    }

    private val onItemClickListener = object : ChangeBackgroundAdapter.OnItemClickListener {
        override fun onItemClick(view: Array<String>, position: Int) {
            if (position == 0) {
                useImageCropWithPermissionCheck()
            } else {
                RealmHelper.instance.updateBackgroundImage(view[position], true)
                finish()
            }
        }
    }

    @NeedsPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun useImageCrop() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    @OnShowRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showRationaleForCamera(request: PermissionRequest) {
        AlertDialog.Builder(this@ChangeBackgroundActivity)
                .setMessage(R.string.permission_crop_rationale)
                .setPositiveButton(R.string.button_allow, { dialog, button -> request.proceed() })
                .setNegativeButton(R.string.button_deny, { dialog, button -> request.cancel() })
                .show()
    }

    @OnPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showDeniedForCrop() {
        Snackbar.make(contentView!!, getString(R.string.storagePermissionDenied), Snackbar.LENGTH_LONG).show()
    }

    @OnNeverAskAgain(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun showNeverAskForCrop() {
        Snackbar.make(contentView!!, getString(R.string.storagePermissionNeverask), Snackbar.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === READ_REQUEST_CODE && resultCode === Activity.RESULT_OK) {
            var uri: Uri? = null
            if (data != null) {
                uri = data.data
                CropImage.activity(uri)
                        .start(this@ChangeBackgroundActivity)
            }
        }
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                val resultUri = result.uri
                DLog.e(resultUri.toString())
                RealmHelper.instance.updateBackgroundImage(resultUri.toString(), false)
                finish()
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                DLog.e("CropImage Exception : $error")
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)

    }
    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        super.onAttachedToWindow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changebackground)

        setCancelButton()
        setBackgroundList()
    }


    private fun setCancelButton() {
        cancelButton.clicks()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    finish()
                }.apply { compositeDisposable.add(this) }
    }

    private fun setBackgroundList() {

        with(changeBackgroundList) {
            setHasFixedSize(true)
            staggeredGridLayoutManager = android.support.v7.widget.StaggeredGridLayoutManager(3, android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL)
            layoutManager = staggeredGridLayoutManager
            val changeBackgroundAdapter = ChangeBackgroundAdapter()
            changeBackgroundAdapter.setOnItemClickListener(onItemClickListener)
            adapter = changeBackgroundAdapter
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.slide_out_right)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}