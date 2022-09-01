package com.app.invite.view.invite

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.invite.R
import com.app.invite.databinding.FragmentInviteBinding
import com.app.invite.model.QRModel
import com.app.invite.model.Role
import com.app.invite.model.TeamModel
import com.app.invite.network.DataState
import com.app.invite.view.home.HomeActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson


class InviteTeamFragment : Fragment() {

    private lateinit var binding: FragmentInviteBinding
    private lateinit var teamModel: TeamModel
    private lateinit var link: QRModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInviteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (::binding.isInitialized) {
            (activity as HomeActivity).supportActionBar?.apply {
                customView.apply {
                    findViewById<TextView>(R.id.tv_menu_back).setOnClickListener {
                        findViewById<TextView>(R.id.tv_menu_title).text =
                            getString(R.string.app_name)
                        it.visibility = View.GONE
                        findNavController().navigate(R.id.gotoHome)
                    }
                }
            }

        }

        initObservers()
        (activity as HomeActivity).getVM().getTeams()
    }

    private fun initObservers() {
        (activity as HomeActivity).getVM().apply {
            dataState.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is DataState.Success -> {
                        showLoading(false)
                        when (response.data.code()) {
                            200 -> {
                                response.data.body()?.apply {

                                    Gson().fromJson(string(), TeamModel::class.java)?.apply {
                                        teamModel = this
                                    }
                                    updateUI()
                                    setupInviteSpinner(
                                        teamModel.id,
                                        teamModel.plan.supporterLimit,
                                        teamModel.plan.supporterLimit - teamModel.members.supporters,
                                        teamModel.plan.memberLimit - teamModel.members.total
                                    )
                                }
                            }
                            else -> {

                            }
                        }
                    }
                    is DataState.Loading -> {
                        showLoading(true)
                    }
                    else -> {
                        showLoading(false)
                    }
                }
            }

            roleState.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is DataState.Success -> {
                        updateButtonLoadingState(true)
                        when (response.data.code()) {
                            200 -> {
                                response.data.body()?.apply {
                                    Gson().fromJson(string(), QRModel::class.java)
                                        ?.apply { link = this }
                                }
                            }
                            else -> {
                            }
                        }
                    }
                    is DataState.Loading -> {
                        updateButtonLoadingState(false)
                    }
                    else -> {
                        updateButtonLoadingState(true)
                    }
                }
            }
        }

        binding.apply {
            bCopyLink.setOnClickListener {
                copyToClipBoard()
            }

            bShareQr.setOnClickListener {
                if (::link.isInitialized) {
                    findNavController().navigate(
                        R.id.gotoQR,
                        bundleOf(getString(R.string.label_qr_link) to link.url)
                    )
                    (activity as HomeActivity).supportActionBar?.apply {
                        customView.apply {
                            findViewById<TextView>(R.id.tv_menu_back).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.tv_menu_title).text =
                                context.getString(R.string.label_my_qr_code)
                        }
                    }
                } else Toast.makeText(requireContext(), "QR Code not found", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun copyToClipBoard() {
        val clipboardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(
            "link", when {
                ::link.isInitialized -> link.url
                else -> ""
            }
        )
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "Text Copied to Clipboard!", Toast.LENGTH_SHORT).show()
    }
    private fun updateUI() {
        if (::teamModel.isInitialized) {
            binding.apply {
                if (teamModel.plan.supporterLimit < 1) {
                    binding.groupSupporters.visibility = View.GONE
                }
                tvCurrentMembersVal.text = teamModel.members.total.toString()
                tvCurrentSupportersVal.text = teamModel.members.supporters.toString()
                tvMembersLimitVal.text = teamModel.plan.memberLimit.toString()
                tvSupportersLimitVal.text = teamModel.plan.supporterLimit.toString()
            }

        }
    }

    private fun setupInviteSpinner(
        id: String,
        supporterLimit: Int,
        availSupporterSlot: Int,
        availMemberSlot: Int
    ) {
        if (availSupporterSlot < 1 && availMemberSlot < 1 || availMemberSlot < 1 && supporterLimit < 1) {
            binding.apply {
                tvInvitePermissions.text = getString(R.string.label_invite_disabled)
                sInvitePermissions.visibility = View.GONE
            }
            return
        }

        val arrayAdapter: ArrayAdapter<Any> = object : ArrayAdapter<Any>(
            requireContext(),
            R.layout.spinner_item_selected,
            when (supporterLimit) {
                0 -> {
                    resources.getStringArray(R.array.invite_permissions_without_supporters).asList()
                }
                else -> {
                    resources.getStringArray(R.array.invite_permissions_with_supporters).asList()
                }
            }
        ) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view1 = super.getDropDownView(position, convertView, parent)
                val tv = view1 as TextView

                if (position != 0) {
                    if (position == 3) {
                        tv.setTextColor(Color.GRAY)
                    } else {
                        tv.setTextColor(
                            requireActivity().resources.getColor(
                                R.color.blue,
                                null
                            )
                        )
                    }
                } else {
                    tv.setTextColor(Color.GRAY)
                }



                return view1
            }

            override fun isEnabled(position: Int): Boolean {
                if (availMemberSlot < 1) {
                    if (position < 3)
                        return false
                }

                if (availSupporterSlot < 1) {
                    if (position > 2) {
                        return false
                    }
                }

                return true
            }
        }
        arrayAdapter.setDropDownViewResource(R.layout.spinner_center_aligned)
        binding.sInvitePermissions.apply {
            adapter = arrayAdapter
            when {
                availSupporterSlot < 1 -> {
                    setSelection(0)
                }
                availMemberSlot < 1 -> {
                    setSelection(3)
                }
            }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapter: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    p3: Long
                ) {
                    (activity as HomeActivity).getVM().updateRole(
                        id,
                        Role(resources.getStringArray(R.array.role_choice)[position]!!)
                    )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }
        }
    }

    private fun showLoading(enabled: Boolean) {
        if (enabled) {
            binding.flLoading.visibility = View.VISIBLE
        } else {
            binding.flLoading.visibility = View.GONE
        }
    }

    private fun updateButtonLoadingState(enabled: Boolean) {
        binding.apply {
            bShareQr.isEnabled = enabled
            bCopyLink.isEnabled = enabled

            if (enabled) {
                bShareQr.alpha = 1.0f
                bCopyLink.alpha = 1.0f
            } else {
                bShareQr.alpha = 0.5f
                bCopyLink.alpha = 0.5f
            }
        }
    }
}