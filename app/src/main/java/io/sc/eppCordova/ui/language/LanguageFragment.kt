package io.sc.eppCordova.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.data.preferences.UserPreferences
import io.sc.eppCordova.databinding.FragmentLanguageBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment : Fragment() {

    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userPreferences: UserPreferences

    private var selectedLanguageCode = "mr"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore selection from current locale
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val initialLang = if (!currentLocales.isEmpty) {
            when (currentLocales.get(0)?.language) {
                "hi" -> "hi"
                "en" -> "en"
                else -> "mr"
            }
        } else "mr"
        
        updateSelection(initialLang)

        // Wire card clicks using Material 3 native checkable states
        binding.cardMarathi.setOnClickListener { updateSelection("mr") }
        binding.cardHindi.setOnClickListener   { updateSelection("hi") }
        binding.cardEnglish.setOnClickListener { updateSelection("en") }

        // Proceed button
        binding.btnProceed.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                userPreferences.setLanguage(selectedLanguageCode)
                
                // Apply locale
                val appLocale = LocaleListCompat.forLanguageTags(selectedLanguageCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
                
                findNavController().navigate(R.id.action_language_to_login)
            }
        }
    }
    
    private fun updateSelection(langCode: String) {
        selectedLanguageCode = langCode
        
        // Use native MaterialCardView isChecked property for built-in visual state and animations
        binding.cardMarathi.isChecked = (langCode == "mr")
        binding.cardHindi.isChecked = (langCode == "hi")
        binding.cardEnglish.isChecked = (langCode == "en")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
