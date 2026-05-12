package io.sc.eppCordova.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sc.eppCordova.data.local.entity.Farmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class CsvParserService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "CsvParserService"
    private var cachedFarmers: List<Farmer>? = null

    suspend fun getFarmerByMobile(mobile: String): Farmer? = withContext(Dispatchers.IO) {
        val cleanMobile = mobile.replace(Regex("[^0-9]"), "")
        // Handle +91 or 91 prefix
        val searchMobile = if (cleanMobile.length > 10 && cleanMobile.startsWith("91")) {
            cleanMobile.substring(cleanMobile.length - 10)
        } else {
            cleanMobile
        }

        if (cachedFarmers != null) {
            return@withContext cachedFarmers?.find { it.mobile == searchMobile }
        }

        val parsedFarmers = mutableListOf<Farmer>()
        try {
            val inputStream = context.assets.open("agristack_mock_farmers.csv")
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            
            // Skip header
            val headerLine = reader.readLine()
            if (headerLine != null) {
                val headers = headerLine.split(",")
                // We could map column indices if we want to be safe, but assuming fixed structure for now.
            }

            var line: String?
            var matchedFarmer: Farmer? = null

            while (reader.readLine().also { line = it } != null) {
                val tokens = line!!.split(",")
                if (tokens.size >= 28) {
                    val mobileCol = tokens[8].trim()
                    
                    val farmer = Farmer(
                        userId = mobileCol,
                        name = tokens[2].trim(),
                        mobile = mobileCol,
                        authToken = "",
                        farmerId = tokens[0].trim(),
                        gender = tokens[3].trim(),
                        dateOfBirth = tokens[4].trim(),
                        category = tokens[6].trim(),
                        aadhaarMasked = tokens[7].trim(),
                        state = tokens[9].trim(),
                        district = tokens[10].trim(),
                        taluka = tokens[11].trim(),
                        village = tokens[12].trim(),
                        pincode = tokens[13].trim(),
                        khasraNumber = tokens[14].trim(),
                        landHoldingHa = tokens[15].trim(),
                        landType = tokens[16].trim(),
                        soilType = tokens[17].trim(),
                        irrigationSource = tokens[18].trim(),
                        primaryCrop = tokens[19].trim(),
                        secondaryCrop = tokens[20].trim(),
                        hasKcc = tokens[22].trim(),
                        kccBank = tokens[23].trim(),
                        pmKisanBeneficiary = tokens[24].trim(),
                        mgnregaLinked = tokens[25].trim(),
                        registrationDate = tokens[26].trim(),
                        status = tokens[27].trim()
                    )
                    
                    parsedFarmers.add(farmer)
                    
                    if (mobileCol == searchMobile) {
                        matchedFarmer = farmer
                    }
                }
            }
            reader.close()
            
            cachedFarmers = parsedFarmers
            Log.d(TAG, "CSV loaded successfully. Parsed ${parsedFarmers.size} records.")
            
            if (matchedFarmer != null) {
                Log.d(TAG, "Lookup success! Found farmer: ${matchedFarmer.name}")
            } else {
                Log.d(TAG, "Lookup failed for mobile: $searchMobile")
            }
            
            return@withContext matchedFarmer
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing CSV: ${e.message}")
            return@withContext null
        }
    }
}
