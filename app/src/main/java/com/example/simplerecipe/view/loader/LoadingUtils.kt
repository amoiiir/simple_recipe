package com.example.simplerecipe.view.loader

import android.content.Context

class LoadingUtils {

    companion object {
        private var loadingDialog: LoadingDialog? = null
        fun showDialog(
            context: Context?,
            isCancelable: Boolean
        ) {
            hideDialog()
            if (context != null) {
                try {
                    loadingDialog = LoadingDialog(context)
                    loadingDialog?.let { it->
                        it.setCanceledOnTouchOutside(true)
                        it.setCancelable(isCancelable)
                        it.show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun hideDialog() {
            if (loadingDialog!=null && loadingDialog?.isShowing!!) {
                loadingDialog = try {
                    loadingDialog?.dismiss()
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }

    }

}