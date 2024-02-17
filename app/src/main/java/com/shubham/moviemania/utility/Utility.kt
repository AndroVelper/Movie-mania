package com.shubham.moviemania.utility


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.shubham.moviemania.R
import com.shubham.moviemania.databinding.ItemFavMovieDataBinding
import com.shubham.moviemania.models.MovieData


class Utility {

    companion object {
        val movieDataArray: ArrayList<MovieData> = ArrayList()


        const val isLongLengthToast = true
        const val isShortLengthToast = false

        fun debugLogs(msg: String) {
            Log.d("MovieMania", "debugLogs: $msg")
        }

        fun showToast(context: Context, msg: String, isLongToast: Boolean) {
            if (isLongToast) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }


        fun Fragment.shareItemData(data: MovieData, context: Context) {
            val shareData = Uri.parse("mailto:shubham@chicmic.co.in")
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = shareData
            val message =
                "Check out this amazing movie :\n  ${data.title} \n Released in : ${data.year}"

            intent.putExtra(Intent.EXTRA_SUBJECT, data.title)
            intent.putExtra(Intent.EXTRA_TEXT, message)

            /*
            * To prevent the crash when we don't have the intents i have checked whether we have the particular app
            * or not for this.
            * */
            val activities = context.packageManager.queryIntentActivities(intent, 0)
            val isIntentSafe = activities.isNotEmpty()

            /*
            * if we have the app then we will show the app to user else we would try with new intent and work accordingly

            * */
            if (isIntentSafe) {
                startActivity(intent)
            } else {

                try {
                    val newIntent = Intent(Intent.ACTION_SEND)
                    newIntent.type = "message/rfc822"
                    newIntent.putExtra(Intent.EXTRA_EMAIL, "shubham@chicmic.co.in")
                    newIntent.putExtra(Intent.EXTRA_SUBJECT, data.title)
                    newIntent.putExtra(Intent.EXTRA_TEXT, message)
                    startActivity(newIntent)
                } catch (e: Exception) {
                    // Handle the case when no email app is found, for example, show a message to the user
                    showToast(context, "No email app found", true)
                    e.printStackTrace()
                }

            }
        }


        fun Context.makeTransactions(manager : FragmentManager , fragment : Fragment){
            val fragmentTransaction = manager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        fun Fragment.changeStatusBarColor(window: Window, context: Context, color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.statusBarColor = ContextCompat.getColor(context, color)

        }

        fun Activity.shareUs() {
            val shareData = Uri.parse("mailto:shubham@chicmic.co.in")
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = shareData

            /*
            * To prevent the crash when we don't have the intents i have checked whether we have the particular app
            * or not for this.
            * */
            val activities = packageManager.queryIntentActivities(intent, 0)
            val isIntentSafe = activities.isNotEmpty()

            /*
            * if we have the app then we will show the app to user else we would try with new intent and work accordingly

            * */
            if (isIntentSafe) {
                startActivity(intent)
            } else {

                try {
                    val newIntent = Intent(Intent.ACTION_SEND)
                    newIntent.type = "message/rfc822"
                    newIntent.putExtra(Intent.EXTRA_EMAIL, "shubham@chicmic.co.in")
                    startActivity(newIntent)
                } catch (e: Exception) {
                    // Handle the case when no email app is found, for example, show a message to the user
                    showToast(this, "No email app found", true)
                    e.printStackTrace()
                }

            }
        }

        fun Fragment.makeKeyboardDown(context :Context){
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        fun Fragment.exceptionHandler(className: String, tryBlock: () -> Unit?): Any? {
            try {
                return tryBlock()
            } catch (e: Exception) {
                debugLogs("$className exception occur due to this class")
                e.printStackTrace()
            } finally {
                debugLogs("Api call has been executed")
            }
            return null
        }

        fun showCustomDialog(
            removeButtonText : String,
            cancelButtonText : String,
            title: String,
            message: String,
            layoutInflater: LayoutInflater,
            context: Context,
            position: Int?,
            favMovieList: ArrayList<MovieData>?,
            task: (data: MovieData) -> Unit,
            task2: () -> Unit,
        ) {
            val data = position?.let { favMovieList?.get(it) }
            val dialog = AlertDialog.Builder(context, R.style.customTheme)

            val favBinding = ItemFavMovieDataBinding.inflate(layoutInflater)
            favBinding.dialogTitle.text = title
            favBinding.cancelButton.text = cancelButtonText
            favBinding.removeFromList.text = removeButtonText
            Glide.with(context).load(data?.poster)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.no_result_found))
                .into(favBinding.dialogImage)

            favBinding.dialogMessage.text = message
            dialog.setView(favBinding.root)
            val perfectDialog = dialog.create()
            favBinding.cancelButton.setOnClickListener {
                perfectDialog.dismiss()
            }
            favBinding.removeFromList.setOnClickListener {
                perfectDialog.dismiss()
                if (data != null) {
                    task(data)
                }
                else{
                    task2()
                }

            }

            perfectDialog.setCancelable(false)
            perfectDialog.show()

        }


    }


}