package io.sc.eppCordova.ui.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentOtpBinding
import io.sc.eppCordova.ui.SharedViewModel

@AndroidEntryPoint
class OtpFragment : Fragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mobile = sharedViewModel.farmerState.value?.mobile ?: ""
        if (mobile.length == 10) {
            binding.tvMobileInfo.text = "OTP पाठवला: XXXXXX${mobile.substring(6)}"
        }

        setupOtpInputs()
        startResendTimer()

        binding.btnVerify.setOnClickListener {
            val otp = "${binding.otp1.text}${binding.otp2.text}${binding.otp3.text}${binding.otp4.text}"
            if (otp == "1234" || otp.length == 4) { // Demo mode allows 1234 or any 4 digit
                findNavController().navigate(R.id.action_otp_to_adminUnit)
            } else {
                Snackbar.make(view, "अवैध OTP", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupOtpInputs() {
        val editTexts = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4)
        for (i in 0..2) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) editTexts[i + 1].requestFocus()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun startResendTimer() {
        binding.tvResend.isEnabled = false
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _binding?.tvResend?.text = "OTP पुन्हा पाठवा (${millisUntilFinished / 1000}s)"
            }
            override fun onFinish() {
                _binding?.tvResend?.isEnabled = true
                _binding?.tvResend?.text = "OTP पुन्हा पाठवा"
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        countDownTimer = null
        _binding = null
    }
}
