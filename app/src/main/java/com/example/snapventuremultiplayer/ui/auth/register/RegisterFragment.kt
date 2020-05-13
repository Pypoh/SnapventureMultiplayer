package com.example.snapventuremultiplayer.ui.auth.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.snapventuremultiplayer.R
import com.example.snapventuremultiplayer.databinding.FragmentRegisterBinding
import com.example.snapventuremultiplayer.repository.datasource.remote.auth.register.RegisterRepoImpl
import com.example.snapventuremultiplayer.ui.MainActivity
import com.example.snapventuremultiplayer.ui.auth.register.domain.RegisterImpl
import com.example.snapventuremultiplayer.utils.viewobject.Resource
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import toast

class RegisterFragment : Fragment() {

    private lateinit var alertDialog: AlertDialog

    private lateinit var registerDataBinding: FragmentRegisterBinding

    private val registerViewModel: RegisterViewModel by lazy {
        ViewModelProvider(
            this,
            RegisterVMFactory(RegisterImpl(RegisterRepoImpl()))
        ).get(RegisterViewModel::class.java)
    }

    private lateinit var registerButton: MaterialButton

    private lateinit var toLoginButton: MaterialButton

    private lateinit var emailInput: TextInputLayout
    private lateinit var nameInput: TextInputLayout
    private lateinit var passwordInput: TextInputLayout
    private lateinit var passwordConfInput: TextInputLayout

    private var status: Boolean? = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Setup Data Binding
        registerDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerDataBinding.registerViewModel = registerViewModel

        // View Binding
        setupViewBinding(registerDataBinding.root)

        // Init Progress Dialog
        initProgressDialog()

        // Setup Login Button onClick Listener
        setupButtonListener()

        return registerDataBinding.root
    }

    private fun setupViewBinding(root: View) {
        registerButton = root.findViewById(R.id.btn_register_register)
        toLoginButton = root.findViewById(R.id.btn_to_login)
        emailInput = root.findViewById(R.id.email_text_input)
        nameInput = root.findViewById(R.id.nama_text_input)
        passwordInput = root.findViewById(R.id.pass_text_input)
        passwordConfInput = root.findViewById(R.id.pass_conf_text_input)
    }

    private fun setupButtonListener() {
        registerButton.setOnClickListener {

            // First Null Check
            if (setInputNull() == true) {
                handleRegister()
            } else {
                setInputObserver()
            }
        }

        toLoginButton.setOnClickListener {
            moveToLogin()
        }
    }

    private fun handleRegister() {
        // Handle Register
        registerViewModel.registerWithEmailAndPassword()
        registerViewModel.result.observe(viewLifecycleOwner, Observer { task ->
            when (task) {
                is Resource.Loading -> {
                    if (!alertDialog.isShowing) alertDialog.show()
                }

                is Resource.Success -> {
                    // TODO: Intent ke main
                    requireContext().toast("Success")
                    if (alertDialog.isShowing) alertDialog.dismiss()
                    registerViewModel.insertDataToDatabase()
                    intentToMain()
                }

                is Resource.Failure -> {
                    if (alertDialog.isShowing) alertDialog.dismiss()
                    requireContext().toast(task.throwable.message.toString())
                }

                else -> {
                    // do nothing
                    requireContext().toast(task.toString())
                }
            }
        })
    }

    private fun intentToMain() {
        val intent = Intent(this.context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun moveToLogin() {
        requireView().findNavController().navigate(R.id.navigation_login)
    }

    private fun setInputNull() : Boolean? {
        // Init new state of status
        status = true

        if (registerViewModel.email.value.isNullOrEmpty()) {
            emailInput.error = "Email tidak boleh kosong"
            status = false
        }

        if (registerViewModel.name.value.isNullOrEmpty()) {
            nameInput.error = "Nama tidak boleh kosong"
            status = false
        }

        if (registerViewModel.password.value.isNullOrEmpty()) {
            passwordInput.error = "Password tidak boleh kosong"
            status = false
        }

        if (registerViewModel.passwordConf.value.isNullOrEmpty()) {
            passwordConfInput.error = "Konfirmasi Password tidak boleh kosong"
            status = false
        }

        if (!registerViewModel.password.value.equals(registerViewModel.passwordConf.value)) {
            passwordConfInput.error = "Password tidak sama"
            status = false
        }

        return status
    }

    private fun setInputObserver() {
       if (status == false) {
           return
       }

        // Set Observer
        registerViewModel.email.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                emailInput.error = "Email tidak boleh kosong"
            } else {
                emailInput.error = null
            }
        })

        registerViewModel.name.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                nameInput.error = "Nama tidak boleh kosong"
            } else {
                nameInput.error = null
            }
        })

        registerViewModel.password.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                passwordInput.error = "Password tidak boleh kosong"
            } else {
                passwordInput.error = null
            }
        })

        registerViewModel.passwordConf.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                passwordConfInput.error = "Konfirmasi Password tidak boleh kosong"
            } else {
                passwordConfInput.error = null
            }
        })
    }

    private fun initProgressDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        alertDialog = dialogBuilder.create()
    }

}