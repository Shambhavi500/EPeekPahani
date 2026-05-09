package io.sc.eppCordova.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import io.sc.eppCordova.R

class LanguageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_language, container, false)
        
        val cardMarathi = view.findViewById<MaterialCardView>(R.id.card_marathi)
        val cardHindi = view.findViewById<MaterialCardView>(R.id.card_hindi)
        val cardEnglish = view.findViewById<MaterialCardView>(R.id.card_english)
        
        val rbMarathi = view.findViewById<RadioButton>(R.id.rb_marathi)
        val rbHindi = view.findViewById<RadioButton>(R.id.rb_hindi)
        val rbEnglish = view.findViewById<RadioButton>(R.id.rb_english)
        
        fun updateSelection(selectedCard: MaterialCardView, selectedRb: RadioButton) {
            cardMarathi.isSelected = false
            cardHindi.isSelected = false
            cardEnglish.isSelected = false
            
            rbMarathi.isChecked = false
            rbHindi.isChecked = false
            rbEnglish.isChecked = false
            
            selectedCard.isSelected = true
            selectedRb.isChecked = true
        }
        
        cardMarathi.setOnClickListener { updateSelection(cardMarathi, rbMarathi) }
        cardHindi.setOnClickListener { updateSelection(cardHindi, rbHindi) }
        cardEnglish.setOnClickListener { updateSelection(cardEnglish, rbEnglish) }
        
        // Initial setup
        updateSelection(cardMarathi, rbMarathi)
        
        view.findViewById<View>(R.id.btn_next).setOnClickListener {
            findNavController().navigate(R.id.action_language_to_login)
        }
        
        return view
    }
}
