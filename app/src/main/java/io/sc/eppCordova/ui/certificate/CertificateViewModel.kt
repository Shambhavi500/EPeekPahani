package io.sc.eppCordova.ui.certificate

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sc.eppCordova.data.local.dao.CropRecordDao
import io.sc.eppCordova.data.local.entity.CropRecord
import io.sc.eppCordova.data.local.entity.Farmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class CertificateData(
    val farmer: Farmer?,
    val cropRecord: CropRecord?,
    val qrBitmap: Bitmap?
)

@HiltViewModel
class CertificateViewModel @Inject constructor(
    private val cropRecordDao: CropRecordDao
) : ViewModel() {

    private val _certificateData = MutableLiveData<CertificateData>()
    val certificateData: LiveData<CertificateData> = _certificateData

    fun generateCertificate(gutNo: String?) {
        if (gutNo == null) return
        viewModelScope.launch {
            val farmer = cropRecordDao.getFarmer()
            val cropRecord = cropRecordDao.getCropRecordByGutNo(gutNo)
            
            val qrContent = "CERT-${farmer?.userId}-${cropRecord?.cropId}"
            val qrBitmap = generateQrBitmap(qrContent)
            
            _certificateData.postValue(CertificateData(farmer, cropRecord, qrBitmap))
        }
    }

    private suspend fun generateQrBitmap(content: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}