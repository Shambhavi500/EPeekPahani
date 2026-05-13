package io.sc.eppCordova.ui.myclaims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.sc.eppCordova.R
import io.sc.eppCordova.databinding.FragmentMyClaimsBinding

@AndroidEntryPoint
class MyClaimsFragment : Fragment() {

    private var _binding: FragmentMyClaimsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyClaimsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyClaimsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // Simplify for demo, removing ViewPager2
        val rv = RecyclerView(requireContext())
        rv.layoutManager = LinearLayoutManager(requireContext())
        
        val ll = binding.viewPager.parent as ViewGroup
        ll.removeView(binding.viewPager)
        ll.addView(rv)
        
        val adapter = ClaimAdapter()
        rv.adapter = adapter
        
        viewModel.lossClaims.observe(viewLifecycleOwner) { claims ->
            adapter.submitList(claims)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ClaimAdapter : RecyclerView.Adapter<ClaimAdapter.ViewHolder>() {
        private var list = listOf<io.sc.eppCordova.data.local.entity.LossClaimEntity>()

        fun submitList(newList: List<io.sc.eppCordova.data.local.entity.LossClaimEntity>) {
            list = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = io.sc.eppCordova.databinding.ItemClaimCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.binding.tvClaimRefId.text = item.claimId
            holder.binding.tvClaimDesc.text = "गट: ${item.gatNumber} | ${item.lossType}"
            holder.binding.tvClaimDate.text = "तारीख: ${item.incidentDate}"
        }

        override fun getItemCount() = list.size

        class ViewHolder(val binding: io.sc.eppCordova.databinding.ItemClaimCardBinding) : RecyclerView.ViewHolder(binding.root)
    }
}
