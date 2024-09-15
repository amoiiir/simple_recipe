package com.example.simplerecipe.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplerecipe.R
import com.example.simplerecipe.databinding.FragmentProfileBinding
import com.example.simplerecipe.user.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //display username
        getDetails()

        //logout
        initLogout()
    }

    private fun getDetails() {
        //check current users
        val users = firebaseAuth.currentUser?.uid
        if (users != null) {
            db.collection("users").document(users).get().addOnSuccessListener {
                val username = it.getString("username")
                val email = it.getString("email")
                binding.username.text = username
                binding.email.text = email
            }
                .addOnFailureListener {
                    binding.username.text = "User"
                    binding.email.text = "Email"
                }
        }else{
            binding.username.text = "User"
            binding.email.text = "Email"
        }
    }

    private fun initLogout() {
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            //clear caches from the previous user
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }

}