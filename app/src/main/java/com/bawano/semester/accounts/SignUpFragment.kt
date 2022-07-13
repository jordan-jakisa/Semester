package com.bawano.semester.accounts

import android.app.AlertDialog
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bawano.semester.R
import com.bawano.semester.databinding.FragmentSignUpBinding
import com.bawano.semester.models.User
import com.bawano.semester.utils.*
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest


class SignUpFragment : Fragment() {
    private lateinit var b: FragmentSignUpBinding
    private val args: SignUpFragmentArgs by navArgs()

    private val ONE_TAP = 2
    private var showOneTapUI = true

    private val auth = Fp.authInstance()
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleSignInOneTap: ActivityResultLauncher<IntentSenderRequest>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        b = FragmentSignUpBinding.inflate(inflater)
        animateViews()
        onViewClick()
        return b.root
    }


    private fun animateViews() {
        b.nameLayout.slideInFromLeft()
        b.shapeableImageView.translateYFromTo(from = 200F, to = 0F)
        b.emailLayout.slideInFromLeft(delay = 200L)
        b.etPasswordLayout.slideInFromLeft(delay = 300L)
        b.etRetypeLayout.slideInFromLeft(delay = 400L)
        b.signUpBtn.slideInFromDown(delay = 1000L)
        b.googleSignInButton.slideInFromDown(delay = 1000L)
        b.v1.fadeIn(delay = 1300L)
        b.v2.fadeIn(delay = 1300L)
        b.or.fadeIn(delay = 1300L)
        b.haveAccount.fadeIn(delay = 1300L)
        b.disclaimer.fadeIn(delay = 1300L)

    }

    private fun onViewClick() {
        args.email?.let { b.emailEt.setText(args.email) }
        args.password?.let {
            b.emailEt.setText(args.email)
            b.etPassword.setText(args.password)
            b.etRetype.setText(args.password)
        }
        b.haveAccount.addLinksToText(Pair("Sign In", View.OnClickListener {
            val email = b.emailEt.text.toString()
            val password = b.etPassword.text.toString()
            val action =
                SignUpFragmentDirections.actionSignUpFragmentToSignInFragment(email, password)
            findNavController().navigate(action)
        }))
        b.disclaimer.addLinksToText(
            // TODO: privacy policy and terms and conditions
        )
        b.googleSignInButton.setOnClickListener {
            if(showOneTapUI) startOneTapSignInFlow()
        }

        b.signUpBtn.setOnClickListener {
            if (isValidInfo()) createAccount()
        }
    }

    private fun startOneTapSignInFlow() {
        b.progressBar.visibility = View.VISIBLE
        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) {
                try {
                    val intentSenderRequest: IntentSenderRequest = IntentSenderRequest
                        .Builder(it.pendingIntent.intentSender)
                        .build()
                    googleSignInOneTap.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    requireContext().toast("failed to access one tap log in")
                }
            }
            .addOnFailureListener(requireActivity()) { }
    }

    private fun isValidInfo(): Boolean {
        var valid = true
        val email = b.emailEt.text.toString()
        val name = b.nameEt.text.toString()
        val password = b.etPassword.text.toString()
        val rePassword = b.etRetype.text.toString()


        if (name.isEmpty()) {
            b.nameLayout.error = "Enter your Name Address"
            valid = false
        }

        if (email.isEmpty()) {
            b.emailLayout.error = "Enter your email Address"
            valid = false
        }
        if (password.isEmpty()) {
            b.etPasswordLayout.error = "Enter a password"
            valid = false
        }
        if (password.length < 8) {
            b.etPasswordLayout.error = "Password too Short"
            valid = false
        }

        if (password != rePassword || rePassword.isEmpty()) {
            b.etPasswordLayout.error = "Passwords didn't match"
            b.etRetypeLayout.error = "Passwords didn't match"
            b.etPassword.setText("")
            b.etRetype.setText("")
            valid = false
        }
        return valid
    }

    private fun createAccount() {
        val email = b.emailEt.text.toString()
        val password = b.etPassword.text.toString()
        val name = b.nameEt.text.toString()

        b.progressBar.visibility = View.VISIBLE
        val authInstance = Fp.authInstance()
        authInstance.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user: FirebaseUser = authInstance.currentUser!!
                val changeRequest: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                val person = User(name, user.uid)
                Fp.userPath().setValue(person)
                user.updateProfile(changeRequest).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        requireActivity().toast("$name is now Registered")
                        b.progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_signUpFragment_to_nav_home)
                    }
                }
            } else {
                b.progressBar.visibility = View.GONE
                AlertDialog.Builder(requireContext())
                    .setCancelable(true).setPositiveButton("dismiss") { dialog, _ ->
                        dialog.dismiss()
                    }.setTitle("Sign Up Failed").setMessage(
                        when (it.exception) {
                            null -> "An unknown error occurred"
                            is FirebaseNetworkException -> "Please Connect to the internet"
                            else -> Fp.getErrorString((it.exception as FirebaseAuthException).errorCode)
                        }
                    ).create().show()
            }
        }

    }

    private var passwordWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (charSequence.length < 8) {
                b.etPasswordLayout.error = "Password too Short"
            } else b.etPasswordLayout.error = null
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private var confirmWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (charSequence.toString() != b.etPassword.text.toString()) {
                b.etRetypeLayout.error = "Passwords Don't Match"
            } else b.etPasswordLayout.error = null
        }

        override fun afterTextChanged(editable: Editable) {}
    }
    private var emailWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            b.emailLayout.error = null
        }

        override fun afterTextChanged(editable: Editable) {}
    }
    private var nameWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            b.nameLayout.error = null
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    override fun onResume() {
        super.onResume()
        b.etPassword.addTextChangedListener(passwordWatcher)
        b.etRetype.addTextChangedListener(confirmWatcher)
        b.nameEt.addTextChangedListener(nameWatcher)
        b.emailEt.addTextChangedListener(emailWatcher)
    }

    override fun onPause() {
        super.onPause()
        b.etPassword.removeTextChangedListener(passwordWatcher)
        b.etRetype.removeTextChangedListener(confirmWatcher)
        b.nameEt.removeTextChangedListener(nameWatcher)
        b.emailEt.removeTextChangedListener(emailWatcher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInOneTap =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == ONE_TAP) {
                    try {
                        val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
                        val idToken = credential.googleIdToken
                        when {
                            idToken != null -> {
                                val firebaseCredential =
                                    GoogleAuthProvider.getCredential(idToken, null)
                                auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = auth.currentUser
                                            val person = user!!.displayName?.let { name -> User(name, user.uid) }
                                            Fp.userPath().setValue(person)
                                            findNavController().navigate(R.id.action_signUpFragment_to_nav_home)
                                        } else {
                                            b.progressBar.visibility = View.GONE
                                            (AlertDialog.Builder(requireContext())
                                                .setCancelable(true)).setPositiveButton("dismiss") { dialog, _ ->
                                                    dialog.dismiss()
                                                }
                                                .setTitle("Sign Up Failed").setMessage(
                                                    when (task.exception) {
                                                        null -> "An unknown error occurred"
                                                        is FirebaseNetworkException -> "Please Connect to the internet"
                                                        else -> Fp.getErrorString((task.exception as FirebaseAuthException).errorCode)
                                                    }
                                                ).create().show()
                                        }
                                    }

                            }
                            else -> {
                                requireContext().toast("No ID token! ty again")
                            }
                        }
                    } catch (e: ApiException) {
                        when (e.statusCode) {
                            CommonStatusCodes.CANCELED -> {
                                requireContext().toast("One-tap dialog was canceled.")
                                // Don't re-prompt the user.
                                showOneTapUI = false
                            }
                            CommonStatusCodes.NETWORK_ERROR -> {
                                requireContext().toast("One-tap encountered a network error.")
                                // Try again or just ignore.
                            }
                            else -> {
                                requireContext().toast("Couldn't find any credentials please manually sign up.")
                            }
                        }
                    }


                }
                b.progressBar.visibility = View.GONE

            }
    }
}