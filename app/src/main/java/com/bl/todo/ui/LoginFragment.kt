package com.bl.todo.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bl.todo.R
import com.bl.todo.databinding.LoginFragmentBinding
import com.bl.todo.services.FirebaseAuthentication
import com.bl.todo.services.FirebaseDatabase
import com.bl.todo.util.Utilities

class LoginFragment : Fragment(R.layout.login_fragment){
    private lateinit var binding: LoginFragmentBinding
    private lateinit var dialog  : Dialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)

        binding.loginRegister.setOnClickListener {
            Toast.makeText(requireContext(),"button pressed",Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId , SignUpFragment()).commit()
        }

        binding.loginSubmit.setOnClickListener {
            dialog.show()
            login()
        }



    }

    private fun login() {
        var email = binding.loginEmail
        var password = binding.loginPassword
        if(Utilities.loginCredentialsValidator(email,password)){
            FirebaseAuthentication.loginWithEmailAndPassword(email.text.toString(),password.text.toString()){status, user ->
                if(!status){
                    dialog.dismiss()
                    Toast.makeText(requireContext(),"Account has not been created",Toast.LENGTH_SHORT).show()
                }else{
                    FirebaseDatabase.getUserData(user!!.uid){status,bundle ->
                        if(!status){
                            Log.e("DatabaseError","read failed")
                            dialog.dismiss()
                        }else{
                            var profileObj = ProfileFragment()
                            profileObj.arguments = bundle
                            dialog.dismiss()
                            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerId,profileObj).commit()
                        }
                    }
                }
            }
        }
    }
}